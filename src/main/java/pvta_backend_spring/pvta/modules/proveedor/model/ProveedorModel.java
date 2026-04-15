package pvta_backend_spring.pvta.modules.proveedor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProveedorModel {
    private long id;
    private String codigo;
    private String nombre;
    private String razonSocial;
    private String ruc;
    private String direccion;
    private String telefono;
    private String email;
    private String contacto;
    private Timestamp fechaRegistro;
    private boolean activo;
}