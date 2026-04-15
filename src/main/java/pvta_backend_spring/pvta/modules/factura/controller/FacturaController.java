package pvta_backend_spring.pvta.modules.factura.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pvta_backend_spring.pvta.context.UsuarioContext;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.modules.factura.model.dto.DetalleFacturaDTO;
import pvta_backend_spring.pvta.modules.factura.model.dto.FacturaDTO;
import pvta_backend_spring.pvta.modules.factura.service.FacturaService;

import java.sql.SQLException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/factura")
@RequiredArgsConstructor
public class FacturaController {

    private final FacturaService facturaService;

    @PostMapping("/grabar")
    public ResponseEntity<?> grabar(@RequestBody FacturaDTO data) throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        return ResponseEntity.ok().body(facturaService.grabar(usu, data));
    }

    @PostMapping("/actualizar")
    public void actualizar(@RequestBody FacturaDTO data) throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        facturaService.actualizar(usu, data);
    }

//    @PostMapping("/listar")
//    public ResponseEntity<?> listar(@RequestBody pvta_backend_spring.pvta.entities.filters.GlobalFilters filters) throws SQLException {
//        Usuario usu = UsuarioContext.getUsuario();
//        return ResponseEntity.ok().body(facturaService.listar(usu, filters));
//    }

//    @GetMapping("/validarExistencia")
//    public ResponseEntity<?> validarExistencia(@RequestParam(value = "numero") String numero) throws SQLException {
//        Usuario usu = UsuarioContext.getUsuario();
//        return ResponseEntity.ok().body(facturaService.buscarPorNumero(usu, numero));
//    }

    @PostMapping("/detalle/listar")
    public ResponseEntity<?> listarDetalle(@RequestParam(value = "facturaId") Long facturaId) throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        return ResponseEntity.ok().body(facturaService.listarDetallePorFactura(usu, facturaId));
    }
}