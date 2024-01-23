package savogineros.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import savogineros.entities.Dispositivo;
import savogineros.entities.Utente;
import savogineros.exceptions.NotFoundException;
import savogineros.payloadsDTO.Dispositivo.DTOResponseDispositivoLatoUtente;
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
    @Autowired // Questo DAO lo userò per settare la lista di dispositivi nell'utente
    private DispositiviDAO dispositiviDAO;

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
                    listaDispositivi
            );
            // Finalmente abbiamo usato due DTO per personalizzare la risposta in JSON senza creare StackOverflow
            // Ho modificato col mio UtenteDTO la List<Dispositivo> in List<DTOResponseDispositivoLatoUtente> che tralascia l'utente
        });
    }

    // POST -> save--------------------------------------------------------------------------------------------
    public DTOResponseUtenteLatoUtente salvaUtente(NewUtenteRequestDTO utenteRequestDTO) {

        List<DTOResponseDispositivoLatoUtente> listaDispositivi = new ArrayList<>();

        Utente utente = new Utente(
                utenteRequestDTO.userName(),
                utenteRequestDTO.nome(),
                utenteRequestDTO.cognome(),
                utenteRequestDTO.email(),
                utenteRequestDTO.password()
        );

        // Se ho compreso bene sarebbe da creare col dao utente l'oggetto nel DB e poi associarci la lista di dispositivi
        utentiDAO.save(utente);

        // il costruttore non accetta la lista, quindi la setto dopo la creazione,
        // prima però devo assicurarmi che ci siano o meno elementi
        utente.setListaDispositivi(new ArrayList<>());
        if (!utenteRequestDTO.listaDispositivi().isEmpty()) {

            utenteRequestDTO.listaDispositivi().forEach(dispositivo -> {
                Dispositivo dispositivo1 = dispositiviDAO.findById(dispositivo.getId()).orElseThrow(() -> new NotFoundException(dispositivo.getId()));
                dispositivo1.setUtente(utente);
                dispositiviDAO.save(dispositivo1);
                listaDispositivi.add(new DTOResponseDispositivoLatoUtente(dispositivo1.getId(), dispositivo1.getStatoDispositivo()));

            });

        }

        return new DTOResponseUtenteLatoUtente(
                utente.getId(),
                utente.getUserName(),
                utente.getNome(),
                utente.getCognome(),
                utente.getEmail(),
                utente.getPassword(),
                listaDispositivi
        );
    }

    // GET Ricerca specifico utente con id------------------------------------------------------------------------
    public DTOResponseUtenteLatoUtente getUtenteById(UUID idUtente) {

        Optional<Utente> utente = utentiDAO.findById(idUtente);
        if (utente.isPresent()) {
            List<DTOResponseDispositivoLatoUtente> responseDispositivo = new ArrayList<>();
            if (!utente.get().getListaDispositivi().isEmpty()) {
                utente.get().getListaDispositivi()
                        .forEach(dispositivo -> responseDispositivo.add(new DTOResponseDispositivoLatoUtente(
                                dispositivo.getId(),
                                dispositivo.getStatoDispositivo())));
            }
        return new DTOResponseUtenteLatoUtente(
                utente.get().getId(),
                utente.get().getUserName(),
                utente.get().getNome(),
                utente.get().getCognome(),
                utente.get().getEmail(),
                utente.get().getPassword(),
                responseDispositivo
        );
        } else {
            throw new NotFoundException(idUtente);
        }
    }

    // PUT Modifica un Utente, dato id e corpo della richiesta-------------------------------------------------------
    public DTOResponseUtenteLatoUtente modificaUtente(UUID idUtente, NewUtenteRequestDTO richiestaUtente) {
        Utente utente = utentiDAO.findById(idUtente).orElseThrow(() -> new NotFoundException(idUtente));
        // Variante senza if, utilizzando la scorciatoia .orElseThrow per lanciare un'eccezione nel caso id non presente nel DB
        utente.setUserName(richiestaUtente.userName());
        utente.setNome(richiestaUtente.nome());
        utente.setCognome(richiestaUtente.cognome());
        utente.setEmail(richiestaUtente.email());

        // Penso che prima di modificare la lista di dispositivi andrebbe impostato a null ogni
        // elemento della lista, così da separare ogni utente dai dispositivi associati
        // sempre attraverso il dao dei dispositivi, POI puoi procedere all'aggiunta o alla rimozione
        if (!utente.getListaDispositivi().isEmpty()) {
            utente.getListaDispositivi().forEach(dispositivo -> {
                dispositivo.setUtente(null);
            });
        }

        List<DTOResponseDispositivoLatoUtente> responseListaDispositivi = new ArrayList<>();
        // mi servirà per crearmi la mia response lista dispositivi all'interno della response utente

        // FORSE IL PROBLEMA È CHE DEVO USARE IL DAO DEL DISPOSITIVO PER SALVARE OGNI DISPOSITIVO NELLA LISTA DISPOSITIVI DELL'UTENTE
        // OK RISOLTO PROPRIO IN QUESTO MODO MA E' IL METODO CORRETTO???

        // Innanzitutto controlliamo se la lista dispositivi è vuota o meno, nel caso sia vuota non eseguiamo logica
        if (!richiestaUtente.listaDispositivi().isEmpty()) {
            richiestaUtente.listaDispositivi().forEach( dispositivo -> {

                // Controlliamo poi che ogni elemento contenga id validi
                Optional<Dispositivo> dispositivoOptional = dispositiviDAO.findById(dispositivo.getId());
                if (dispositivoOptional.isPresent()) {
                    responseListaDispositivi.add(new DTOResponseDispositivoLatoUtente(
                            dispositivo.getId(),
                            dispositivoOptional.get().getStatoDispositivo())); // ricordati che nella request in Json passiamo solo l'id

                    dispositivoOptional.get().setUtente(utente);
                    dispositiviDAO.save(dispositivoOptional.get());
                } else {
                    throw new NotFoundException(dispositivo.getId());
                }
            });
        }

        utente.getListaDispositivi().forEach(dispositivo -> System.out.println(dispositivo));

        utentiDAO.save(utente);
        // ricordati che la save fa da creazione o modifica nel caso trovi già un elemento con lo stesso id

        return new DTOResponseUtenteLatoUtente(
                utente.getId(),
                utente.getUserName(),
                utente.getNome(),
                utente.getCognome(),
                utente.getEmail(),
                utente.getPassword(),
                responseListaDispositivi
        );
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