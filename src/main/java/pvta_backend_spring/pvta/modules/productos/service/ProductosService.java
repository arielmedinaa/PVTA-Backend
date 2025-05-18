package pvta_backend_spring.pvta.modules.productos.service;

import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pvta_backend_spring.pvta.conexion.ConexionBusiness;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.modules.productos.model.PrecioModel;
import pvta_backend_spring.pvta.modules.productos.model.ProductosModel;
import pvta_backend_spring.pvta.entities.response.ResponseDTO;
import pvta_backend_spring.pvta.modules.productos.model.dto.ProductosDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductosService {
    private final ConexionBusiness cone;
    private final PreciosService preciosService;

    public ResponseDTO grabar(Usuario usu, @Validated ProductosDTO productos) throws SQLException {
        long productoId = 0;
        try (Connection conn = cone.getConnection(usu)) {
            conn.setAutoCommit(false);

            try (PreparedStatement psIns = conn.prepareStatement("""
                INSERT INTO productos (codigo, nombre, stock, descripcion,
                unidad_medida, categoria_id, proveedor, nomenclatura,
                fecha_creacion, fecha_actualizada) VALUES (?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """, Statement.RETURN_GENERATED_KEYS)) {

                psIns.setString(1, productos.codigo());
                psIns.setString(2, productos.nombre());
                psIns.setBoolean(3, productos.stock());
                psIns.setString(4, productos.descripcion());
                psIns.setString(5, productos.unidadMedida());
                psIns.setLong(6, productos.categoriaId());
                psIns.setString(7, productos.proveedor());
                psIns.setString(8, productos.nomenclatura());
                psIns.executeUpdate();

                try (ResultSet generatedKeys = psIns.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        productoId = generatedKeys.getLong(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID del producto insertado.");
                    }
                }
            }

            for (PrecioModel precio : productos.precios()) {
                precio.setProductoId(productoId);
                preciosService.grabar(conn, precio);
            }

            ProductosModel prod = ProductosModel.builder()
                    .id(productoId)
                    .descripcion(productos.descripcion())
                    .codigo(productos.codigo())
                    .stock(productos.stock())
                    .precio(preciosService.listarPorProducto(usu, productoId))
                    .unidadMed(productos.unidadMedida())
                    .build();
            conn.commit();
            conn.setAutoCommit(true);
            return ResponseDTO.builder()
                    .dataResponse(prod)
                    .build();
        } catch (Exception e) {
            throw new SQLException("Error al guardar el producto y sus precios: " + e.getMessage(), e);
        }
    }


    public void actualizar(Usuario usu, ProductosModel productos) throws SQLException, IllegalAccessException {
        @Cleanup Connection conn = cone.getConnection(usu);
        StringBuilder sql = new StringBuilder("UPDATE public.productos SET ");
        List<Object> valores = new ArrayList<>();

        for (var field : ProductosModel.class.getDeclaredFields()) {
            field.setAccessible(true);
            String nombreCampo = field.getName();
            Object valor = field.get(productos);
            if (nombreCampo.equals("id")) {
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
        valores.add(productos.getId());
        @Cleanup PreparedStatement ps = conn.prepareStatement(sql.toString());
        for (int i = 0; i < valores.size(); i++) {
            ps.setObject(i + 1, valores.get(i));
        }
        ps.executeUpdate();
    }

    public ResponseDTO<ProductosModel> eliminar(Usuario usu, ProductosDTO productos) throws SQLException {return null;}

    public ResponseDTO listar(Usuario usu) throws SQLException {
        @Cleanup Connection conn = cone.getConnection(usu);
        @Cleanup PreparedStatement ps = conn.prepareStatement("""
            SELECT p.*, (SELECT COUNT(*) FROM public.productos) AS totalRegistros
            FROM public.productos p;
            """);
        @Cleanup ResultSet rs = ps.executeQuery();
        List<ProductosModel> productosList = new ArrayList<>();
        long totalRegistros = 0;
        while (rs.next()) {
            ProductosModel producto = ProductosModel.builder()
                    .id(rs.getLong("id"))
                    .codigo(rs.getString("codigo"))
                    .nombre(rs.getString("nombre"))
                    .stock(rs.getBoolean("stock"))
                    .precio(preciosService.listarPorProducto(usu, rs.getLong("id")))
                    .build();

            productosList.add(producto);
            totalRegistros = rs.getLong("totalRegistros");
        }

        return ResponseDTO.builder()
                .dataResponse(productosList)
                .totalRegistros(totalRegistros)
                .build();
    }

    private String convertirNombreCampo(String nombreCampo) {
        return switch (nombreCampo){
            case "unidadMed" -> "unidad_medida";
            case "categoriaId" -> "categoria_id";
            default -> nombreCampo.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
        };
    }
}
