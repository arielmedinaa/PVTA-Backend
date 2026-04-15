package pvta_backend_spring.pvta.modules.configuracionFacturacion.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pvta_backend_spring.pvta.context.UsuarioContext;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.modules.configuracionFacturacion.model.dto.ConfiguracionFacturacionDTO;
import pvta_backend_spring.pvta.modules.configuracionFacturacion.service.ConfiguracionFacturacionService;

import java.sql.SQLException;

@CrossOrigin
@RestController
@RequestMapping("/configuracionFacturacion")
@RequiredArgsConstructor
public class ConfiguracionFacturacionController {

    private final ConfiguracionFacturacionService configuracionFacturacionService;

    @PostMapping("/grabar")
    public ResponseEntity<?> grabar(@RequestBody ConfiguracionFacturacionDTO data) throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        return ResponseEntity.ok().body(configuracionFacturacionService.grabar(usu, data));
    }

    @PostMapping("/actualizar")
    public ResponseEntity<?> actualizar(@RequestBody ConfiguracionFacturacionDTO data) throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        return ResponseEntity.ok().body(configuracionFacturacionService.actualizar(usu, data));
    }

    @GetMapping("/obtener")
    public ResponseEntity<?> obtener() throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        return ResponseEntity.ok().body(configuracionFacturacionService.obtenerConfiguracion(usu));
    }

    @GetMapping("/generarNumeroFactura")
    public ResponseEntity<?> generarNumeroFactura() throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        return ResponseEntity.ok().body(configuracionFacturacionService.generarNumeroFactura(usu));
    }
}