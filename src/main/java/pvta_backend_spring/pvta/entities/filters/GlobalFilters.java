package pvta_backend_spring.pvta.entities.filters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class GlobalFilters {
    long id;
    String fechaDesde;
    String fechaHasta;
    String sucursal;
    String deposito;
    String ruc, codigo, descripcion;
    String nombre;
    boolean enStock, pocoStock, sinStock;
    long limit;
    long offset;
}
