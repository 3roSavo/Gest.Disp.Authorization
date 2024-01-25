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
import savogineros.payloadsDTO.Dispositivo.DTOResponseDispositivoLatoDispositivo;
import savogineros.payloadsDTO.Dispositivo.NewDispositivoRequestDTO;
import savogineros.payloadsDTO.Utente.DTOResponseUtenteLatoDispositivo;
import savogineros.repositories.DispositiviDAO;
import savogineros.repositories.UtentiDAO;

import java.util.Optional;
import java.util.UUID;

@Service
public class DispositiviService {
    @Autowired
    private DispositiviDAO dispositiviDAO;

    //@Autowired
    //private UtentiService utentiService;
    @Autowired
    private UtentiDAO utentiDAO; // È sbagliato richiamare il DAO qui ma non so come fare

    // GET -> getAllDispositivi
    /*public Page<Dispositivo> getAllDispositivi(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return dispositiviDAO.findAll(pageable);
    }*/

    // Prova con response di tipo DTO
    // Anche qui ho utilizzato due DTO, uno per la visualizzazione del dispositivo
    // e uno per la visualizzazione dell'utente collegato, situato all'interno del primo DTO.
    // Se il lavoro è fatto bene si evita di incorrere in eccezioni di tipo stackOverflow
    public Page<DTOResponseDispositivoLatoDispositivo> getAllDispositivi(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Dispositivo> dispositivoPage = dispositiviDAO.findAll(pageable);
        return dispositivoPage.map(
                dispositivo -> {
                    DTOResponseUtenteLatoDispositivo utenteAssociato;
                    if (dispositivo.getUtente() != null) {
                        utenteAssociato = new DTOResponseUtenteLatoDispositivo(
                    dispositivo.getUtente().getId(),
                    dispositivo.getUtente().getUserName(),
                        dispositivo.getUtente().getRole());
                    } else {
                        utenteAssociato = null;
                    }
                    // Perché se un dispositivo non ha un utente da errore, quindi mettiamo null

            return new DTOResponseDispositivoLatoDispositivo(
                    dispositivo.getId(),
                    dispositivo.getStatoDispositivo(),
                    utenteAssociato
            );
                }
        );
    }

    // POST -> save
    public Dispositivo salvaDispositivo(NewDispositivoRequestDTO richiestaDispositivo) {
        Dispositivo dispositivo = new Dispositivo();

        dispositivo.setStatoDispositivo(richiestaDispositivo.statoDispositivo());


        if (richiestaDispositivo.utente() != null) {
            dispositivo.setUtente(utentiDAO.findById(richiestaDispositivo.utente().getId()).orElseThrow(
                () -> new NotFoundException(richiestaDispositivo.utente().getId())));
        } else {
            dispositivo.setUtente(null);
        }
        return dispositiviDAO.save(dispositivo);
        // salvato sul DB l'oggetto Dispositivo

        /*
        // Passiamo ora alla response JSON
        DTOResponseUtenteLatoDispositivo utenteAssociato = dispositivo.getUtente() != null ?
                new DTOResponseUtenteLatoDispositivo(
                        dispositivo.getUtente().getId(),
                        dispositivo.getUtente().getUserName(),
                        dispositivo.getUtente().getRole()
                )
                : null;

         */
           /*if (dispositivo.getUtente() != null) {
                utenteAssociato = new DTOResponseUtenteLatoDispositivo(
                    dispositivo.getUtente().getId(),
                    dispositivo.getUtente().getUserName()
                );
           } else {
               utenteAssociato = null;
           }*/

           // Nel caso manchi l'utente nella richiesta, esso avrà valore null, senza il controllo causa un errore
        /*
          return new DTOResponseDispositivoLatoDispositivo(
                  dispositivo.getId(),
                  dispositivo.getStatoDispositivo(),
                  utenteAssociato
          );
        // Funziona tutto ma l'userName di risposta non viene visualizzato correttamente
        // la mia ipotesi è che ci sia il solito problema lazy fetch o qualche sincronizzazione ritardata
        */
    }

    // GET Ricerca specifico Dispositivo con id
    public Dispositivo getDispositivoById(UUID idDispositivo) {

        /*Optional<Dispositivo> dispositivo = dispositiviDAO.findById(idDispositivo);
        if (dispositivo.isPresent()) {
            return dispositivo.get();
        } else {
            throw new NotFoundException(idDispositivo);
        }*/
        // Forma abbreviata
        return dispositiviDAO.findById(idDispositivo).orElseThrow( () -> new NotFoundException(idDispositivo));
    }

    // PUT Modifica un Dispositivo, dato id e corpo della richiesta
    public Dispositivo modificaDispositivo(UUID idDispositivo, NewDispositivoRequestDTO richiestaDispositivo) {
        Dispositivo dispositivo = getDispositivoById(idDispositivo);
        Utente utente = utentiDAO.findById(richiestaDispositivo.utente().getId()).orElseThrow( () -> new NotFoundException(richiestaDispositivo.utente().getId()));
        dispositivo.setUtente(richiestaDispositivo.utente());
        return dispositiviDAO.save(dispositivo); // ricordati che la save fa da creazione o modifica nel caso trovi già un elemento con lo stesso id
    }

    // DELETE - Elimina Dispositivo, dato id
    public void eliminaDispositivo(UUID idDispositivo) {
        Dispositivo dispositivo = getDispositivoById(idDispositivo);
        dispositiviDAO.delete(dispositivo);
    }


}
