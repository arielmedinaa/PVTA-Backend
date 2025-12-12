package pvta_backend_spring.pvta.modules.stock.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pvta_backend_spring.pvta.context.UsuarioContext;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.modules.stock.model.dto.StockDTO;
import pvta_backend_spring.pvta.modules.stock.service.StockService;

import java.sql.SQLException;

@CrossOrigin
@RestController
@RequestMapping(value = "/stock")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @PostMapping("/grabar")
    public ResponseEntity<?> grabar(@RequestBody StockDTO data) throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        return ResponseEntity.ok().body(stockService.grabaStock(usu, data));
    }
}
