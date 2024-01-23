package savogineros.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Utente {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    private String userName;

    private String nome;

    private String cognome;

    private String email;

    private String password;

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

}
