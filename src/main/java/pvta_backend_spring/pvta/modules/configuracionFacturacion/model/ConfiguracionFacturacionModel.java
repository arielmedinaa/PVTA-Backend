package pvta_backend_spring.pvta.modules.configuracionFacturacion.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ConfiguracionFacturacionModel {
    private long id;
    private long establecimiento; // 001
    private long puntoExpedicion; // 001
    private long ultimoSecuencial; // Último número utilizado
    private boolean activo;
}