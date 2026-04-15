package pvta_backend_spring.pvta.modules.factura.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pvta_backend_spring.pvta.connection.ConexionBusiness;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.entities.filters.GlobalFilters;
import pvta_backend_spring.pvta.entities.response.ResponseDTO;
import pvta_backend_spring.pvta.modules.factura.model.DetalleFacturaModel;
import pvta_backend_spring.pvta.modules.factura.model.FacturaModel;
import pvta_backend_spring.pvta.modules.factura.model.dto.DetalleFacturaDTO;
import pvta_backend_spring.pvta.modules.factura.model.dto.FacturaDTO;
import pvta_backend_spring.pvta.modules.factura.events.FacturaCreditoEvent;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FacturaService {
    private final ConexionBusiness cone;
    private final ApplicationEventPublisher eventPublisher;

    public ResponseDTO<?> grabar(Usuario usu, FacturaDTO dto) throws SQLException {
        System.out.println("DTO: " + dto);
        String sqlInsert = """
            INSERT INTO factura (
                cliente_id, numero, documento, timbrado, condicion, sucursal,
                fecha, fechavincu, gra10, gra5, exenta, total, iva10, iva5,
                moneda, cotizacion, gra10me, gra5me, exentame, totalme, iva10me, iva5me,
                id_caja, id_movcaja, anulado, estado, porcdesc, descuento, descuentome,
                origen, anticipo, idsucursalcliente,
                cuotainicial, cuotamensual, cuotadias, cuotacant, cuotaprimervenc, cuotainicialme
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id;
            """;

        try (Connection conn = cone.getConnection(usu)) {
            conn.setAutoCommit(false);
            Long idGenerado = null;
            
            try (PreparedStatement ps = conn.prepareStatement(sqlInsert);
                    PreparedStatement psDetalle = conn.prepareStatement("""
                        INSERT INTO facturadetalle (
                            factura_id, producto_id, producto_codigo, producto_nombre,
                            cantidad, precio, precio_me, iva_tipo, iva_monto, iva_monto_me,
                            gra10, gra5, exenta, total, iva10, iva5, gra10me, gra5me, exentame,
                            totalme, iva10me, iva5me, descuento, descuentome, deposito_id, linea
                        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """)
                ) {
                ps.setLong(1, dto.clienteId());
                ps.setString(2, dto.numeroFactura());
                ps.setString(3, dto.documento() != null ? dto.documento() : "");
                ps.setString(4, dto.timbrado() != null ? dto.timbrado() : "");
                ps.setString(5, dto.condicion() != null ? dto.condicion() : "CONTADO");
                ps.setObject(6, dto.sucursalId());
                ps.setTimestamp(7, dto.fechaEmision() != null ? dto.fechaEmision() : new Timestamp(System.currentTimeMillis()));
                ps.setTimestamp(8, dto.fechaVencimiento());
                ps.setDouble(9, dto.gra10() != null ? dto.gra10() : 0.0);
                ps.setDouble(10, dto.gra5() != null ? dto.gra5() : 0.0);
                ps.setDouble(11, dto.exenta() != null ? dto.exenta() : 0.0);
                ps.setDouble(12, dto.total() != null ? dto.total() : 0.0);
                ps.setDouble(13, dto.iva10() != null ? dto.iva10() : 0.0);
                ps.setDouble(14, dto.iva5() != null ? dto.iva5() : 0.0);
                ps.setString(15, dto.moneda() != null ? dto.moneda() : "PYG");
                ps.setDouble(16, dto.cotizacion() != null ? dto.cotizacion() : 1.0);
                ps.setDouble(17, dto.gra10me() != null ? dto.gra10me() : 0.0);
                ps.setDouble(18, dto.gra5me() != null ? dto.gra5me() : 0.0);
                ps.setDouble(19, dto.exentame() != null ? dto.exentame() : 0.0);
                ps.setDouble(20, dto.totalme() != null ? dto.totalme() : 0.0);
                ps.setDouble(21, dto.iva10me() != null ? dto.iva10me() : 0.0);
                ps.setDouble(22, dto.iva5me() != null ? dto.iva5me() : 0.0);
                ps.setObject(23, dto.cajaId());
                ps.setObject(24, dto.movCajaId());
                ps.setBoolean(25, false);
                ps.setString(26, dto.estado() != null ? dto.estado() : "EMITIDA");
                ps.setDouble(27, dto.porcDesc() != null ? dto.porcDesc() : 0.0);
                ps.setDouble(28, dto.descuento() != null ? dto.descuento() : 0.0);
                ps.setDouble(29, dto.descuentome() != null ? dto.descuentome() : 0.0);
                ps.setString(30, dto.origen() != null ? dto.origen() : "FV");
                ps.setDouble(31, dto.anticipo() != null ? dto.anticipo() : 0.0);
                ps.setObject(32, dto.idSucursalCliente());
                // Campos de configuración de crédito
                ps.setDouble(33, dto.cuotainicial() != null ? dto.cuotainicial() : 0.0);
                ps.setDouble(34, dto.cuotamensual() != null ? dto.cuotamensual() : 0.0);
                ps.setInt(35, dto.cuotadias() != null ? dto.cuotadias() : 0);
                ps.setInt(36, dto.cuotacant() != null ? dto.cuotacant() : 0);
                ps.setTimestamp(37, dto.cuotaprimervenc());
                ps.setDouble(38, dto.cuotainicialme() != null ? dto.cuotainicialme() : 0.0);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idGenerado = rs.getLong("id");
                    }
                }
                
                for (DetalleFacturaDTO detalle : dto.detalle()) {
                    psDetalle.setLong(1, idGenerado);
                    psDetalle.setLong(2, detalle.productoId());
                    psDetalle.setString(3, detalle.productoCodigo());
                    psDetalle.setString(4, detalle.productoNombre());
                    psDetalle.setDouble(5, detalle.cantidad());
                    psDetalle.setDouble(6, detalle.precioUnitario());
                    psDetalle.setObject(7, detalle.precioMe());
                    psDetalle.setInt(8, detalle.ivaTipo() != null ? detalle.ivaTipo() : 10);
                    psDetalle.setDouble(9, detalle.ivaMonto() != null ? detalle.ivaMonto() : 0.0);
                    psDetalle.setObject(10, detalle.ivaMontoMe());
                    psDetalle.setDouble(11, detalle.gra10() != null ? detalle.gra10() : 0.0);
                    psDetalle.setDouble(12, detalle.gra5() != null ? detalle.gra5() : 0.0);
                    psDetalle.setDouble(13, detalle.exenta() != null ? detalle.exenta() : 0.0);
                    psDetalle.setDouble(14, detalle.total() != null ? detalle.total() : 0.0);
                    psDetalle.setDouble(15, detalle.iva10() != null ? detalle.iva10() : 0.0);
                    psDetalle.setDouble(16, detalle.iva5() != null ? detalle.iva5() : 0.0);
                    psDetalle.setDouble(17, detalle.gra10me() != null ? detalle.gra10me() : 0.0);
                    psDetalle.setDouble(18, detalle.gra5me() != null ? detalle.gra5me() : 0.0);
                    psDetalle.setDouble(19, detalle.exentame() != null ? detalle.exentame() : 0.0);
                    psDetalle.setDouble(20, detalle.totalme() != null ? detalle.totalme() : 0.0);
                    psDetalle.setDouble(21, detalle.iva10me() != null ? detalle.iva10me() : 0.0);
                    psDetalle.setDouble(22, detalle.iva5me() != null ? detalle.iva5me() : 0.0);
                    psDetalle.setDouble(23, detalle.descuento() != null ? detalle.descuento() : 0.0);
                    psDetalle.setObject(24, detalle.descuentome());
                    psDetalle.setObject(25, detalle.depositoId());
                    psDetalle.setInt(26, detalle.linea() != null ? detalle.linea() : 1);
                    psDetalle.addBatch();
                }
                psDetalle.executeBatch();
            }

            if (idGenerado == null) {
                conn.rollback();
                conn.setAutoCommit(true);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo generar el ID de la factura");
            }

            conn.commit();
            conn.setAutoCommit(true);
            
            if ("CREDITO".equals(dto.condicion())) {
                publicarEventoCuotas(idGenerado, dto, usu);
            }
            
            FacturaModel factura = FacturaModel.builder()
                    .id(idGenerado)
                    .numeroFactura(dto.numeroFactura())
                    .clienteId(dto.clienteId())
                    .total(dto.total())
                    .estado(dto.estado() != null ? dto.estado() : "EMITIDA")
                    .build();

            return ResponseDTO.builder()
                    .messageResponse("FACTURA CREADA EXITOSAMENTE")
                    .dataResponse(factura)
                    .build();

        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al grabar la factura: " + e.getMessage(), e);
        }
    }

    public void actualizar(Usuario usu, FacturaDTO data) throws SQLException {
        String sqlUpdate = """
            UPDATE factura SET 
                cliente_id = ?, numero = ?, documento = ?, timbrado = ?, condicion = ?,
                fecha = ?, fechavincu = ?, gra10 = ?, gra5 = ?, exenta = ?, total = ?,
                iva10 = ?, iva5 = ?, moneda = ?, cotizacion = ?, estado = ?
            WHERE id = ?
            """;

        try (Connection conn = cone.getConnection(usu)) {
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setLong(1, data.clienteId());
                ps.setString(2, data.numeroFactura());
                ps.setString(3, data.documento());
                ps.setString(4, data.timbrado());
                ps.setString(5, data.condicion());
                ps.setTimestamp(6, data.fechaEmision());
                ps.setTimestamp(7, data.fechaVencimiento());
                ps.setDouble(8, data.gra10());
                ps.setDouble(9, data.gra5());
                ps.setDouble(10, data.exenta());
                ps.setDouble(11, data.total());
                ps.setDouble(12, data.iva10());
                ps.setDouble(13, data.iva5());
                ps.setString(14, data.moneda());
                ps.setDouble(15, data.cotizacion());
                ps.setString(16, data.estado());
                ps.setLong(17, data.id());

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Factura no encontrada");
                }
            }
        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar la factura", e);
        }
    }

