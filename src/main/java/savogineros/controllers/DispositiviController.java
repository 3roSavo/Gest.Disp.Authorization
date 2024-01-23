package savogineros.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import savogineros.entities.Dispositivo;
import savogineros.payloadsDTO.Dispositivo.DTOResponseDispositivoLatoDispositivo;
import savogineros.payloadsDTO.Dispositivo.NewDispositivoRequestDTO;
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
    @ResponseStatus(HttpStatus.CREATED)
    public DTOResponseDispositivoLatoDispositivo creaDispositivo(@RequestBody NewDispositivoRequestDTO richiestaDispositivo) {
        DTOResponseDispositivoLatoDispositivo dispositivo = dispositiviService.salvaDispositivo(richiestaDispositivo);
        return new DTOResponseDispositivoLatoDispositivo(
                dispositivo.id(),
                dispositivo.statoDispositivo(),
                dispositivo.utente_associato()
        );
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
    public Dispositivo modificaUtente(@PathVariable UUID idDispositivo, @RequestBody NewDispositivoRequestDTO richiestaDispositivo) {
        // Mi vengono in mente due modi, o mi inietto in questa classe la repo UtentiDao così da utilizzare un'altra save()
        // Oppure mi faccio un altro metodo specifico per gli Update nel service
        // Seguiamo la seconda opzione
        return dispositiviService.modificaDispositivo(idDispositivo,richiestaDispositivo);
    }

    // DELETE - Elimina un Dispositivo dato l'id
    // URL http://localhost:3001/dispositivi/{idDispositivo}
    @DeleteMapping("{idDispositivo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaDispositivo(@PathVariable UUID idDispositivo) {
        dispositiviService.eliminaDispositivo(idDispositivo);
    }



}
