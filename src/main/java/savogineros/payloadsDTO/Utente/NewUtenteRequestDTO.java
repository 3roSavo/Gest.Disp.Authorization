package savogineros.payloadsDTO.Utente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import savogineros.entities.Dispositivo;

import java.util.List;

public record NewUtenteRequestDTO(
        @NotEmpty(message = "Lo user name è un campo obbligatorio")
        @Size(min = 3, max = 20, message = "Lo user name deve essere compreso tra 3 e 20 caratteri")
        String userName,
        @NotEmpty(message = "Il nome è un campo obbligatorio")
        @Size(min = 3, max = 20, message = "Il nome deve essere compreso tra 3 e 20 caratteri")
        String nome,
        @NotEmpty(message = "Il cognome è un campo obbligatorio")
        @Size(min = 3, max = 20, message = "Il cognome deve essere compreso tra 3 e 20 caratteri")
        String cognome,
        @NotEmpty(message = "La email è un campo obbligatorio")
        @Email(message = "Indirizzo e-mail non valido")
        String email,
        @NotEmpty
        @Size(min = 8, max = 30, message = "La password deve essere compresa tra 8 e 30 caratteri")
        String password,
        List<Dispositivo> listaDispositivi) {
}
// I record dispongono in automatico di tutti i getter, MA non dei setter
// Dato che non abbiamo i setter il costruttore con tutti gli argomenti è incluso, quindi non devo
// crearmi a mano l'oggetto nel controller
// Ogni parametro del record corrisponde in automatico a un attributo
