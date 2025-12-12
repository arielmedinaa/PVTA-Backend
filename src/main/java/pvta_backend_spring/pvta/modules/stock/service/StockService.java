package pvta_backend_spring.pvta.modules.stock.service;

import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pvta_backend_spring.pvta.connection.ConexionBusiness;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.entities.response.ResponseDTO;
import pvta_backend_spring.pvta.modules.stock.model.StockModel;
import pvta_backend_spring.pvta.modules.stock.model.dto.StockDTO;

import java.sql.*;

@Service
@RequiredArgsConstructor
public class StockService {
    private final ConexionBusiness cone;

    public ResponseDTO<StockModel> grabaStock(Usuario usu, @Validated StockDTO data) throws SQLException {
        @Cleanup Connection conn = cone.getConnection(usu);
        String sql = """
        INSERT INTO public.stock
        (id, origen, linea, producto_id, codigo_producto, cantidad, costo, costome, total, totalme, moneda, fecha_creacion, deposito_id)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        StockModel model;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            double saldo = data.cantidad() * data.costo();

            ps.setLong(1, data.id());
            ps.setString(2, data.origen());
            ps.setInt(3, data.linea());
            ps.setLong(4, data.idProducto());
            ps.setString(5, data.codigo());
            ps.setDouble(6, data.cantidad());
            ps.setLong(7, data.costo());
            ps.setDouble(8, data.costome());
            ps.setDouble(9, saldo);
            ps.setDouble(10, data.totalme());
            ps.setString(11, data.moneda());
            ps.setTimestamp(12, data.fecha());
            ps.setLong(13, Long.parseLong(data.deposito()));

            ps.executeUpdate();

            long generatedId = 0;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getLong(1);
                }
            }

            model = StockModel.builder()
                    .id(generatedId)
                    .costo(data.costo())
                    .total((long) (data.cantidad() * data.costo()))
                    .linea(data.linea())
                    .origen(data.origen())
                    .deposito(data.deposito())
                    .depositodes(data.depositodes())
                    .codigo(data.codigo())
                    .fecha(data.fecha())
                    .cantidad(data.cantidad())
                    .saldo(data.cantidad() * data.costo())
                    .costome(data.costome())
                    .totalme(data.totalme())
                    .moneda(data.moneda())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ResponseDTO.<StockModel>builder()
                .messageResponse("STOCK REGISTRADO CORRECTAMENTE")
                .totalRegistros(1)
                .dataResponse(model)
                .build();
    }


}
