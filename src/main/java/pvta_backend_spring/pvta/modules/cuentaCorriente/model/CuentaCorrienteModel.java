package pvta_backend_spring.pvta.modules.cuentaCorriente.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CuentaCorrienteModel {
    private long id;
    private long entidadId; // Cliente o Proveedor ID
    private String tipoEntidad; // "CLIENTE" o "PROVEEDOR"
    private String tipoMovimiento; // "DEBE" o "HABER"
    private double monto;
    private String concepto;
    private long facturaId; // Referencia a factura (opcional)
    private long comprobanteId; // Referencia a comprobante de pago (opcional)
    private Timestamp fecha;
    private long usuarioId;
    private boolean activo;
}