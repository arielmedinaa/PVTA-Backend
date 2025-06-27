package pvta_backend_spring.pvta.modules.clientes.service;

import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;
import pvta_backend_spring.pvta.conexion.ConexionBusiness;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.entities.filters.GlobalFilters;
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
        try (PreparedStatement ps = conn.prepareStatement("""
        INSERT INTO clienteproveedor (
            ruc, nombre, mail, telefono, tipo,
            direccion, natrec, tipope, tipcont, tipdoc,
            numerodoc, ciuemidesc, paisemidesc, paisemi,
            nroconstancia, nrocontrol
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """, PreparedStatement.RETURN_GENERATED_KEYS)) {

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
                long idGenerado = rs.getLong(1);
                ClienteModel cliente = convertDtoToModel(dto, idGenerado);
                System.out.println(cliente);
                return ResponseDTO.builder()
                        .messageResponse("CLIENTE REGISTRADO CON EXITO")
                        .dataResponse(cliente)
                        .build();
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error al crear cliente");
                throw new SQLException("No se pudo obtener el ID generado del cliente.");
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error al ejecutar el query de creacion", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al ejecutar el query de creacion", e);
        }
    }

    public void update(long id, ClientesDTO dto, Usuario usuario) throws SQLException {
        @Cleanup Connection conn = cone.getConnection(usuario);
        StringBuilder sb = new StringBuilder("UPDATE public.clienteproveedor SET ");
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

    public ResponseDTO<Object> delete(Long id, Usuario usuario) throws SQLException {
        @Cleanup Connection conn = cone.getConnection(usuario);
        try(PreparedStatement psDlt = conn.prepareStatement("""
                DELETE FROM public.clienteproveedor
                WHERE id=?
                """)){
            psDlt.setLong(1, id);
            psDlt.executeQuery();
            return ResponseDTO.builder()
                    .messageResponse("CLIENTE ELIMINADO CON EXITO")
                    .dataResponse("OK").build();
        }catch (Exception e){
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al intentar eliminar el cliente con id " + id, e);
        }
    }

    public ResponseDTO<Object> findAll(Usuario usuario, @Validated GlobalFilters filters) throws SQLException {
        @Cleanup Connection conn = cone.getConnection(usuario);
        StringBuilder sbSlt = new StringBuilder("SELECT id, ruc, nombre, mail, telefono, tipo, direccion, natrec, tipope, tipcont, tipdoc, numerodoc, ciuemidesc, paisemidesc, paisemi, nroconstancia, nrocontrol FROM public.clienteproveedor");
        boolean hasWhere = false;

        if (filters.getId() != 0) {
            sbSlt.append(" WHERE id = ").append(filters.getId());
            hasWhere = true;
        }
        if (filters.getFechaDesde() != null) {
            sbSlt.append(hasWhere ? " AND" : " WHERE");
            sbSlt.append(" fecha >= '").append(filters.getFechaDesde()).append("'");
            hasWhere = true;
        }
        if (filters.getFechaHasta() != null) {
            sbSlt.append(hasWhere ? " AND" : " WHERE");
            sbSlt.append(" fecha <= '").append(filters.getFechaHasta()).append("'");
            hasWhere = true;
        }
        if (filters.getRuc() != null) {
            sbSlt.append(hasWhere ? " AND" : " WHERE");
            sbSlt.append(" ruc = '").append(filters.getRuc()).append("'");
            hasWhere = true;
        }
        if (filters.getNombre() != null) {
            sbSlt.append(hasWhere ? " AND" : " WHERE");
            sbSlt.append(" nombre = '").append(filters.getNombre()).append("'");
        }

        sbSlt.append(" LIMIT ").append(filters.getLimit()).append(" OFFSET ").append(filters.getOffset());

        try (PreparedStatement ps = conn.prepareStatement(sbSlt.toString())) {
            @Cleanup ResultSet rs = ps.executeQuery();
            List<ClienteModel> clientes = new ArrayList<>();
            while (rs.next()) {
                ClientesDTO dto = new ClientesDTO(
                        rs.getLong("id"),
                        rs.getString("ruc"),
                        rs.getString("nombre"),
                        rs.getString("mail"),
                        rs.getString("telefono"),
                        rs.getString("tipo"),
                        rs.getString("direccion"),
                        rs.getString("natrec"),
                        rs.getInt("tipope"),
                        rs.getInt("tipcont"),
                        rs.getInt("tipdoc"),
                        rs.getString("numerodoc"),
                        rs.getString("ciuemidesc"),
                        rs.getString("paisemidesc"),
                        rs.getString("paisemi"),
                        rs.getString("nroconstancia"),
                        rs.getString("nrocontrol")
                );
                clientes.add(convertDtoToModel(dto, dto.id()));
            }

            return ResponseDTO.builder()
                    .messageResponse("CLIENTES ENCONTRADOS")
                    .dataResponse(clientes)
                    .build();
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al intentar obtener los clientes", e);
        }
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

