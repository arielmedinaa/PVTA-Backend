package pvta_backend_spring.pvta.modules.cuentaCorriente.model.dto;

import java.sql.Timestamp;

public record CuentaCorrienteDTO(
    Long id,
    Long entidadId,
    String tipoEntidad,
    String tipoMovimiento,
    Double monto,
    String concepto,
    Long facturaId,
    Long comprobanteId,
    Timestamp fecha,
    String usuarioLic
) {}