//    public List<FacturaModel> listar(Usuario usu, GlobalFilters filters) throws SQLException {
//        String sqlSelect = "SELECT * FROM factura WHERE anulado = false LIMIT = ? OFFSET = ?";
//        List<FacturaModel> facturas = new ArrayList<>();
//
//        try (Connection conn = cone.getConnection(usu);
//             PreparedStatement ps = conn.prepareStatement(sqlSelect)) {
//            ps.setLong(1, filters.getLimit());
//            ps.setLong(2, filters.getOffset());
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    facturas.add(mapFactura(rs));j
//                }
//            }
//            return facturas;
//        }
//    }

//    public FacturaModel buscarPorNumero(Usuario usu, String numero) throws SQLException {
//        String sqlSelect = "SELECT * FROM factura WHERE numero = ? AND anulado = false";
//
//        try (Connection conn = cone.getConnection(usu);
//             PreparedStatement ps = conn.prepareStatement(sqlSelect)) {
//
//            ps.setString(1, numero);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    return mapFactura(rs);
//                }
//            }
//        }
//        return null;
//    }

//    public FacturaModel buscarPorId(Usuario usu, long id) throws SQLException {
//        String sqlSelect = "SELECT * FROM factura WHERE id = ?";
//
//        try (Connection conn = cone.getConnection(usu);
//             PreparedStatement ps = conn.prepareStatement(sqlSelect)) {
//
//            ps.setLong(1, id);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    return mapFactura(rs);
//                }
//            }
//        }
//        return null;
//    }

    public List<DetalleFacturaModel> listarDetallePorFactura(Usuario usu, Long facturaId) throws SQLException {
        String sqlSelect = "SELECT * FROM facturadetalle WHERE factura_id = ?";
        List<DetalleFacturaModel> detalles = new ArrayList<>();

        try (Connection conn = cone.getConnection(usu);
             PreparedStatement ps = conn.prepareStatement(sqlSelect)) {
            
            ps.setLong(1, facturaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detalles.add(mapDetalleFactura(rs));
                }
            }
        }
        return detalles;
    }

