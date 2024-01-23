package savogineros.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import savogineros.exceptions.BadRequestException;
import savogineros.payloadsDTO.Utente.DTOResponseUtenteLatoUtente;
import savogineros.payloadsDTO.Utente.LoginUtenteRequestDTO;
import savogineros.payloadsDTO.Utente.LoginUtenteResponseDTO;
import savogineros.payloadsDTO.Utente.NewUtenteRequestDTO;
import savogineros.services.AuthenticationService;
import savogineros.services.UtentiService;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    UtentiService utentiService;

    @PostMapping("/login")  // qui otteniamo il token da utilizzare in seguito per fare richieste di vario tipo
    public LoginUtenteResponseDTO login(@RequestBody LoginUtenteRequestDTO loginUtente) {
        String token = authenticationService.authenticateUtente(loginUtente);
        return new LoginUtenteResponseDTO(token);
    }


    // POST - Aggiungi un utente
    // URL http://localhost:3001/authentication     + (body)
    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    public DTOResponseUtenteLatoUtente creaUtente(@RequestBody @Validated NewUtenteRequestDTO utente, BindingResult validation) {
        // Per completare la validazione devo in qualche maniera fare un controllo del tipo: se ci sono errori -> manda risposta con 400 Bad Request
        if (validation.hasErrors()) {
            //System.out.println(validation);

            throw new BadRequestException("Ci sono errori nel payload :" + System.lineSeparator() +
                    validation.getAllErrors().stream()
                            .map(error -> error.getDefaultMessage())
                            .collect(Collectors.joining(System.lineSeparator())));
            // non so bene cosa faccia l'ultima riga ma stampa con successo in json tutti gli errori
        } else {
            return utentiService.salvaUtente(utente);
        }
    }

}
