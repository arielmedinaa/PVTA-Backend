package pvta_backend_spring.pvta.modules.clientes.service;

import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pvta_backend_spring.pvta.conexion.ConexionBusiness;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.entities.response.ResponseDTO;
import pvta_backend_spring.pvta.modules.clientes.models.ClienteModel;
import pvta_backend_spring.pvta.modules.clientes.models.dto.ClientesDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
@Service
public class ClienteService {

    private final ConexionBusiness cone;

    public ResponseDTO<Object> create(ClientesDTO dto, Usuario usuario) throws SQLException {
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
        ps.executeQuery();

        @Cleanup ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            ClienteModel cliente = convertDtoToModel(dto, rs.getLong(1));
            return ResponseDTO.builder()
                    .messageResponse("CLIENTE REGISTRADO CON EXITO")
                    .dataResponse(cliente).build();
        }

        return null;
    }

    public void update(long id, ClientesDTO dto, Usuario usuario) throws SQLException {
        @Cleanup Connection conn = cone.getConnection(usuario);
        StringBuilder sb = new StringBuilder("UPDATE cliente SET ");
        List<Object> valores = new ArrayList<>();
        for (var field : ClienteModel.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(dto);
                if (value != null) {
                    sb.append(field.getName()).append(" = ?, ");
                    valores.add(value);
                }
            } catch (IllegalAccessException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al obtener valor del campo", e);
            }
        }
        sb.setLength(sb.length() - 2);
        sb.append(" WHERE id = ?");

        @Cleanup PreparedStatement ps = conn.prepareStatement(sb.toString());
        for (int i = 0; i < valores.size(); i++) {
            ps.setObject(i + 1, valores.get(i));
        }
        ps.setLong(valores.size() + 1, id);
        ps.executeQuery();
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

