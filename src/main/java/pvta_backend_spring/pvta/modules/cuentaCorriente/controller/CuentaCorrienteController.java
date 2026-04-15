package pvta_backend_spring.pvta.modules.cuentaCorriente.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pvta_backend_spring.pvta.context.UsuarioContext;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.modules.cuentaCorriente.model.dto.CuentaCorrienteDTO;
import pvta_backend_spring.pvta.modules.cuentaCorriente.service.CuentaCorrienteService;

import java.sql.SQLException;

@CrossOrigin
@RestController
@RequestMapping("/cuentaCorriente")
@RequiredArgsConstructor
public class CuentaCorrienteController {

    private final CuentaCorrienteService cuentaCorrienteService;

    @PostMapping("/grabar")
    public ResponseEntity<?> grabar(@RequestBody CuentaCorrienteDTO data) throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        return ResponseEntity.ok().body(cuentaCorrienteService.grabar(usu, data));
    }

    @PostMapping("/listar")
    public ResponseEntity<?> listar(@RequestBody pvta_backend_spring.pvta.entities.filters.GlobalFilters filters) throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        return ResponseEntity.ok().body(cuentaCorrienteService.listar(usu, filters));
    }

    @GetMapping("/estadoCuenta")
    public ResponseEntity<?> estadoCuenta(
            @RequestParam(value = "entidadId") Long entidadId,
            @RequestParam(value = "tipoEntidad") String tipoEntidad) throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        return ResponseEntity.ok().body(cuentaCorrienteService.obtenerEstadoCuenta(usu, entidadId, tipoEntidad));
    }

    @PostMapping("/pago")
    public ResponseEntity<?> registrarPago(@RequestBody CuentaCorrienteDTO data) throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        return ResponseEntity.ok().body(cuentaCorrienteService.registrarPago(usu, data));
    }
}