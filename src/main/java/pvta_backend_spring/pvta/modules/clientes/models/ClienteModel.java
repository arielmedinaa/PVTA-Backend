package pvta_backend_spring.pvta.modules.clientes.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public class ClienteModel {
    long id;

    String ruc;
    String nombre;
    String mail;
    String telefono;
    String tipo;
    String direccion;
    String natrec;
    int tipope;
    int tipcont;
    int tipdoc;
    String numerodoc;
    String ciuemidesc;
    String paisemidesc;
    String paisemi;
    String nroconstancia;
    String nrocontrol;
}
