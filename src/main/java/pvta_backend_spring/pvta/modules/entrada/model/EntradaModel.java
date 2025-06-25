package pvta_backend_spring.pvta.modules.entrada.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EntradaModel {
    long id;
    long idSucursal;
    String numero;
    String moneda;
    String fechaCreacion;
    String fechaActualizacion;
    long cotizacion;
    String origen = "ES";
}