//    private FacturaModel mapFactura(ResultSet rs) throws SQLException {
//        return FacturaModel.builder()
//                .id(rs.getLong("id"))
//                .clienteId(rs.getLong("cliente_id"))
//                .numeroFactura(rs.getString("numero"))
//                .documento(rs.getString("documento"))
//                .timbrado(rs.getString("timbrado"))
//                .condicion(rs.getString("condicion"))
//                .fechaEmision(rs.getTimestamp("fecha"))
//                .fechaVencimiento(rs.getTimestamp("fechavincu"))
//                .gra10(rs.getDouble("gra10"))
//                .gra5(rs.getDouble("gra5"))
//                .exenta(rs.getDouble("exenta"))
//                .total(rs.getDouble("total"))
//                .iva10(rs.getDouble("iva10"))
//                .iva5(rs.getDouble("iva5"))
//                .moneda(rs.getString("moneda"))
//                .cotizacion(rs.getDouble("cotizacion"))
//                .cajaId(rs.getObject("id_caja") != null ? rs.getLong("id_caja") : null)
//                .estado(rs.getString("estado"))
//                .anulado(rs.getBoolean("anulado"))
//                .origen(rs.getString("origen"))
//                .build();
//    }

    private DetalleFacturaModel mapDetalleFactura(ResultSet rs) throws SQLException {
        return DetalleFacturaModel.builder()
                .id(rs.getLong("id"))
                .facturaId(rs.getLong("factura_id"))
                .productoId(rs.getLong("producto_id"))
                .productoCodigo(rs.getString("producto_codigo"))
                .productoNombre(rs.getString("producto_nombre"))
                .cantidad(rs.getDouble("cantidad"))
                .precioUnitario(rs.getDouble("precio"))
                .ivaTipo(rs.getInt("iva_tipo"))
                .ivaMonto(rs.getDouble("iva_monto"))
                .gra10(rs.getDouble("gra10"))
                .gra5(rs.getDouble("gra5"))
                .exenta(rs.getDouble("exenta"))
                .total(rs.getDouble("total"))
                .iva10(rs.getDouble("iva10"))
                .iva5(rs.getDouble("iva5"))
                .descuento(rs.getDouble("descuento"))
                .linea(rs.getInt("linea"))
                .build();
    }
    
    private void publicarEventoCuotas(Long facturaId, FacturaDTO dto, Usuario usu) {
        Integer cantidadCuotas = extraerCantidadCuotas(dto);
        Double montoCuota = extraerMontoCuota(dto);
        Date primerVencimiento = extraerPrimerVencimiento(dto);
        
        FacturaCreditoEvent event = new FacturaCreditoEvent(
            facturaId,
            dto.clienteId(),
            dto.numeroFactura(),
            dto.total(),
            cantidadCuotas,
            montoCuota,
            primerVencimiento,
            usu
        );
        
        eventPublisher.publishEvent(event);
        System.out.println("Evento publicado para generación de cuotas: " + event);
    }

    private Integer extraerCantidadCuotas(FacturaDTO dto) {
        return dto.cuotacant() != null && dto.cuotacant() > 0 ? dto.cuotacant() : 1;
    }
    
    private Double extraerMontoCuota(FacturaDTO dto) {
        Integer cantidadCuotas = extraerCantidadCuotas(dto);
        if (dto.cuotamensual() != null && dto.cuotamensual() > 0) {
            return dto.cuotamensual();
        } else {
            return cantidadCuotas > 0 ? dto.total() / cantidadCuotas : dto.total();
        }
    }
    
    private Date extraerPrimerVencimiento(FacturaDTO dto) {
        if (dto.cuotaprimervenc() != null) {
            return dto.cuotaprimervenc();
        } else {
            long diasPorDefecto = dto.cuotadias() != null ? dto.cuotadias() : 30;
            return new Date(dto.fechaEmision() != null ? dto.fechaEmision().getTime() : System.currentTimeMillis() + (diasPorDefecto * 24L * 60 * 60 * 1000));
        }
    }
}