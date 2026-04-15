package pvta_backend_spring.pvta.modules.configuracionFacturacion.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pvta_backend_spring.pvta.connection.ConexionBusiness;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.entities.response.ResponseDTO;
import pvta_backend_spring.pvta.modules.configuracionFacturacion.model.ConfiguracionFacturacionModel;
import pvta_backend_spring.pvta.modules.configuracionFacturacion.model.dto.ConfiguracionFacturacionDTO;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ConfiguracionFacturacionService {
    private final ConexionBusiness cone;

    public ResponseDTO<?> grabar(Usuario usu, ConfiguracionFacturacionDTO dto) throws SQLException {
        String sqlInsert = """
            INSERT INTO configuracion_facturacion (
                establecimiento, punto_expedicion, ultimo_secuencial, activo
            ) VALUES (?, ?, ?, ?)
            RETURNING id;
            """;

        try (Connection conn = cone.getConnection(usu)) {
            conn.setAutoCommit(false);
            Long idGenerado = null;
            
            try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                ps.setLong(1, dto.getEstablecimiento());
                ps.setLong(2, dto.getPuntoExpedicion());
                ps.setLong(3, dto.getUltimoSecuencial() != null ? dto.getUltimoSecuencial() : 0);
                ps.setBoolean(4, true);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idGenerado = rs.getLong("id");
                    }
                }
            }

            if (idGenerado == null) {
                conn.rollback();
                conn.setAutoCommit(true);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo generar el ID de configuración");
            }

            conn.commit();
            conn.setAutoCommit(true);
            
            ConfiguracionFacturacionModel config = ConfiguracionFacturacionModel.builder()
                    .id(idGenerado)
                    .establecimiento(dto.getEstablecimiento())
                    .puntoExpedicion(dto.getPuntoExpedicion())
                    .ultimoSecuencial(dto.getUltimoSecuencial() != null ? dto.getUltimoSecuencial() : 0)
                    .activo(true)
                    .build();

            return ResponseDTO.builder()
                    .messageResponse("CONFIGURACIÓN CREADA EXITOSAMENTE")
                    .dataResponse(config)
                    .build();

        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al grabar la configuración", e);
        }
    }

    public ResponseDTO<?> actualizar(Usuario usu, ConfiguracionFacturacionDTO dto) throws SQLException {
        String sqlUpdate = """
            UPDATE configuracion_facturacion SET 
                establecimiento = ?, punto_expedicion = ?, ultimo_secuencial = ?
            WHERE id = ? AND activo = true
            """;

        try (Connection conn = cone.getConnection(usu)) {
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setLong(1, dto.getEstablecimiento());
                ps.setLong(2, dto.getPuntoExpedicion());
                ps.setLong(3, dto.getUltimoSecuencial() != null ? dto.getUltimoSecuencial() : 0);
                ps.setLong(4, dto.getId());

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Configuración no encontrada");
                }
            }
        }

        return ResponseDTO.builder()
                .messageResponse("CONFIGURACIÓN ACTUALIZADA EXITOSAMENTE")
                .build();
    }

    public ResponseDTO<?> obtenerConfiguracion(Usuario usu) throws SQLException {
        String sqlSelect = "SELECT * FROM configuracion_facturacion WHERE activo = true ORDER BY id DESC LIMIT 1";

        try (Connection conn = cone.getConnection(usu);
             PreparedStatement ps = conn.prepareStatement(sqlSelect);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                ConfiguracionFacturacionModel config = ConfiguracionFacturacionModel.builder()
                        .id(rs.getLong("id"))
                        .establecimiento(rs.getLong("establecimiento"))
                        .puntoExpedicion(rs.getLong("punto_expedicion"))
                        .ultimoSecuencial(rs.getLong("ultimo_secuencial"))
                        .activo(rs.getBoolean("activo"))
                        .build();

                return ResponseDTO.builder()
                        .dataResponse(config)
                        .build();
            }
        }

        return ResponseDTO.builder()
                .messageResponse("No existe configuración de facturación")
                .dataResponse(null)
                .build();
    }

    public ResponseDTO<?> generarNumeroFactura(Usuario usu) throws SQLException {
        String sqlSelect = "SELECT * FROM configuracion_facturacion WHERE activo = true ORDER BY id DESC LIMIT 1";
        String sqlUpdate = "UPDATE configuracion_facturacion SET ultimo_secuencial = ? WHERE id = ?";

        try (Connection conn = cone.getConnection(usu)) {
            conn.setAutoCommit(false);
            
            ConfiguracionFacturacionModel config = null;
            
            try (PreparedStatement psSelect = conn.prepareStatement(sqlSelect);
                 ResultSet rs = psSelect.executeQuery()) {

                if (rs.next()) {
                    config = ConfiguracionFacturacionModel.builder()
                            .id(rs.getLong("id"))
                            .establecimiento(rs.getLong("establecimiento"))
                            .puntoExpedicion(rs.getLong("punto_expedicion"))
                            .ultimoSecuencial(rs.getLong("ultimo_secuencial"))
                            .build();
                }
            }

            if (config == null) {
                conn.rollback();
                conn.setAutoCommit(true);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe configuración de facturación. Debe crear una primero.");
            }

            long nuevoSecuencial = config.getUltimoSecuencial() + 1;
            
            try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
                psUpdate.setLong(1, nuevoSecuencial);
                psUpdate.setLong(2, config.getId());
                psUpdate.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);

            String numeroFactura = String.format("%03d-%03d-%06d", 
                    config.getEstablecimiento(), 
                    config.getPuntoExpedicion(), 
                    nuevoSecuencial);

            String year = new SimpleDateFormat("yyyy").format(new Date());
            
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("numeroFactura", numeroFactura);
            result.put("establecimiento", config.getEstablecimiento());
            result.put("puntoExpedicion", config.getPuntoExpedicion());
            result.put("secuencial", nuevoSecuencial);
            result.put("year", year);

            return ResponseDTO.builder()
                    .dataResponse(result)
                    .build();

        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al generar número de factura", e);
        }
    }
}