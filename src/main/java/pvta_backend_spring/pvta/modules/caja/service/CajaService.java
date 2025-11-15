package pvta_backend_spring.pvta.modules.caja.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pvta_backend_spring.pvta.connection.ConexionBusiness;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.entities.response.ResponseDTO;
import pvta_backend_spring.pvta.modules.caja.models.CajaModel;
import pvta_backend_spring.pvta.modules.caja.models.dto.CajaDto;
import pvta_backend_spring.pvta.utiles.Utiles;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CajaService {
    private final ConexionBusiness cone;
    private final Utiles utiles;

    public ResponseDTO<CajaModel> grabar(Usuario usuario, CajaDto dto) {
        String sqlInsertCaja = """
            INSERT INTO public.caja (id, id_deposito, id_sucursal, usuario_id, nombre, activo)
            VALUES (nextval('caja_id_seq'::regclass), ?, ?, ?, ?, ?)
            RETURNING id;
            """;

            String sqlInsertMovCaja = """
            INSERT INTO public.movcaja (
                id, usuarioid, apertura, importeini, documentoini, importecierre, documentocierre, cajaid
            ) VALUES (
                nextval('movcaja_id_seq'::regclass), ?, ?, ?, ?, ?, ?, ?
            );
            """;

        try (Connection conn = cone.getConnection(usuario)) {
            conn.setAutoCommit(false);
            Long idGenerado = null;
            try (PreparedStatement psCaja = conn.prepareStatement(sqlInsertCaja)) {
                psCaja.setInt(1, dto.idDeposito());
                psCaja.setInt(2, dto.idSucursal());
                psCaja.setInt(3, dto.usuarioId());
                psCaja.setString(4, dto.nombre());
                psCaja.setBoolean(5, dto.activo());

                try (ResultSet rs = psCaja.executeQuery()) {
                    if (rs.next()) {
                        idGenerado = rs.getLong("id");
                    }
                }
            }

            if (idGenerado == null) {
                conn.rollback();
                conn.setAutoCommit(true);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo generar el ID de la caja");
            }

            try (PreparedStatement psMovCaja = conn.prepareStatement(sqlInsertMovCaja)) {

                psMovCaja.setInt(1, dto.usuarioId());
                psMovCaja.setString(2, utiles.horaParaguayaInsert());
                psMovCaja.setLong(3, dto.importeIni() != null ? dto.importeIni() : 0L);
                psMovCaja.setString(4, dto.documentoIni() != null ? dto.documentoIni().toString() : "");
                psMovCaja.setLong(5, dto.importeCierre() != null ? dto.importeCierre() : 0L);
                psMovCaja.setString(6, dto.documentoFin() != null ? dto.documentoFin().toString() : "");
                psMovCaja.setLong(7, idGenerado);

                psMovCaja.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);
            CajaModel caja = CajaModel.builder()
                    .id(idGenerado)
                    .idDeposito(dto.idDeposito())
                    .idSucursal(dto.idSucursal())
                    .usuarioId(dto.usuarioId())
                    .nombre(dto.nombre())
                    .activo(dto.activo())
                    .build();

            return ResponseDTO.<CajaModel>builder()
                    .messageResponse("CAJA CREADA EXITOSAMENTE")
                    .totalRegistros(1)
                    .dataResponse(caja)
                    .build();

        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al grabar la caja", e);
        }
    }

}
