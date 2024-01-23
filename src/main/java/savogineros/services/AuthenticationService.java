package savogineros.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import savogineros.entities.Utente;
import savogineros.exceptions.NotFoundException;
import savogineros.exceptions.UnauthorizedException;
import savogineros.payloadsDTO.Utente.LoginUtenteRequestDTO;
import savogineros.security.JWTTools;

@Service
public class AuthenticationService {
    @Autowired
    private UtentiService utentiService; // lo inietto per usare il dao nel service
    @Autowired
    private JWTTools jwtTools; // inietto anche la classe per creare i token così da richiamare il metodo per la creazione
    public String authenticateUtente(LoginUtenteRequestDTO loginUtente) {

        // 1. Verifichiamo che la mail dell'utente sia presente nel DB
        Utente utente = utentiService.findByEmail(loginUtente.email());

        // 2. In caso la mail ci sia, verifichiamo che la password fornita corrisponda a quella nel DB
        if (utente.getPassword().equals(loginUtente.password())) {


        // 3. Se le credenziali sono corrette generiamo un token JWT e lo ritorniamo

        return jwtTools.createToken(utente); // attraverso l'iniezione della dipendenza invoco il metodo createToken()


        } else {
        // 4. Se le credenziali NON sono corrette --> 401 (Unauthorized)

            throw new UnauthorizedException("Credenziali non valide!");
        }

    }
}
