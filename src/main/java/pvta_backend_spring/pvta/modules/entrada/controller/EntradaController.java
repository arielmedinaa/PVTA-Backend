package pvta_backend_spring.pvta.modules.entrada.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pvta_backend_spring.pvta.context.UsuarioContext;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.modules.entrada.model.DTO.EntradaDTO;
import pvta_backend_spring.pvta.modules.entrada.service.EntradaService;

import java.sql.SQLException;

@CrossOrigin
@RestController
@RequestMapping(value = "/entrada")
@RequiredArgsConstructor
public class EntradaController {

    private final EntradaService entradaService;

    @PostMapping("/grabar")
    public ResponseEntity<?> grabar(@RequestBody EntradaDTO data) throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        System.out.println(usu);
        return ResponseEntity.ok().body(entradaService.grabar(usu, data));
    }
}
