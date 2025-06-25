package pvta_backend_spring.pvta.modules.productos.utiles;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pvta_backend_spring.pvta.conexion.ConexionBusiness;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.entities.response.ResponseDTO;
import pvta_backend_spring.pvta.modules.productos.model.dto.ProductosDTO;
import pvta_backend_spring.pvta.modules.productos.service.ProductosService;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelMigration {

    private final ProductosService productosService;
    private final ConexionBusiness cone;

    public List<ResponseDTO> importarDesdeExcel(MultipartFile file, Usuario usuario) throws IOException, SQLException {
        List<ResponseDTO> respuestas = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            Row header = sheet.getRow(0);
            List<String> expectedHeaders = List.of("codigo", "nombre", "stock", "descripcion", "unidadMedida", "categoriaId", "proveedor", "nomenclatura");

            for (int i = 0; i < expectedHeaders.size(); i++) {
                if (!expectedHeaders.get(i).equalsIgnoreCase(header.getCell(i).getStringCellValue().trim())) {
                    throw new IllegalArgumentException("Encabezado inválido en la columna " + (i + 1));
                }
            }

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                try {
                    ProductosDTO productoDTO = new ProductosDTO(
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
                            new ArrayList<>()
                    );

                    ResponseDTO response = productosService.grabar(usuario, productoDTO);
                    respuestas.add(response);
                } catch (Exception ex) {
                    respuestas.add(ResponseDTO.builder().dataResponse("Fila " + (rowIndex + 1) + ": " + ex.getMessage()).build());
                }
            }
        }

        return respuestas;
    }
}
