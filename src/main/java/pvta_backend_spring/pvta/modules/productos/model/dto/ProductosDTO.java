package pvta_backend_spring.pvta.modules.productos.model.dto;

import pvta_backend_spring.pvta.modules.productos.model.PrecioModel;

import java.time.LocalDate;
import java.util.List;

public record ProductosDTO(
        long id,
        long categoriaId,
        String codigo,
        String nombre,
        boolean stock,
        String descripcion,
        LocalDate fecha_creacion,
        LocalDate fecha_actualizada,
        String unidadMedida,
        String proveedor,
        String nomenclatura,
        List<PrecioModel> precios,
        Long cantidadInicial,
        Long sucursalId
        //ListaPrecios
        //Categorias
        //Imagenes
) {
}
