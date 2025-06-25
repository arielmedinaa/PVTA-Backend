package pvta_backend_spring.pvta.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Usuario {
    private String usulic;
    private String email;
    private String usuip;
    private String usupuerto;
    private String passbd;
    private String usubd;
    private String usuariobd;
}
