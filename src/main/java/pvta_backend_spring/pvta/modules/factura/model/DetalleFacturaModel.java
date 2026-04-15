package pvta_backend_spring.pvta.modules.factura.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleFacturaModel {
    private Long id;
    private Long facturaId;
    private Long productoId;
    private String productoCodigo;
    private String productoNombre;
    private String unidadMedida;
    private Double cantidad;
    private Double precioUnitario;
    private Double precioMe;
    private Integer ivaTipo;
    private Double ivaMonto;
    private Double ivaMontoMe;
    private Double gra10;
    private Double gra5;
    private Double exenta;
    private Double total;
    private Double iva10;
    private Double iva5;
    private Double gra10me;
    private Double gra5me;
    private Double exentame;
    private Double totalme;
    private Double iva10me;
    private Double iva5me;
    private Double descuento;
    private Double descargome;
    private Long depositoId;
    private Integer linea;
    private Timestamp fechaCreacion;
}