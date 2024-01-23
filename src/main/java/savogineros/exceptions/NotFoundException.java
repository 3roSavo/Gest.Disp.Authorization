package savogineros.exceptions;

import java.util.UUID;

public class NotFoundException extends RuntimeException{
    public NotFoundException(UUID id) {
        super("Elemento con id " + id + " non trovato!");
    }

    // Facciamo un overload per supportare anche le eccezioni delle email
    public NotFoundException(String message) {
        super(message);
    }

}
