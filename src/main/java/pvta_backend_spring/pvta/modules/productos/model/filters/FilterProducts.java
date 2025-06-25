package pvta_backend_spring.pvta.modules.productos.model.filters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import pvta_backend_spring.pvta.entities.filters.GlobalFilters;

@AllArgsConstructor
@NoArgsConstructor
public class FilterProducts extends GlobalFilters {
    String codigo;
    String nombreProducto;
    String categoria;
    String precio;
    String conStock;
    String sinStock;
    String pocoStock = "false";
}
