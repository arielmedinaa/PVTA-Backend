package pvta_backend_spring.pvta.modules.productos.service;

import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pvta_backend_spring.pvta.connection.ConexionBusiness;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.modules.productos.model.PrecioModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PreciosService {
    private final ConexionBusiness cone;

    public void grabar(Usuario usuario, PrecioModel data) throws SQLException {
        @Cleanup Connection conn = cone.getConnection(usuario);
        data.setId(1L);

        if (data.isActivo()) {
            String sqlCheck = """
            SELECT 1 FROM precios
            WHERE id = ? AND activo = TRUE
            """;
            try (PreparedStatement ps = conn.prepareStatement(sqlCheck)) {
                ps.setLong(1, data.getId()); // id = producto_id o 1 si venía null
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        throw new SQLException("Ya existe un precio activo para este producto.");
                    }
                }
            }
        }

        String sql = """
        INSERT INTO precios
        (id, linea, producto_id, precio, activo, iva, tipo_precio, moneda, fecha_creacion)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, data.getId());
            ps.setLong(2, data.getLinea());
            ps.setLong(3, data.getProductoId());
            ps.setLong(4, data.getPrecio());
            ps.setBoolean(5, data.isActivo());
            ps.setLong(6, data.getIva());
            ps.setString(7, data.getTipoPrecio());
            ps.setString(8, data.getMoneda());
            ps.executeUpdate();
        }
    }


    public void modificar(Usuario usu, PrecioModel data) throws SQLException, IllegalAccessException {
        @Cleanup Connection conn = cone.getConnection(usu);
        StringBuilder sql = new StringBuilder("UPDATE public.precios SET ");
        List<Object> valores = new ArrayList<>();

        for (var field : PrecioModel.class.getDeclaredFields()) {
            field.setAccessible(true);
            String nombreCampo = field.getName();
            Object valor = field.get(data);

            if (nombreCampo.equals("id") || nombreCampo.equals("fechaCreacion")) {
                continue;
            }

            if (valor != null) {
                if (valor instanceof Number && ((Number) valor).doubleValue() == 0) {
                    continue;
                }
                sql.append(convertirNombreCampo(nombreCampo)).append(" = ?, ");
                valores.add(valor);
            }
        }

        if (valores.isEmpty()) {
            throw new IllegalArgumentException("No hay datos para actualizar.");
        }
        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id = ?");
        valores.add(data.getId());

        @Cleanup PreparedStatement ps = conn.prepareStatement(sql.toString());
        for (int i = 0; i < valores.size(); i++) {
            ps.setObject(i + 1, valores.get(i));
        }
        ps.executeUpdate();
    }

    public void eliminar(Usuario usu, long id) throws SQLException {
        @Cleanup Connection conn = cone.getConnection(usu);
        @Cleanup PreparedStatement psDel = conn.prepareStatement("""
                DELETE FROM public.precios WHERE id = ?;
                """);
        psDel.setLong(1, id);
        psDel.executeUpdate();
    }

    public List<PrecioModel> listar(Usuario usu) throws SQLException {
        @Cleanup Connection conn = cone.getConnection(usu);
        @Cleanup PreparedStatement psSel = conn.prepareStatement("""
                SELECT *
                FROM public.precios;
                """);
        @Cleanup ResultSet rs = psSel.executeQuery();
        List<PrecioModel> precios = new ArrayList<>();
        while (rs.next()) {
            PrecioModel precio = new PrecioModel();
            precio.setId(rs.getLong("id"));
            precio.setProductoId(rs.getLong("producto_id"));
            precio.setPrecio(rs.getLong("precio"));
            precio.setActivo(rs.getBoolean("activo"));
            precio.setIva(rs.getLong("iva"));
            precio.setTipoPrecio(rs.getString("tipo_precio"));
            precio.setMoneda(rs.getString("moneda"));
            precio.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
            precios.add(precio);
        }
        return precios;
    }

    public List<PrecioModel> listarPorProducto(Usuario usu, long id) throws SQLException {
        @Cleanup Connection conn = cone.getConnection(usu);
        @Cleanup PreparedStatement psSel = conn.prepareStatement("""
                SELECT *
                FROM public.precios
                WHERE producto_id = ?;
                """);
        psSel.setLong(1, id);
        @Cleanup ResultSet rs = psSel.executeQuery();
        List<PrecioModel> precios = new ArrayList<>();
        while (rs.next()) {
            PrecioModel precio = new PrecioModel();
            precio.setId(rs.getLong("id"));
            precio.setProductoId(rs.getLong("producto_id"));
            precio.setPrecio(rs.getLong("precio"));
            precio.setActivo(rs.getBoolean("activo"));
            precio.setIva(rs.getLong("iva"));
            precio.setTipoPrecio(rs.getString("tipo_precio"));
            precio.setMoneda(rs.getString("moneda"));
            precio.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
            precios.add(precio);
        }
        return precios;
    }

    private String convertirNombreCampo(String nombreCampo) {
        return nombreCampo.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}
