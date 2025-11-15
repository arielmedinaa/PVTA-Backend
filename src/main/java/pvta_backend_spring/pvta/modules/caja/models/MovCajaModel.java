package pvta_backend_spring.pvta.modules.caja.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MovCajaModel {
    private Long id;
    private Integer usuarioId;
    private LocalDateTime apertura;
    private Long importeIni;
    private String documentoIni;
    private Long importeCierre;
    private String documentoCierre;
    private Integer cajaId;
}