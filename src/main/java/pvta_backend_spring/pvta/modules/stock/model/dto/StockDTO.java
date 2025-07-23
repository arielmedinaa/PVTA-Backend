package pvta_backend_spring.pvta.modules.stock.model.dto;

import java.sql.Timestamp;

public record StockDTO(
        long id,
        long costo,
        long total,
        int linea,
        String origen,
        String deposito,
        String depositodes,
        String codigo,
        Timestamp fecha,
        double cantidad,
        double saldo,
        double costome,
        double totalme,
        String moneda
) {
}
