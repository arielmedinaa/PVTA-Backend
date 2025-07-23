package pvta_backend_spring.pvta.modules.stock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StockModel {
    long id;
    long costo;
    long total;
    int linea;
    String origen;
    String deposito;
    String depositodes;
    String codigo;
    Timestamp fecha;
    double cantidad, saldo, costome, totalme;
    String moneda;
}
