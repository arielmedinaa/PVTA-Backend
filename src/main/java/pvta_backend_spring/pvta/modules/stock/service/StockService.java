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

import java.sql.Connection;
import java.sql.SQLException;

@Service
@RequiredArgsConstructor
public class StockService {
    private final ConexionBusiness cone;

    public ResponseDTO<StockModel> grabaStock(Usuario usu, @Validated StockDTO data) throws SQLException {
        @Cleanup Connection conn = cone.getConnection(usu);
        return ResponseDTO.<StockModel>builder().build();
    }
}
