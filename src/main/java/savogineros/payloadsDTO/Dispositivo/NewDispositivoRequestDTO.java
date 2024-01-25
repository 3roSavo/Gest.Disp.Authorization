package savogineros.payloadsDTO.Dispositivo;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import savogineros.entities.StatoDispositivo;
import savogineros.entities.Utente;

public record NewDispositivoRequestDTO(
        @Enumerated(EnumType.STRING)
        StatoDispositivo statoDispositivo,
        Utente utente) {
}
