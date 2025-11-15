package pvta_backend_spring.pvta.utiles;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import pvta_backend_spring.pvta.entities.Usuario;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class Utiles {

    private static final String SECRET_KEY = "alquetodosenelbarriollamanelsenseivossabeis";

    public Usuario leerToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();

            Usuario usuario = new Usuario();
            usuario.setUsulic(claims.get("sub", String.class));
            usuario.setEmail(claims.get("email", String.class));
            usuario.setUsuip(claims.get("db_host", String.class));
            usuario.setUsupuerto(claims.get("db_port", String.class));
            usuario.setPassbd(claims.get("db_password", String.class));
            usuario.setUsubd(claims.get("database_name", String.class));
            usuario.setUsuariobd("postgres");

            return usuario;
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido", e);
        }
    }

    public String horaParaguayaInsert() {
        return ZonedDateTime.now(ZoneId.of("America/Asuncion"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
