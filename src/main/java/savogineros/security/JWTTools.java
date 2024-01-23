package savogineros.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import savogineros.entities.Utente;
import savogineros.exceptions.UnauthorizedException;

import java.util.Date;
@Component // Rendo la classe componente per recuperarmi attraverso @Value il valore del segreto
public class JWTTools {
    @Value("${spring.jwt.secret}") // Assegno a secret il valore di spring.jwt.secret
    private String secret;

    // CLASSE PER CREAZIONE TOKEN E SUA VERIFICA, attraverso la libreria JJWT Jackson che mi da il metodo Jwts
    // Qui mi creerò il token per l'utente che ha superato i controlli della mail e password
    // Un token è composto da tre parti: HEADER, PAYLOAD, SIGNATURE
    // Per crearlo dovrò recuperare le sue componenti
    public String createToken(Utente utente){
        String accessToken = Jwts.builder()   // builder -> costruiamo il token
                .subject(String.valueOf(utente.getId()))    // subject --> a chi appartiene il token (id dell'utente)
                // ho utilizzato String.valueOf() per convertire in String l'id che è di tipo UUID
                .issuedAt(new Date(System.currentTimeMillis()))    // Data di emissione  (IAT - Issued At)
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))  // Data di scadenza  (Expiration date)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))   // Firmo il token, con annesso il segreto preso da application properties
                .compact();   // Finalizzo il token
        // ho utilizzato String.valueOf() per convertire in String l'id che è di tipo UUID
        return accessToken;
    }

    // Verifichiamo se un token è scaduto o è stato manipolato
    public void verifyToken(String token){ // Dato un token lancia eccezioni in caso di token manipolati/scaduti
        try {
        Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes())).build().parse(token);
        } catch (Exception exception) {
            throw new UnauthorizedException("Problemi col token, effettua di nuovo il login");
        }
    }

    // Metodo per estrarre l'id di un utente dato il suo token
    public String extractIdFromToken(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

}
