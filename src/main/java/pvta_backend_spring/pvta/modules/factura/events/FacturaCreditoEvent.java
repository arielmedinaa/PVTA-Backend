package pvta_backend_spring.pvta.modules.factura.events;

import pvta_backend_spring.pvta.entities.Usuario;

import java.util.Date;

/**
 * Evento para comunicación asíncrona de generación de cuotas
 * Se dispara cuando se crea una factura a crédito
 */
public class FacturaCreditoEvent {
    private final Long facturaId;
    private final Long clienteId;
    private final String numeroFactura;
    private final Double total;
    private final Integer cantidadCuotas;
    private final Double montoCuota;
    private final Date primerVencimiento;
    private final Usuario usuarioLic;

    public FacturaCreditoEvent(Long facturaId, Long clienteId, String numeroFactura,
                               Double total, Integer cantidadCuotas, Double montoCuota,
                               Date primerVencimiento, Usuario usuarioLic) {
        this.facturaId = facturaId;
        this.clienteId = clienteId;
        this.numeroFactura = numeroFactura;
        this.total = total;
        this.cantidadCuotas = cantidadCuotas;
        this.montoCuota = montoCuota;
        this.primerVencimiento = primerVencimiento;
        this.usuarioLic = usuarioLic;
    }

    public Long getFacturaId() {
        return facturaId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public Double getTotal() {
        return total;
    }

    public Integer getCantidadCuotas() {
        return cantidadCuotas;
    }

    public Double getMontoCuota() {
        return montoCuota;
    }

    public java.sql.Date getPrimerVencimiento() {
        if (primerVencimiento instanceof java.sql.Timestamp) {
            return new java.sql.Date(((java.sql.Timestamp) primerVencimiento).getTime());
        }
        return (java.sql.Date) primerVencimiento;
    }

    public Usuario getUsuarioId() {
        return usuarioLic;
    }

    @Override
    public String toString() {
        return String.format("FacturaCreditoEvent{facturaId=%d, clienteId=%d, numeroFactura='%s', total=%.2f, cantidadCuotas=%d, montoCuota=%.2f}",
                facturaId, clienteId, numeroFactura, total, cantidadCuotas, montoCuota);
    }
}
