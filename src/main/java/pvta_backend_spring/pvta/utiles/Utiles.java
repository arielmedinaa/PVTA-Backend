package pvta_backend_spring.pvta.utiles;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;
import pvta_backend_spring.pvta.entities.Usuario;

import java.nio.charset.StandardCharsets;

@Component
public class Utiles {

    private static final String SECRET_KEY = "alquetodosenelbarriollamanelsenseivossabeis";

    public Usuario leerToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

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
    }


}
