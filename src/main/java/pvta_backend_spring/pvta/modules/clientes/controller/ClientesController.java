package pvta_backend_spring.pvta.modules.clientes.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pvta_backend_spring.pvta.context.UsuarioContext;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.entities.filters.GlobalFilters;
import pvta_backend_spring.pvta.modules.clientes.models.dto.ClientesDTO;
import pvta_backend_spring.pvta.modules.clientes.service.ClienteService;

import java.sql.SQLException;

@CrossOrigin(origins = "*")
@RequestMapping("/clientes")
@RestController
@RequiredArgsConstructor
public class ClientesController {

    private final ClienteService clienteService;

    @PostMapping("/grabar")
    public ResponseEntity<?> grabar(@RequestBody ClientesDTO data) throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        return ResponseEntity.ok().body(clienteService.create(data, usu));
    }

    @PostMapping("/listar")
    public ResponseEntity<?> listar(@RequestBody GlobalFilters filters) throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        return ResponseEntity.ok().body(clienteService.findAll(usu, filters));
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<?> eliminar(@RequestParam(value = "id") Long id) throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        return ResponseEntity.ok().body(clienteService.delete(id, usu));
    }

    @PatchMapping("/update")
    public void actualizar(@RequestBody ClientesDTO data) throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        clienteService.update(data.id(), data, usu);
    }
}
