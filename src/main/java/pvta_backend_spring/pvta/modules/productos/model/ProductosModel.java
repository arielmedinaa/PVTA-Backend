package pvta_backend_spring.pvta.modules.productos.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Builder
public class ProductosModel {
    long id;
    String codigo;
    String nombre;
    String descripcion;
    String familia;
    String marca;
    double impuesto;
    List<PrecioModel> precio;
    boolean stock;
    //List<Imagenes> img;
    String unidadMed;
    //List<DepositoProducto> depositoProducto;
    double cantidad;
    String promocion;
    String proveedor;
    String tipodescuento;
    long valordescuento;
    int porcentajedescuento;
    Timestamp fecini;
    Timestamp fecfin;
    String tipoProducto;
    double cantstock;
    String nomenclatura;
}
