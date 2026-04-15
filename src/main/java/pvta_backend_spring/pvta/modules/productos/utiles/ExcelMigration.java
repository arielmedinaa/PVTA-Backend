package pvta_backend_spring.pvta.modules.productos.utiles;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import pvta_backend_spring.pvta.connection.ConexionBusiness;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.entities.response.ResponseDTO;
import pvta_backend_spring.pvta.modules.productos.event.ProductoImportEvent;
import pvta_backend_spring.pvta.modules.productos.model.PrecioModel;
import pvta_backend_spring.pvta.modules.productos.model.dto.ProductosDTO;
import pvta_backend_spring.pvta.modules.productos.service.PreciosService;
import pvta_backend_spring.pvta.modules.productos.service.ProductosService;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelMigration {

    private final ProductosService productosService;
    private final PreciosService preciosService;
    private final ConexionBusiness cone;

    private final ApplicationEventPublisher publisher;

    public void importarDesdeExcel(MultipartFile file, Usuario usuario) throws IOException {
        int contador = 0;

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                ProductosDTO productoDTO = mapearFila(row);
                publisher.publishEvent(
                        new ProductoImportEvent(productoDTO, usuario, rowIndex + 1)
                );

                contador++;
                if (contador % 20 == 0) {
                    Thread.sleep(50);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ResponseDTO> importarPreciosDesdeExcel(MultipartFile file, Usuario usuario) throws IOException, SQLException {
        List<ResponseDTO> respuestas = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row header = sheet.getRow(0);
            List<String> expectedHeaders = List.of(
                    "codigoProducto",
                    "linea",
                    "precio",
                    "activo",
                    "iva",
                    "tipoPrecio",
                    "moneda"
            );

            for (int i = 0; i < expectedHeaders.size(); i++) {
                if (!expectedHeaders.get(i).equalsIgnoreCase(header.getCell(i).getStringCellValue().trim())) {
                    throw new IllegalArgumentException("Encabezado inválido en la columna " + (i + 1));
                }
            }

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {

                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                try {
                    String codigo = row.getCell(0).getStringCellValue().trim();

                    var producto = productosService.buscarPorCodigo(usuario, codigo);
                    if (producto == null) {
                        respuestas.add(ResponseDTO.builder()
                                .dataResponse("Fila " + (rowIndex + 1) + ": No existe producto con código " + codigo)
                                .build());
                        continue;
                    }

                    PrecioModel precio = new PrecioModel();
                    precio.setProductoId(producto.getId());
                    precio.setLinea((long) row.getCell(1).getNumericCellValue());
                    precio.setPrecio((long) row.getCell(2).getNumericCellValue());
                    precio.setActivo(row.getCell(3).getBooleanCellValue());
                    precio.setIva((long) row.getCell(4).getNumericCellValue());
                    precio.setTipoPrecio(row.getCell(5).getStringCellValue());
                    precio.setMoneda(row.getCell(6).getStringCellValue());

                    preciosService.grabar(usuario, precio);

                    respuestas.add(ResponseDTO.builder()
                            .dataResponse("Precio grabado para producto " + codigo)
                            .build());

                } catch (Exception ex) {
                    respuestas.add(ResponseDTO.builder()
                            .dataResponse("Fila " + (rowIndex + 1) + ": " + ex.getMessage())
                            .build());
                }
            }
        }

        return respuestas;
    }

    private ProductosDTO mapearFila(Row row) {
        return new ProductosDTO(
                0L,
                (long) row.getCell(5).getNumericCellValue(),
                row.getCell(0).getStringCellValue(),
                row.getCell(1).getStringCellValue(),
                row.getCell(2).getBooleanCellValue(),
                row.getCell(3).getStringCellValue(),
                null, null,
                row.getCell(4).getStringCellValue(),
                row.getCell(6).getStringCellValue(),
                row.getCell(7).getStringCellValue(),
                new ArrayList<>(),
                0L,
                0L
        );
    }

}
