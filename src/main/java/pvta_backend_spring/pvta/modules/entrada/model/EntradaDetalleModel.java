package pvta_backend_spring.pvta.modules.entrada.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EntradaDetalleModel {
    long id;
    long idProducto;
    String codigoProducto;
    long idDeposito;
    int linea;
    int cantidad;
    long costo;
    double costome;
    long total;
    double totalme;
}
