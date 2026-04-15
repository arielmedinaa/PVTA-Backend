package pvta_backend_spring.pvta.modules.configuracionFacturacion.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionFacturacionDTO {
    private Long id;
    private Long establecimiento;
    private Long puntoExpedicion;
    private Long ultimoSecuencial;
}