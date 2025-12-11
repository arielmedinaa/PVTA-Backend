package pvta_backend_spring.pvta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalAdviceException {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex) {
        StackTraceElement[] stackTrace = ex.getStackTrace();
        String location = stackTrace.length > 0 ?
                stackTrace[0].getClassName() + " - " + stackTrace[0].getMethodName() + " (linea " + stackTrace[0].getLineNumber() + ")"
                : "Ubicación no disponible";

        Map<String, Object> response = new HashMap<>();
        response.put("status", ex.getStatusCode().value());
        response.put("error", ex.getReason());
        response.put("details", ex.getLocalizedMessage());
        response.put("linea", location);
        return new ResponseEntity<>(response, ex.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Ocurrió un error inesperado");
        response.put("details", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Object> handlerSqlException(SQLException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());

        if ("23505".equals(ex.getSQLState())) {
            String detail = ex.getMessage();
            String columna = "";
            String valor = "";
            try {
                int start = detail.indexOf("Key (") + 5;
                int end = detail.indexOf(")=", start);
                if (start > 4 && end > start) {
                    columna = detail.substring(start, end);
                }
                int valStart = detail.indexOf("=(", end) + 2;
                int valEnd = detail.indexOf(")", valStart);
                if (valStart > 1 && valEnd > valStart) {
                    valor = detail.substring(valStart, valEnd);
                }
            } catch (Exception ignored) {}

            response.put("error", "Valor duplicado en la base de datos");
            response.put("details", "Ya existe un registro con " + columna + " = " + valor);
        } else {
            response.put("error", "Error con la conexión a la base de datos");
            response.put("details", ex.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
