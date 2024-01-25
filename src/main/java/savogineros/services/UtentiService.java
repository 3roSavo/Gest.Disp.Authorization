package savogineros.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import savogineros.entities.Dispositivo;
import savogineros.entities.Role;
import savogineros.entities.StatoDispositivo;
import savogineros.entities.Utente;
import savogineros.exceptions.NotFoundException;
import savogineros.payloadsDTO.Dispositivo.DTOResponseDispositivoLatoUtente;
import savogineros.payloadsDTO.Dispositivo.NewDispositivoRequestDTO;
import savogineros.payloadsDTO.Utente.NewUtenteAdminRequestDTO;
import savogineros.payloadsDTO.Utente.NewUtenteRequestDTO;
import savogineros.payloadsDTO.Utente.DTOResponseUtenteLatoUtente;
import savogineros.repositories.DispositiviDAO;
import savogineros.repositories.UtentiDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UtentiService {
    @Autowired
    private UtentiDAO utentiDAO;
    @Autowired // Questo service lo userò per settare la lista di dispositivi nell'utente
    private DispositiviService dispositiviService;

    // GET -> getAllUsers-------------------------------------------------------------------------------------
    public Page<DTOResponseUtenteLatoUtente> getAllUsers(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Utente> listaUtenti = utentiDAO.findAll(pageable);
        return listaUtenti.map(utente -> {
        List<DTOResponseDispositivoLatoUtente> listaDispositivi = new ArrayList<>();
            utente.getListaDispositivi().forEach(dispositivo ->
                    listaDispositivi.add(new DTOResponseDispositivoLatoUtente(dispositivo.getId(),
                            dispositivo.getStatoDispositivo())));
            return new DTOResponseUtenteLatoUtente(
                    utente.getId(),
                    utente.getUserName(),
                    utente.getNome(),
                    utente.getCognome(),
                    utente.getEmail(),
                    utente.getPassword(),
                    utente.getRole(),
                    listaDispositivi
            );
            // Finalmente abbiamo usato due DTO per personalizzare la risposta in JSON senza creare StackOverflow
            // Ho modificato col mio UtenteDTO la List<Dispositivo> in List<DTOResponseDispositivoLatoUtente> che tralascia l'utente
        });
    }

    // POST -> save--------------------------------------------------------------------------------------------
    public Utente salvaUtente(NewUtenteRequestDTO utenteRequestDTO) {

        List<DTOResponseDispositivoLatoUtente> listaDispositivi = new ArrayList<>();

        Utente utente = new Utente(
                utenteRequestDTO.userName(),
                utenteRequestDTO.nome(),
                utenteRequestDTO.cognome(),
                utenteRequestDTO.email(),
                utenteRequestDTO.password()
        );
        utente.setRole(Role.USER); // Alla creazione di un utente sarà in automatico assegnato il ruolo di USER, sarà un eventuale ADMIN a cambiare questo ruolo
        // Se ho compreso bene sarebbe da creare col dao utente l'oggetto nel DB e poi associarci la lista di dispositivi

        //utente.setListaDispositivi(utenteRequestDTO.listaDispositivi());
        //utentiDAO.save(utente);
            utente.setListaDispositivi(new ArrayList<>());

        // il costruttore non accetta la lista, quindi la setto dopo la creazione,
        // prima però devo assicurarmi che ci siano o meno elementi
        if (!utenteRequestDTO.listaDispositivi().isEmpty()) {
            utentiDAO.save(utente);

            utenteRequestDTO.listaDispositivi().forEach(dispositivo -> {
                Dispositivo dispositivoTrovato = dispositiviService.getDispositivoById(dispositivo.getId());
                dispositivoTrovato.setUtente(utente);

                NewDispositivoRequestDTO dispositivoRequestDTO = new NewDispositivoRequestDTO(
                        dispositivoTrovato.getStatoDispositivo(),
                        dispositivoTrovato.getUtente());

                dispositiviService.modificaDispositivo(dispositivoTrovato.getId(), dispositivoRequestDTO);

                //listaDispositivi.add(new DTOResponseDispositivoLatoUtente(dispositivo1.getId(), dispositivo1.getStatoDispositivo()));
            });
        }
        return utentiDAO.save(utente);
    }

    // GET Ricerca specifico utente con id------------------------------------------------------------------------
    public Utente getUtenteById(UUID idUtente) {

        /*Optional<Utente> utente = utentiDAO.findById(idUtente);
        if (utente.isPresent()) {
            return  utente.get();
        } else {
            throw new NotFoundException(idUtente);
        }*/
        // OPPURE SEMPLIFICATA
        return utentiDAO.findById(idUtente).orElseThrow(() -> new NotFoundException(idUtente));

    }

    // PUT Modifica un Utente, dato id e corpo della richiesta-------------------------------------------------------
    // Questa metodo è per gli USER, dato che nel payload manca il role
    public Utente modificaUtentePerRoleUtente(UUID idUtente, NewUtenteRequestDTO richiestaUtente) {
        Utente utente = getUtenteById(idUtente);

        utente.setUserName(richiestaUtente.userName());
        utente.setNome(richiestaUtente.nome());
        utente.setCognome(richiestaUtente.cognome());
        utente.setEmail(richiestaUtente.email());
        utente.setPassword(richiestaUtente.password());


        // Penso che prima di modificare la lista di dispositivi andrebbe impostato a null ogni
        // elemento della lista, così da separare ogni utente dai dispositivi associati
        // per poi riempirla coi dispositivi passati con la request,
        // sempre attraverso il dao dei dispositivi, POI puoi procedere all'aggiunta o alla rimozione
        if (!utente.getListaDispositivi().isEmpty()) {
            utente.getListaDispositivi().forEach(dispositivo -> {
                dispositiviService.modificaDispositivo(
                        dispositivo.getId(),
                        new NewDispositivoRequestDTO(dispositivo.getStatoDispositivo(),null));
            });
        }

        //utente.setListaDispositivi(richiestaUtente.listaDispositivi());

        // Innanzitutto controlliamo se la lista dispositivi della request è vuota o meno, nel caso sia vuota non eseguiamo logica
        if (!richiestaUtente.listaDispositivi().isEmpty()) {
            richiestaUtente.listaDispositivi().forEach( dispositivo -> {

                // Controlliamo poi che ogni dispositivo esista effettivamente nel DB
                Dispositivo dispositivo111 = dispositiviService.getDispositivoById(dispositivo.getId());

                // Per modificare un dispositivo il mio metodo "modificaDispositivo" richiede come secondo parametro un oggetto NewDispositivoRequestDTO, non un Dispositivo
                NewDispositivoRequestDTO dispositivoRequestDTO = new NewDispositivoRequestDTO(StatoDispositivo.Assegnato, utente);

                dispositiviService.modificaDispositivo(dispositivo.getId(), dispositivoRequestDTO);

            });
        }
        return utentiDAO.save(utente);

    }

    public Utente modificaUtentePerAdmin(UUID idUtente, NewUtenteAdminRequestDTO richiestaUtente) {
        Utente utente = getUtenteById(idUtente);

        utente.setUserName(richiestaUtente.userName());
        utente.setNome(richiestaUtente.nome());
        utente.setCognome(richiestaUtente.cognome());
        utente.setEmail(richiestaUtente.email());
        utente.setPassword(richiestaUtente.password());
        utente.setRole(richiestaUtente.role());


        // Penso che prima di modificare la lista di dispositivi andrebbe impostato a null ogni
        // elemento della lista, così da separare ogni utente dai dispositivi associati
        // per poi riempirla coi dispositivi passati con la request,
        // sempre attraverso il dao dei dispositivi, POI puoi procedere all'aggiunta o alla rimozione
        if (!utente.getListaDispositivi().isEmpty()) {
            utente.getListaDispositivi().forEach(dispositivo -> {
                dispositiviService.modificaDispositivo(
                        dispositivo.getId(),
                        new NewDispositivoRequestDTO(dispositivo.getStatoDispositivo(),null));
            });
        }

        //utente.setListaDispositivi(richiestaUtente.listaDispositivi());

        // Innanzitutto controlliamo se la lista dispositivi della request è vuota o meno, nel caso sia vuota non eseguiamo logica
        if (!richiestaUtente.listaDispositivi().isEmpty()) {
            richiestaUtente.listaDispositivi().forEach( dispositivo -> {

                // Controlliamo poi che ogni dispositivo esista effettivamente nel DB
                Dispositivo dispositivo111 = dispositiviService.getDispositivoById(dispositivo.getId());

                // Per modificare un dispositivo il mio metodo "modificaDispositivo" richiede come secondo parametro un oggetto NewDispositivoRequestDTO, non un Dispositivo
                NewDispositivoRequestDTO dispositivoRequestDTO = new NewDispositivoRequestDTO(StatoDispositivo.Assegnato, utente);

                dispositiviService.modificaDispositivo(dispositivo.getId(), dispositivoRequestDTO);

            });
        }
        return utentiDAO.save(utente);

    }

    // DELETE Elimina utente, dato id
    public void eliminaUtente(UUID idUtente) {
        Utente utente = utentiDAO.findById(idUtente).orElseThrow(() -> new NotFoundException(idUtente));
        utentiDAO.delete(utente);
    }

    // findByEmail  (mi son fatto la derived query nel DAO) che utilizzerò per la login
    public Utente findByEmail(String email) {
        return utentiDAO.findByEmail(email).orElseThrow(() -> new NotFoundException("Utente con email " + email + " non trovato"));
    }
}