package pvta_backend_spring.pvta.modules.proveedor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pvta_backend_spring.pvta.context.UsuarioContext;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.modules.proveedor.model.dto.ProveedorDTO;
import pvta_backend_spring.pvta.modules.proveedor.service.ProveedorService;

import java.sql.SQLException;

@CrossOrigin
@RestController
@RequestMapping("/proveedor")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;

    @PostMapping("/grabar")
    public ResponseEntity<?> grabar(@RequestBody ProveedorDTO data) throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        return ResponseEntity.ok().body(proveedorService.grabar(usu, data));
    }

    @PostMapping("/actualizar")
    public void actualizar(@RequestBody ProveedorDTO data) throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        proveedorService.actualizar(usu, data);
    }

    @PostMapping("/listar")
    public ResponseEntity<?> listar(@RequestBody pvta_backend_spring.pvta.entities.filters.GlobalFilters filters) throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        return ResponseEntity.ok().body(proveedorService.listar(usu, filters));
    }

    @GetMapping("/validarExistencia")
    public ResponseEntity<?> validarExistencia(@RequestParam(value = "codigo") String codigo) throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        return ResponseEntity.ok().body(proveedorService.buscarPorCodigo(usu, codigo));
    }
}