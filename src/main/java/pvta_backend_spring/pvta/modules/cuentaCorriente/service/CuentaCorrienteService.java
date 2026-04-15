package pvta_backend_spring.pvta.modules.cuentaCorriente.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pvta_backend_spring.pvta.connection.ConexionBusiness;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.entities.filters.GlobalFilters;
import pvta_backend_spring.pvta.entities.response.ResponseDTO;
import pvta_backend_spring.pvta.modules.cuentaCorriente.model.CuentaCorrienteModel;
import pvta_backend_spring.pvta.modules.cuentaCorriente.model.dto.CuentaCorrienteDTO;
import pvta_backend_spring.pvta.modules.factura.events.FacturaCreditoEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CuentaCorrienteService {
    private final ConexionBusiness cone;

    public ResponseDTO<?> grabar(Usuario usu, CuentaCorrienteDTO dto) throws SQLException {
        String sqlInsert = """
            INSERT INTO cuenta_corriente (
                entidad_id, tipo_entidad, tipo_movimiento, monto, concepto,
                factura_id, comprobante_id, fecha, usuario_id, activo
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id;
            """;

        try (Connection conn = cone.getConnection(usu)) {
            conn.setAutoCommit(false);
            Long idGenerado = null;
            
            try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                ps.setLong(1, dto.entidadId());
                ps.setString(2, dto.tipoEntidad());
                ps.setString(3, dto.tipoMovimiento());
                ps.setDouble(4, dto.monto());
                ps.setString(5, dto.concepto());
                ps.setObject(6, dto.facturaId());
                ps.setObject(7, dto.comprobanteId());
                ps.setTimestamp(8, dto.fecha() != null ? dto.fecha() : new Timestamp(System.currentTimeMillis()));
                ps.setString(9, dto.usuarioLic() != null ? dto.usuarioLic() : "");
                ps.setBoolean(10, true);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idGenerado = rs.getLong("id");
                    }
                }
            }

            if (idGenerado == null) {
                conn.rollback();
                conn.setAutoCommit(true);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo generar el ID del movimiento");
            }

            conn.commit();
            conn.setAutoCommit(true);
            
            CuentaCorrienteModel movimiento = CuentaCorrienteModel.builder()
                    .id(idGenerado)
                    .entidadId(dto.entidadId())
                    .tipoEntidad(dto.tipoEntidad())
                    .tipoMovimiento(dto.tipoMovimiento())
                    .monto(dto.monto())
                    .concepto(dto.concepto())
                    .build();

            return ResponseDTO.builder()
                    .messageResponse("MOVIMIENTO REGISTRADO EXITOSAMENTE")
                    .dataResponse(movimiento)
                    .build();

        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al grabar movimiento de cuenta corriente", e);
        }
    }

    public List<CuentaCorrienteModel> listar(Usuario usu, GlobalFilters filters) throws SQLException {
        String sqlSelect = "SELECT * FROM cuenta_corriente WHERE activo = true";
        List<CuentaCorrienteModel> movimientos = new ArrayList<>();

        try (Connection conn = cone.getConnection(usu);
             PreparedStatement ps = conn.prepareStatement(sqlSelect);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                movimientos.add(mapCuentaCorriente(rs));
            }
        }
        return movimientos;
    }

    public ResponseDTO<?> obtenerEstadoCuenta(Usuario usu, Long entidadId, String tipoEntidad) throws SQLException {
        String sqlSelect = """
            SELECT 
                tipo_movimiento,
                SUM(monto) as total
            FROM cuenta_corriente 
            WHERE entidad_id = ? AND tipo_entidad = ? AND activo = true
            GROUP BY tipo_movimiento
            """;

        double debe = 0;
        double haber = 0;

        try (Connection conn = cone.getConnection(usu);
             PreparedStatement ps = conn.prepareStatement(sqlSelect)) {

            ps.setLong(1, entidadId);
            ps.setString(2, tipoEntidad);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String tipo = rs.getString("tipo_movimiento");
                    double total = rs.getDouble("total");
                    if ("DEBE".equals(tipo)) {
                        debe = total;
                    } else if ("HABER".equals(tipo)) {
                        haber = total;
                    }
                }
            }
        }

        double saldo = debe - haber;
        
        Map<String, Object> estadoCuenta = new HashMap<>();
        estadoCuenta.put("entidadId", entidadId);
        estadoCuenta.put("tipoEntidad", tipoEntidad);
        estadoCuenta.put("debe", debe);
        estadoCuenta.put("haber", haber);
        estadoCuenta.put("saldo", saldo);

        return ResponseDTO.builder()
                .dataResponse(estadoCuenta)
                .build();
    }

    public ResponseDTO<?> registrarPago(Usuario usu, CuentaCorrienteDTO dto) throws SQLException {
        CuentaCorrienteDTO dtoActualizado = new CuentaCorrienteDTO(
            dto.id(),
            dto.entidadId(),
            dto.tipoEntidad(),
            "HABER",
            dto.monto(),
            "PAGO RECIBIDO",
            dto.facturaId(),
            dto.comprobanteId(),
            dto.fecha(),
            dto.usuarioLic()
        );
        return grabar(usu, dtoActualizado);
    }

    private CuentaCorrienteModel mapCuentaCorriente(ResultSet rs) throws SQLException {
        return CuentaCorrienteModel.builder()
                .id(rs.getLong("id"))
                .entidadId(rs.getLong("entidad_id"))
                .tipoEntidad(rs.getString("tipo_entidad"))
                .tipoMovimiento(rs.getString("tipo_movimiento"))
                .monto(rs.getDouble("monto"))
                .concepto(rs.getString("concepto"))
                .facturaId(rs.getObject("factura_id") != null ? rs.getLong("factura_id") : null)
                .comprobanteId(rs.getObject("comprobante_id") != null ? rs.getLong("comprobante_id") : null)
                .fecha(rs.getTimestamp("fecha"))
                .usuarioId(rs.getLong("usuario_id"))
                .activo(rs.getBoolean("activo"))
                .build();
    }
    
    @EventListener
    @Async
    public void generarCuotasAsincronicas(FacturaCreditoEvent event) {
        System.out.println("Iniciando generación asíncrona de cuotas - Cliente: " + event.getClienteId() + 
                          ", Factura: " + event.getNumeroFactura());
        
        try {
            List<CuentaCorrienteDTO> cuotas = new ArrayList<>();
            for (int i = 1; i <= event.getCantidadCuotas(); i++) {
                Date fechaVencimiento = calcularFechaVencimiento(
                    event.getPrimerVencimiento(), i, 30
                );
                
                CuentaCorrienteDTO cuota = new CuentaCorrienteDTO(
                    null, // ID se genera en BD
                    event.getClienteId(),
                    "CLIENTE",
                    "DEBE", // Genera deuda
                    event.getMontoCuota(),
                    String.format("CUOTA %d/%d - FACTURA %s", 
                        i, event.getCantidadCuotas(), event.getNumeroFactura()),
                    event.getFacturaId(),
                    null, // comprobante_id
                    new Timestamp(fechaVencimiento.getTime()),
                    event.getUsuarioId().getUsulic()
                );
                
                cuotas.add(cuota);
            }
            
            grabarCuotasBatch(cuotas, event);
            System.out.println("Cuotas generadas exitosamente - Cliente: " + event.getClienteId() +
                             ", Factura: " + event.getNumeroFactura() + 
                             ", Cuotas: " + event.getCantidadCuotas());
            
        } catch (Exception e) {
            System.err.println("Error generando cuotas asíncronas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void grabarCuotasBatch(List<CuentaCorrienteDTO> cuotas, FacturaCreditoEvent event) throws SQLException {
        if (cuotas == null || cuotas.isEmpty()) {
            return;
        }
        
        // Usar el clienteId del evento para la cuenta corriente
        Long clienteId = event.getClienteId();
        String usuarioId = event.getUsuarioId().getUsulic();
        Usuario usu = new Usuario();
        usu.setUsulic(usuarioId);
        
        String sqlInsert = """
            INSERT INTO cuenta_corriente (
                cliente_id, tipo_entidad, tipo_movimiento, monto, concepto,
                factura_id, fecha, activo
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
            
        try (Connection conn = cone.getConnection(usu)) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                for (CuentaCorrienteDTO cuota : cuotas) {
                    ps.setLong(1, clienteId);
                    ps.setString(2, cuota.tipoEntidad());
                    ps.setString(3, cuota.tipoMovimiento());
                    ps.setDouble(4, cuota.monto());
                    ps.setString(5, cuota.concepto());
                    ps.setObject(6, cuota.facturaId());
                    ps.setTimestamp(7, cuota.fecha());
                    ps.setBoolean(8, true);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            
            conn.commit();
        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error al grabar lote de cuotas", e);
        }
    }
    

    private Date calcularFechaVencimiento(Date fechaBase, int numeroCuota, int diasCuota) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(fechaBase);
        cal.add(java.util.Calendar.DAY_OF_MONTH, (numeroCuota - 1) * diasCuota);
        return new java.sql.Date(cal.getTime().getTime());
    }
}