package savogineros.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Utente implements UserDetails {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Getter(AccessLevel.NONE)
    private String userName;

    private String nome;

    private String cognome;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "utente")
    //@JsonIgnore
    private List<Dispositivo> listaDispositivi;

    public Utente(String userName, String nome, String cognome, String email, String password) {
        this.userName = userName;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
    }

    public String getUserName() {  // Ho risolto eliminando il getter dall'attributo userName e posizionandolo a mano qui
        return userName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {  // Deve tornare la lista dei ruoli (SimpleGrantedAuthority) dell'utente, nel nostro caso è uno solo
        return List.of(new SimpleGrantedAuthority(this.role.name())); // e lo passiamo come terzo parametro all'oggetto UsernamePasswordAuthenticationToken nel nostro filtro
    }

    @Override
    public String getUsername() { // PROBLEMA, ho un'interferenza col getter creato con lombok che si chiama getUserName()
        return this.email; // Ritorniamo l'email perché nel nostro caso specifico è quella che usiamo al login al posto dello username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
