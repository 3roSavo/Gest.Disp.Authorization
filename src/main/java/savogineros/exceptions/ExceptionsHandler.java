package savogineros.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import savogineros.payloadsDTO.Errors.ErrorsResponseDTO;

import java.util.Date;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(BadRequestException.class)
    // Indico che questo metodo gestirà le eccezioni di tipo BadRequestException
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorsResponseDTO handleBadRequest(BadRequestException exception) {
        return new ErrorsResponseDTO(exception.getMessage(), new Date());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
    public ErrorsResponseDTO handleUnauthorizedRequest(UnauthorizedException exception) {
        return new ErrorsResponseDTO(exception.getMessage(), new Date());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorsResponseDTO handleAccessDenied(AccessDeniedException exception) {
        return new ErrorsResponseDTO("Il tuo ruolo non permette di accedere a questa funzionalità!", new Date());
    }

    /*@ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    public ErrorsResponseDTO handleGenericError(Exception exception) {
        return new ErrorsResponseDTO("Problema lato server... verrà risolto quanto prima", new Date());
    }*/

}
