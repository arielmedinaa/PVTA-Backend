package pvta_backend_spring.pvta.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Usuario {
    private String usulic;       // sub
    private String email;
    private String usuip;        // db_host
    private String usupuerto;    // db_port
    private String passbd;       // db_password
    private String usubd;        // database_name
    private String usuariobd;
}
