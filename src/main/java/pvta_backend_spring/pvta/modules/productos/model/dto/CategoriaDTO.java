package pvta_backend_spring.pvta.modules.productos.model.dto;

import java.time.LocalDate;

public record CategoriaDTO(
        String nombre,
        String codigo,
        boolean activo,
        LocalDate fechaCreacion,
        Long subCategoriaId
) {
}
