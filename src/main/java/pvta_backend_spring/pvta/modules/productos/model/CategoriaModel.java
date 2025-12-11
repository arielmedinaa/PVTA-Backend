package pvta_backend_spring.pvta.modules.productos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoriaModel {
    Long id;
    String nombre;
    String codigo;
    boolean activo;
    LocalDate fechaCreacion;
    Long subCategoriaId;
}
