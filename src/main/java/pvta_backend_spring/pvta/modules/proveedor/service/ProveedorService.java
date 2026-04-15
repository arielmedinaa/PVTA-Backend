package pvta_backend_spring.pvta.modules.proveedor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pvta_backend_spring.pvta.connection.ConexionBusiness;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.modules.proveedor.model.dto.ProveedorDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorService {
    private final ConexionBusiness cone;

    public Object grabar(Usuario usuario, ProveedorDTO dto) {
        String sqlInsert = """
            INSERT INTO clienteproveedor (ruc, nombre, mail, telefono, tipo, direccion)
            VALUES (?, ?, ?, ?, 'PROVEEDOR', ?)
            RETURNING id;
            """;

        try (Connection conn = cone.getConnection(usuario)) {
            conn.setAutoCommit(false);
            Long idGenerado = null;
            try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                ps.setString(1, dto.ruc() != null ? dto.ruc() : "");
                ps.setString(2, dto.nombre() != null ? dto.nombre() : "");
                ps.setString(3, dto.email() != null ? dto.email() : "");
                ps.setString(4, dto.telefono() != null ? dto.telefono() : "");
                ps.setString(5, dto.direccion() != null ? dto.direccion() : "");

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idGenerado = rs.getLong("id");
                    }
                }
            }

            if (idGenerado == null) {
                conn.rollback();
                conn.setAutoCommit(true);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo generar el ID del proveedor");
            }

            conn.commit();
            conn.setAutoCommit(true);
            
            return new ProveedorDTO(
                idGenerado, dto.codigo(), dto.nombre(), dto.razonSocial(),
                dto.ruc(), dto.direccion(), dto.telefono(), dto.email(),
                dto.contacto(), "PROVEEDOR"
            );

        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al grabar el proveedor: " + e.getMessage(), e);
        }
    }

    public void actualizar(Usuario usuario, ProveedorDTO data) {
        String sqlUpdate = """
            UPDATE clienteproveedor SET 
                ruc = ?, nombre = ?, mail = ?, telefono = ?, direccion = ?
            WHERE id = ? AND tipo = 'PROVEEDOR'
            """;

        try (Connection conn = cone.getConnection(usuario)) {
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setString(1, data.ruc() != null ? data.ruc() : "");
                ps.setString(2, data.nombre() != null ? data.nombre() : "");
                ps.setString(3, data.email() != null ? data.email() : "");
                ps.setString(4, data.telefono() != null ? data.telefono() : "");
                ps.setString(5, data.direccion() != null ? data.direccion() : "");
                ps.setLong(6, data.id() != null ? data.id() : 0);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado");
                }
            }
        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar el proveedor", e);
        }
    }

    public List<ProveedorDTO> listar(Usuario usuario, pvta_backend_spring.pvta.entities.filters.GlobalFilters filters) {
        String sqlSelect = "SELECT * FROM clienteproveedor WHERE tipo = 'PROVEEDOR'";
        List<ProveedorDTO> proveedores = new ArrayList<>();

        try (Connection conn = cone.getConnection(usuario);
             PreparedStatement ps = conn.prepareStatement(sqlSelect);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                proveedores.add(new ProveedorDTO(
                    rs.getLong("id"),
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getString("razonsocial"),
                    rs.getString("ruc"),
                    rs.getString("direccion"),
                    rs.getString("telefono"),
                    rs.getString("mail"),
                    rs.getString("contacto"),
                    rs.getString("tipo")
                ));
            }
        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al listar proveedores", e);
        }
        return proveedores;
    }

    public ProveedorDTO buscarPorCodigo(Usuario usuario, String ruc) {
        String sqlSelect = "SELECT * FROM clienteproveedor WHERE ruc = ? AND tipo = 'PROVEEDOR'";

        try (Connection conn = cone.getConnection(usuario);
             PreparedStatement ps = conn.prepareStatement(sqlSelect)) {

            ps.setString(1, ruc);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ProveedorDTO(
                        rs.getLong("id"),
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getString("razonsocial"),
                        rs.getString("ruc"),
                        rs.getString("direccion"),
                        rs.getString("telefono"),
                        rs.getString("mail"),
                        rs.getString("contacto"),
                        rs.getString("tipo")
                    );
                }
            }
        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al buscar proveedor por RUC", e);
        }
        return null;
    }

    public void eliminar(Usuario usuario, Long id) {
        String sqlDelete = "DELETE FROM clienteproveedor WHERE id = ? AND tipo = 'PROVEEDOR'";

        try (Connection conn = cone.getConnection(usuario)) {
            try (PreparedStatement ps = conn.prepareStatement(sqlDelete)) {
                ps.setLong(1, id);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado");
                }
            }
        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar el proveedor", e);
        }
    }
}
