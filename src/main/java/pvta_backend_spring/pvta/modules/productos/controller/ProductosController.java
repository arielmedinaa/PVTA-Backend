package pvta_backend_spring.pvta.modules.productos.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.modules.productos.model.dto.ProductosDTO;
import pvta_backend_spring.pvta.modules.productos.service.ProductosService;
import pvta_backend_spring.pvta.utiles.Utiles;

import java.sql.SQLException;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/productos")
public class ProductosController {
    private final ProductosService productosService;
    private final Utiles utiles;

    @PostMapping("/grabar")
    public ResponseEntity<?> grabar(@RequestHeader("Authorization") String token, @RequestBody ProductosDTO data) throws SQLException {
        Usuario usu = utiles.leerToken(token);
        return ResponseEntity.ok().body(productosService.grabar(usu, data));
    }

    @GetMapping
    public ResponseEntity<?> listar(@RequestHeader("Authorization") String token) throws SQLException {
        Usuario usu = utiles.leerToken(token);
        return ResponseEntity.ok().body(productosService.listar(usu));
    }
}
