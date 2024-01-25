package savogineros.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import savogineros.entities.Dispositivo;
import savogineros.entities.Utente;
import savogineros.payloadsDTO.Dispositivo.DTOResponseDispositivoLatoDispositivo;
import savogineros.payloadsDTO.Dispositivo.NewDispositivoRequestDTO;
import savogineros.payloadsDTO.Utente.DTOResponseUtenteLatoDispositivo;
import savogineros.services.DispositiviService;

import java.util.UUID;

@RestController
@RequestMapping("/dispositivi")
public class DispositiviController {
    @Autowired
    private DispositiviService dispositiviService;

    //----------------------------------------------------------------------------------

    // GET - tutti i dispositivi
    // URL http://localhost:3001/dispositivi
    @GetMapping("")
    public Page<DTOResponseDispositivoLatoDispositivo> getDispositivi(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {
        return dispositiviService.getAllDispositivi(page,size, sort);
    }

    // POST - Aggiungi un dispositivo
    // URL http://localhost:3001/dispositivi     + (body)
    @PostMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public DTOResponseDispositivoLatoDispositivo creaDispositivo(@RequestBody NewDispositivoRequestDTO richiestaDispositivo) {

        Dispositivo dispositivo = dispositiviService.salvaDispositivo(richiestaDispositivo);

        DTOResponseUtenteLatoDispositivo responseUtenteLatoDispositivo;

        if (dispositivo.getUtente() != null) {
         responseUtenteLatoDispositivo = new DTOResponseUtenteLatoDispositivo(
                dispositivo.getUtente().getId(),
                dispositivo.getUtente().getUserName(),
                dispositivo.getUtente().getRole());
         return new DTOResponseDispositivoLatoDispositivo(
                dispositivo.getId(),
                dispositivo.getStatoDispositivo(),
                responseUtenteLatoDispositivo);
        } else {

            return new DTOResponseDispositivoLatoDispositivo(
                    dispositivo.getId(),
                    dispositivo.getStatoDispositivo(),
                     null);
        }
    }

    // GET - Ricerca specifico Dispositivo
    // URL http://localhost:3001/dispositivi/{idDispositivo}
    @GetMapping("/{idDispositivo}")
    public Dispositivo getDispositivoById(@PathVariable UUID idDispositivo) {  // se non sbaglio il nome della variabile DEVE essere
        return dispositiviService.getDispositivoById(idDispositivo);           // uguale a quella dell' URL. Quindi anche nella PUT
    }

    // PUT - Modifica Dispositivo dato id e payload
    // URL http://localhost:3001/dispositivi/{idDispositivo}     + (body)
    @PutMapping("/{idDispositivo}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public DTOResponseDispositivoLatoDispositivo modificaUtente(@PathVariable UUID idDispositivo, @RequestBody NewDispositivoRequestDTO richiestaDispositivo) {
        // Mi vengono in mente due modi, o mi inietto in questa classe la repo UtentiDao così da utilizzare un'altra save()
        // Oppure mi faccio un altro metodo specifico per gli Update nel service
        // Seguiamo la seconda opzione
        Dispositivo dispositivo = dispositiviService.modificaDispositivo(idDispositivo,richiestaDispositivo);

        DTOResponseUtenteLatoDispositivo responseUtente = null;

        if (dispositivo.getUtente() != null) {

            responseUtente = new DTOResponseUtenteLatoDispositivo(
                dispositivo.getUtente().getId(),
                dispositivo.getUtente().getUserName(), // non può funzionare, dovrei richiamarmi anche qui UtentiService
                dispositivo.getUtente().getRole());    // non può funzionare, dovrei richiamarmi anche qui UtentiService
        }

        return new DTOResponseDispositivoLatoDispositivo(
                dispositivo.getId(),
                dispositivo.getStatoDispositivo(),
                responseUtente);
    }

    // DELETE - Elimina un Dispositivo dato l'id
    // URL http://localhost:3001/dispositivi/{idDispositivo}
    @DeleteMapping("{idDispositivo}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaDispositivo(@PathVariable UUID idDispositivo) {
        dispositiviService.eliminaDispositivo(idDispositivo);
    }



}
