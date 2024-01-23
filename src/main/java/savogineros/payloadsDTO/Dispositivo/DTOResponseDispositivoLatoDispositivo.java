package savogineros.payloadsDTO.Dispositivo;

import savogineros.entities.StatoDispositivo;
import savogineros.payloadsDTO.Utente.DTOResponseUtenteLatoDispositivo;

import java.util.UUID;
public record DTOResponseDispositivoLatoDispositivo(UUID id, StatoDispositivo statoDispositivo, DTOResponseUtenteLatoDispositivo utente_associato) {
}
