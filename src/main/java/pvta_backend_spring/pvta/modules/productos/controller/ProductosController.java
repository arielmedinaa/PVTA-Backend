package pvta_backend_spring.pvta.modules.productos.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pvta_backend_spring.pvta.context.UsuarioContext;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.entities.filters.GlobalFilters;
import pvta_backend_spring.pvta.modules.productos.model.dto.CategoriaDTO;
import pvta_backend_spring.pvta.modules.productos.model.dto.ProductosDTO;
import pvta_backend_spring.pvta.modules.productos.service.ProductosService;
import pvta_backend_spring.pvta.modules.productos.utiles.ExcelMigration;
import pvta_backend_spring.pvta.utiles.Utiles;

import java.io.IOException;
import java.sql.SQLException;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/productos")
public class ProductosController {
    private final ProductosService productosService;
    private final ExcelMigration excelMigration;

    @PostMapping("/grabar")
    public ResponseEntity<?> grabar(@RequestBody ProductosDTO data) throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        return ResponseEntity.ok().body(productosService.grabar(usu, data));
    }

    @PostMapping("/actualizar")
    public void actualizarProductos(@RequestBody ProductosDTO data) throws SQLException, IllegalAccessException {
        Usuario usu = UsuarioContext.getUsuario();
        productosService.actualizar(usu, data);
    }

    @PostMapping("/listar")
    public ResponseEntity<?> listar(@RequestBody GlobalFilters filters) throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        return ResponseEntity.ok().body(productosService.listar(usu, filters));
    }

    @GetMapping("/validarExistencia")
    public ResponseEntity<?> validarExistencia(@RequestParam(value = "codigo") String codigo) throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        return ResponseEntity.ok().body(productosService.buscarPorCodigo(usu, codigo));
    }

    @PostMapping(value = "/migrarExcelProductos", consumes = "multipart/form-data")
    public ResponseEntity<?>migrarExcelProductos(@RequestParam("file") MultipartFile file) throws SQLException, IOException {
        Usuario usu = UsuarioContext.getUsuario();
        return ResponseEntity.ok().body(excelMigration.importarDesdeExcel(file, usu));
    }

    @PostMapping(value = "/migrarExcelPrecios", consumes = "multipart/form-data")
    public ResponseEntity<?>migrarExcelPrecios(@RequestParam("file") MultipartFile file) throws SQLException, IOException {
        Usuario usu = UsuarioContext.getUsuario();
        return ResponseEntity.ok().body(excelMigration.importarPreciosDesdeExcel(file, usu));
    }

    //CONTROLLER CATEGORIA
    @PostMapping("/crearCategoria")
    public ResponseEntity<?> crearCategoria(@RequestBody CategoriaDTO data) throws SQLException {
        Usuario usu = UsuarioContext.getUsuario();
        return ResponseEntity.ok().body(productosService.crearCategoria(usu, data));
    }
}
