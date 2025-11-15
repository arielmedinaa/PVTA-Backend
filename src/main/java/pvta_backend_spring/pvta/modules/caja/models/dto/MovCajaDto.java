package pvta_backend_spring.pvta.modules.caja.models.dto;

import java.time.LocalDateTime;

public record MovCajaDto(
        Long id,
        Integer usuarioId,
        LocalDateTime apertura,
        Long importeIni,
        String documentoIni,
        Long importeCierre,
        String documentoCierre,
        Integer cajaId
) {}
