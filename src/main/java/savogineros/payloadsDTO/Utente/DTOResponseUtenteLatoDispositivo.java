package savogineros.payloadsDTO.Utente;

import savogineros.entities.Role;

import java.util.UUID;

public record DTOResponseUtenteLatoDispositivo(UUID id, String userName, Role role) {
}
