package pvta_backend_spring.pvta.modules.clientes.models.dto;

public record ClientesDTO(
        long id,
        String ruc,
        String nombre,
        String mail,
        String telefono,
        String tipo,
        String direccion,
        String natrec,
        int tipope,
        int tipcont,
        int tipdoc,
        String numerodoc,
        String ciuemidesc,
        String paisemidesc,
        String paisemi,
        String nroconstancia,
        String nrocontrol
) {
}
