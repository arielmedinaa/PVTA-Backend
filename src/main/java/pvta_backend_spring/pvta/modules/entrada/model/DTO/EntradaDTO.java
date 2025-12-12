package pvta_backend_spring.pvta.modules.entrada.model.DTO;

import pvta_backend_spring.pvta.modules.entrada.model.EntradaDetalleModel;

import java.sql.Timestamp;
import java.util.List;

public record EntradaDTO(
        String numero,
        String moneda,
        Timestamp fechaCreacion,
        String fechaActualizacion,
        long idSucursal,
        long cotizacion,
        List<EntradaDetalleModel> detalle
) {
}
