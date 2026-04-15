package pvta_backend_spring.pvta.modules.factura.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FacturaModel {
    private long id;
    private String numeroFactura; // Formato: 001-001-000001
    private String documento;
    private long establecimiento; // 001
    private long puntoExpedicion; // 001
    private long secuencial; // 000001
    private long clienteId;
    private long usuarioId; // Usuario que crea la factura
    private long cajaId; // Caja asociada
    private Timestamp fechaEmision;
    private Timestamp fechaVencimiento;
    private String moneda;
    private double subtotal;
    private double impuestos;
    private double total;
    private String estado; // EMITIDA, PAGADA, ANULADA
    private boolean activo;
}