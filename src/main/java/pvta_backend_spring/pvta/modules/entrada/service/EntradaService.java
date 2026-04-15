package pvta_backend_spring.pvta.modules.entrada.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;
import pvta_backend_spring.pvta.connection.ConexionBusiness;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.entities.response.ResponseDTO;
import pvta_backend_spring.pvta.modules.entrada.model.DTO.EntradaDTO;
import pvta_backend_spring.pvta.modules.entrada.model.EntradaDetalleModel;

import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class EntradaService {
    private final ConexionBusiness conexionBusiness;

    public ResponseDTO<EntradaDTO> grabar(Usuario usu, @Validated EntradaDTO data) throws SQLException {
        try (Connection conn = conexionBusiness.getConnection(usu)) {
            conn.setAutoCommit(false);

            long entradaId = 0;
            try (PreparedStatement psInsert = conn.prepareStatement("""
                INSERT INTO entrada_stock
                (sucursal_id, numero_entrada, moneda, fecha_creacion, fecha_modificacion, cotizacion)
                VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ?)
                """, Statement.RETURN_GENERATED_KEYS)) {

                psInsert.setLong(1, data.idSucursal());
                psInsert.setInt(2, Integer.parseInt(data.numero()));
                psInsert.setString(3, data.moneda());
                psInsert.setLong(4, data.cotizacion());

                psInsert.executeUpdate();

                try (ResultSet rs = psInsert.getGeneratedKeys()) {
                    if (rs.next()) {
                        entradaId = rs.getLong(1);
                    }
                }

                this.insertarDetalles(conn, entradaId, data.detalle());
                conn.commit();

            } catch (Exception e) {
                conn.rollback();
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al insertar los datos", e);
            }

        } catch (SQLException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al intentar conectar a la base de datos", e);
        }

        return ResponseDTO.<EntradaDTO>builder()
                .messageResponse("ENTRADA REGISTRADA CORRECTAMENTE")
                .totalRegistros(1)
                .dataResponse(data)
                .build();
    }

    private void insertarDetalles(Connection conn, long entradaId, List<EntradaDetalleModel> detalles) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("""
        INSERT INTO public.entrada_stock_detalle
        (entrada_id, linea, producto_id, codigo_producto, deposito_id, cantidad, costo, costome, total, totalme)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """)) {

            for (EntradaDetalleModel d : detalles) {
                ps.setLong(1, entradaId);
                ps.setInt(2, d.getLinea());
                ps.setLong(3, d.getIdProducto());
                ps.setString(4, d.getCodigoProducto());
                ps.setLong(5, d.getIdDeposito());
                ps.setInt(6, d.getCantidad());
                ps.setLong(7, d.getCosto());
                ps.setDouble(8, d.getCostome());
                ps.setLong(9, d.getTotal());
                ps.setDouble(10, d.getTotalme());

                ps.addBatch();
            }

            ps.executeBatch();
        }
    }


}
