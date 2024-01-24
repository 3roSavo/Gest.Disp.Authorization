package savogineros.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import savogineros.entities.Utente;
import savogineros.exceptions.BadRequestException;
import savogineros.payloadsDTO.Dispositivo.DTOResponseDispositivoLatoUtente;
import savogineros.payloadsDTO.Utente.NewUtenteRequestDTO;
import savogineros.payloadsDTO.Utente.DTOResponseUtenteLatoUtente;
import savogineros.services.UtentiService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/utenti")
public class UtentiController {
    @Autowired
    private UtentiService utentiService;
//----------------------------------------------------------------------------------


    // --------------------- /me endpoints -----------------------
    @GetMapping("/me")
    public DTOResponseUtenteLatoUtente getProfile(@AuthenticationPrincipal Utente currentUser) { // puoi anche ritornarti i tuoi payload, MA devi passargli o Utente o UserDetails
        // con questo endpoint, se sono autenticato, utilizzando l'annotazione AuthenticationPrincipal e l'interfaccia UserDetails o la classe dove la implementi (Utente),
        // il ritorno è il proprio profilo, ricavato dal token utilizzato per l'autenticazione.

        List<DTOResponseDispositivoLatoUtente> listaDispositivi = currentUser.getListaDispositivi().stream().map(dispositivo ->
                new DTOResponseDispositivoLatoUtente(dispositivo.getId(), dispositivo.getStatoDispositivo())).toList();

        return new DTOResponseUtenteLatoUtente(
                currentUser.getId(),
                currentUser.getUserName(),
                currentUser.getNome(),
                currentUser.getCognome(),
                currentUser.getEmail(),
                currentUser.getPassword(),
                currentUser.getRole(),
                listaDispositivi
        );
    }
    @PutMapping("/me")
    public DTOResponseUtenteLatoUtente getMeAndUpdate(@AuthenticationPrincipal Utente currentUser, @RequestBody NewUtenteRequestDTO richiestaUtente) {
        UUID id = currentUser.getId();
        return utentiService.modificaUtente(id, richiestaUtente); // controlla se funziona
    }
    @DeleteMapping("/me")
    public void getMeAndDelete(@AuthenticationPrincipal Utente currentUser) {
        utentiService.eliminaUtente(currentUser.getId()); // controlla se funziona
    }






    // GET - tutti gli utenti
    // URL http://localhost:3001/utenti
    @GetMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")  // Solo gli admin possono leggere l'elenco completo degli utenti
    public Page<DTOResponseUtenteLatoUtente> getUtenti(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "userName") String sort) {
        return utentiService.getAllUsers(page,size, sort);
    }



    // ---------------  POST SPOSTATA IN AuthenticationController----------------
    // prima il path era vuoto ma ora per evitare che il filtro JWTAuthenticationFilter mi chieda il token
    // spostiamo la Post per creare un utente in AuthenticationController dove la path è /authentication  ---@RequestMapping("/authentication")---
    // questo perché abbiamo aggiunto nel filtro JWTAuthenticationFilter un metodo per bypassarlo per rendere disponibile
    // la login e la creazione di un utente senza il token. Nella login non ho il token, è proprio essa che me la da!




    // GET - Ricerca specifico Utente
    // URL http://localhost:3001/utenti/{idUtente}
    // LA COMMENTIAMO E LA TRASFORMIAMO IN /me E QUINDI ACCEDIAMO ALLE INFORMAZIONI ATTRAVERSO L'AUTENTICAZIONE (TOKEN)

    /*@GetMapping("/{idUtente}")
    public DTOResponseUtenteLatoUtente getUtenteById(@PathVariable UUID idUtente) {  // se non sbaglio il nome della variabile DEVE essere uguale a quella dell' URL. Quindi anche nella PUT
        Utente utente = utentiService.getUtenteById(idUtente);
        List<DTOResponseDispositivoLatoUtente> dtoResponseDispositivoLatoUtente = utente.getListaDispositivi().stream().map(dispositivo ->
                new DTOResponseDispositivoLatoUtente(dispositivo.getId(),dispositivo.getStatoDispositivo())).toList();

        return new DTOResponseUtenteLatoUtente(
                utente.getId(),
                utente.getUserName(),
                utente.getNome(),
                utente.getCognome(),
                utente.getEmail(),
                utente.getPassword(),
                utente.getRole(),
                dtoResponseDispositivoLatoUtente
        );
    }*/

    // PUT - Modifica Utente dato id e payload
    // URL http://localhost:3001/utenti/{idUtente}     + (body)
    @PutMapping("/{idUtente}")
    @PreAuthorize("hasAuthority('ADMIN')")  // Solo gli admin possono modificare un utente
    public DTOResponseUtenteLatoUtente modificaUtente(@PathVariable UUID idUtente, @RequestBody NewUtenteRequestDTO richiestaUtente) {
        // Mi vengono in mente due modi, o mi inietto in questa classe la repo UtentiDao così da utilizzare un'altra save()
        // Oppure mi faccio un altro metodo specifico per gli Update nel service
        // Seguiamo la seconda opzione
        return utentiService.modificaUtente(idUtente,richiestaUtente);
    }

    // DELETE - Elimina un utente dato l'id
    // URL http://localhost:3001/utenti/{idUtente}
    @DeleteMapping("{idUtente}")
    @PreAuthorize("hasAuthority('ADMIN')")  // Solo gli admin possono cancellare un utente
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaUtente(@PathVariable UUID idUtente) {
        utentiService.eliminaUtente(idUtente);
    }





}



















