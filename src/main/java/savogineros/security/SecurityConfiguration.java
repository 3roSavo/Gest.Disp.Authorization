package savogineros.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Obbligatoria se vogliamo dichiarare su ogni singolo endpoint i permessi di accesso in base al ruolo tramite annotazioni @PreAuthorize
public class SecurityConfiguration {
    @Autowired
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // ----------------------------------Disabilitiamo alcuni comportamenti di default-----------------------------------------
        httpSecurity.formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer.disable());
        httpSecurity.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        httpSecurity.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());



        // -------------------------------------Aggiungiamo filtri custom-------------------------------------------------
        httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        // jwtAuthenticationFilter dispone anche di un metodo per bypassare il filtro nel caso volessimo fare delle operazioni senza inserire un token
        // tipo la login dove otteniamo appunto il token o anche la post per creare un nuovo utente. Quindi cambiamo la path /utenti in /authentication



        // ------------------------------Aggiungiamo/Rimuoviamo regole di protezione su singoli endpoint--------------------------------
        // in maniera che venga o non venga concessa l'autorizzazione per accedervi
        httpSecurity.authorizeHttpRequests(request -> request.requestMatchers("/**").permitAll());
        //  tutte le richieste cui l'url seguono questo pattern saranno permesse. Questo pattern si riferisce a tutte le richieste dopo lo slash /**
        return httpSecurity.build();
    }
}
