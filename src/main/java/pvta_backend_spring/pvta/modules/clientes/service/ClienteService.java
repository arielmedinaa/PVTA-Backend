package pvta_backend_spring.pvta.modules.clientes.service;

import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pvta_backend_spring.pvta.conexion.ConexionBusiness;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.entities.response.ResponseDTO;
import pvta_backend_spring.pvta.modules.clientes.models.ClienteModel;
import pvta_backend_spring.pvta.modules.clientes.models.dto.ClientesDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
@Service
public class ClienteService {

    private final ConexionBusiness cone;

    public ResponseDTO<ClienteModel> create(ClientesDTO dto, Usuario usuario) throws SQLException {
        @Cleanup Connection conn = cone.getConnection(usuario);
        @Cleanup PreparedStatement ps = conn.prepareStatement("""
            INSERT INTO clienteproveedor (
                ruc, nombre, mail, telefono, tipo,
                direccion, natrec, tipope, tipcont, tipdoc,
                numerodoc, ciumeidesc, paisemidesc, paisemi,
                nroconstancia, nrocontrol
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, PreparedStatement.RETURN_GENERATED_KEYS);

        ps.setString(1, dto.ruc());
        ps.setString(2, dto.nombre());
        ps.setString(3, dto.mail());
        ps.setString(4, dto.telefono());
        ps.setString(5, dto.tipo());
        ps.setString(6, dto.direccion());
        ps.setString(7, dto.natrec());
        ps.setInt(8, dto.tipope());
        ps.setInt(9, dto.tipcont());
        ps.setInt(10, dto.tipdoc());
        ps.setString(11, dto.numerodoc());
        ps.setString(12, dto.ciuemidesc());
        ps.setString(13, dto.paisemidesc());
        ps.setString(14, dto.paisemi());
        ps.setString(15, dto.nroconstancia());
        ps.setString(16, dto.nrocontrol());
        ps.executeUpdate();

        @Cleanup ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            ClienteModel cliente = convertDtoToModel(dto, rs.getLong(1));
            return null;
        }

        return null;
    }

    public ResponseDTO<ClienteModel> update(long id, ClientesDTO dto, Usuario usuario) throws SQLException {
        @Cleanup Connection conn = cone.getConnection(usuario);
        @Cleanup PreparedStatement psUpd = conn.prepareStatement("""
        UPDATE clienteproveedor SET
            ruc = ?, nombre = ?, mail = ?, telefono = ?, tipo = ?,
            direccion = ?, natrec = ?, tipope = ?, tipcont = ?, tipdoc = ?,
            numerodoc = ?, ciumeidesc = ?, paisemidesc = ?, paisemi = ?,
            nroconstancia = ?, nrocontrol = ?
        WHERE id = ?
    """);

        psUpd.setString(1, dto.ruc());
        psUpd.setString(2, dto.nombre());
        psUpd.setString(3, dto.mail());
        psUpd.setString(4, dto.telefono());
        psUpd.setString(5, dto.tipo());
        psUpd.setString(6, dto.direccion());
        psUpd.setString(7, dto.natrec());
        psUpd.setInt(8, dto.tipope());
        psUpd.setInt(9, dto.tipcont());
        psUpd.setInt(10, dto.tipdoc());
        psUpd.setString(11, dto.numerodoc());
        psUpd.setString(12, dto.ciuemidesc());
        psUpd.setString(13, dto.paisemidesc());
        psUpd.setString(14, dto.paisemi());
        psUpd.setString(15, dto.nroconstancia());
        psUpd.setString(16, dto.nrocontrol());
        psUpd.setLong(17, id);
        int updatedRows = psUpd.executeUpdate();

        if (updatedRows > 0) {
            ClienteModel clienteActualizado = convertDtoToModel(dto, id);
            return null;
        } else {
            return null;
        }
    }


    public ResponseDTO<String> delete(long id, Usuario usuario) throws SQLException {
        return null;
    }

    private ClienteModel convertDtoToModel(ClientesDTO dto, long id) {
        return ClienteModel.builder()
                .id(id)
                .ruc(dto.ruc())
                .nombre(dto.nombre())
                .mail(dto.mail())
                .telefono(dto.telefono())
                .tipo(dto.tipo())
                .direccion(dto.direccion())
                .natrec(dto.natrec())
                .tipope(dto.tipope())
                .tipcont(dto.tipcont())
                .tipdoc(dto.tipdoc())
                .numerodoc(dto.numerodoc())
                .ciuemidesc(dto.ciuemidesc())
                .paisemidesc(dto.paisemidesc())
                .paisemi(dto.paisemi())
                .nroconstancia(dto.nroconstancia())
                .nrocontrol(dto.nrocontrol())
                .build();
    }
}

