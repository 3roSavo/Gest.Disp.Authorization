package savogineros.payloadsDTO.Dispositivo;

import savogineros.entities.StatoDispositivo;
import savogineros.entities.Utente;

public record NewDispositivoRequestDTO(StatoDispositivo statoDispositivo, Utente utente) {
}
