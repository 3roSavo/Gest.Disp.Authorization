package savogineros.payloadsDTO.Dispositivo;

import savogineros.entities.StatoDispositivo;

import java.util.UUID;

public record DTOResponseDispositivoLatoUtente(UUID id, StatoDispositivo statoDispositivo) {
}
