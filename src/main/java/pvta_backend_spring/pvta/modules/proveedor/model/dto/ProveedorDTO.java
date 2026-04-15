package pvta_backend_spring.pvta.modules.proveedor.model.dto;

public record ProveedorDTO(
    Long id,
    String codigo,
    String nombre,
    String razonSocial,
    String ruc,
    String direccion,
    String telefono,
    String email,
    String contacto,
    String tipo
) {}
