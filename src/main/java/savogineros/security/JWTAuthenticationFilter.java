package savogineros.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import savogineros.entities.Utente;
import savogineros.exceptions.UnauthorizedException;
import savogineros.payloadsDTO.Utente.DTOResponseUtenteLatoUtente;
import savogineros.services.UtentiService;

import java.io.IOException;
import java.util.UUID;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    // Creo un filtro che andrò ad aggiungere alla SecurityFilterChain nella classe SecurityConfiguration
    @Autowired
    private JWTTools jwtTools;
    @Autowired
    private UtentiService utentiService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    // doFilterInternal è il metodo di questo filtro che verrà eseguito per ogni richiesta che richieda di essere autenticati, e dovrà:

        // 1. Verificare se la richiesta contiene un Authorization Header ed eventualmente estrarre il token da esso
        String authHeader = request.getHeader("Authorization"); // "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjOTY0YjQwYy1iMzliLTQ4NzYtYTBkYi1jNDA4Yjc5ZDlhNDUiLCJpYXQiOjE3MDU5MjIyMjYsImV4cCI6MTcwNjUyNzAyNn0.stxc0Bko-lN8ej_Yp8hpjLTQmmlnzqJChrDQ7XkAR0Q"


        // Devo controllare che ci sia l'autorizzazione e che cominci con Bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Per favore metti il token nell' Authorization header oppure controlla che cominci per Bearer");
        } else {
            // Devo estrarmi il token tagliando via "Bearer "
            // si può utilizzare replace(), substring(), splice()?
            String accessToken = authHeader.substring(7);

            // 2. Verifichiamo se il token è scaduto o se è stato manipolato (verifica signature)
            jwtTools.verifyToken(accessToken);

            // 3. Se è tutto ok:

            // 3.1 Cerco l'utente a DB (l'id sta all'interno del token)
            String id = jwtTools.extractIdFromToken(accessToken);
            DTOResponseUtenteLatoUtente utente = utentiService.getUtenteById(UUID.fromString(id));

            // 3.2 Informo Spring Security che l'utente è autenticato
            // Senza questo passaggio continuerò ad avere 403 forbidden cioè non autorizzato come risposta
            Authentication authentication = new UsernamePasswordAuthenticationToken(utente, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3.3 Possiamo proseguire al prossimo elemento della chain (e prima o poi si arriverà al controller)
            filterChain.doFilter(request, response); // Va al prossimo elemento della catena
        }
    }

    // Disabilito il filtro per le richieste tipo Login
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Questo metodo serve per specificare quando il filtro NON deve entrare in azione
        // Ad esempio tutte le richieste al controller /authorization non devono essere controllate dal filtro
        // /authentication/** vuoldire qualsiasi endpoint su /authentication e anche qualsiasi cosa venga dopo lo slash (tramite **)
        return new AntPathMatcher().match("/authentication/**", request.getServletPath());
    }
}
