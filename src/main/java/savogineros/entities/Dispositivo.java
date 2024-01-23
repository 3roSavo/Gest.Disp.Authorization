package savogineros.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
public class Dispositivo {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private StatoDispositivo statoDispositivo;

    @ManyToOne
    //@JsonIgnore
    private Utente utente;


    public Dispositivo(StatoDispositivo statoDispositivo) {
        this.statoDispositivo = statoDispositivo;
    }
}
