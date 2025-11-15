package pvta_backend_spring.pvta.modules.caja.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CajaModel {
    private Long id;
    private Integer idDeposito;
    private Integer idSucursal;
    private Integer usuarioId;
    private String nombre;
    private Boolean activo;
}
