package pvta_backend_spring.pvta.modules.productos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PrecioModel {
    long id;
    long productoId;
    long precio;
    long linea;
    boolean activo;
    long iva;
    String tipoPrecio;
    String moneda;
    Timestamp fechaCreacion;
}
