package pvta_backend_spring.pvta.modules.caja.models.dto;

public record CajaDto(
        Long id,
        Integer idDeposito,
        Integer idSucursal,
        Integer usuarioId,
        String nombre,
        Boolean activo,
        Integer documentoIni,
        Integer documentoFin,
        Long importeIni,
        Long importeCierre
) {}
