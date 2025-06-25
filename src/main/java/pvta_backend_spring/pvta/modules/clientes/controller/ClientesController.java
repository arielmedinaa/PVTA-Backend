package pvta_backend_spring.pvta.modules.clientes.controller;

import lombok.RequiredArgsConstructor;
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
    public void grabar(@RequestBody ClientesDTO data) throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        clienteService.create(data, usu);
    }

    @GetMapping("/listar")
    public void listar(@RequestBody GlobalFilters filters) throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        clienteService.findAll(usu, filters);
    }
}
