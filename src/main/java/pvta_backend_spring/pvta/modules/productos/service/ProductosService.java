package pvta_backend_spring.pvta.modules.productos.service;

import jdk.jfr.Category;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;
import pvta_backend_spring.pvta.connection.ConexionBusiness;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.entities.filters.GlobalFilters;
import pvta_backend_spring.pvta.modules.productos.model.CategoriaModel;
import pvta_backend_spring.pvta.modules.productos.model.PrecioModel;
import pvta_backend_spring.pvta.modules.productos.model.ProductosModel;
import pvta_backend_spring.pvta.entities.response.ResponseDTO;
import pvta_backend_spring.pvta.modules.productos.model.dto.CategoriaDTO;
import pvta_backend_spring.pvta.modules.productos.model.dto.ProductosDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductosService {
    private final ConexionBusiness cone;
    private final PreciosService preciosService;

    public ResponseDTO<?> grabar(Usuario usu, @Validated ProductosDTO productos) throws SQLException {
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
                preciosService.grabar(usu, precio);
            }

            ProductosModel prod = ProductosModel.builder()
                    .id(productoId)
                    .nombre(productos.nombre())
                    .descripcion(productos.descripcion())
                    .familia(this.obtenerFamiliaProducto(usu, productos.categoriaId()))
                    .codigo(productos.codigo())
                    .stock(productos.stock())
                    .precio(preciosService.listarPorProducto(usu, productoId))
                    .unidadMed(productos.unidadMedida())
                    .build();
            conn.commit();
            conn.setAutoCommit(true);
            return ResponseDTO.builder()
                    .messageResponse("PRODUCTO CREADO EXITOSAMENTE")
                    .dataResponse(prod)
                    .build();
        } catch (Exception e) {
            throw new SQLException("Error al guardar el producto y sus precios: " + e.getMessage(), e);
        }
    }

    public void actualizar(Usuario usu, ProductosDTO productos) throws SQLException, IllegalAccessException {
        @Cleanup Connection conn = cone.getConnection(usu);
        StringBuilder sql = new StringBuilder("UPDATE public.productos SET ");
        List<Object> valores = new ArrayList<>();

        for (var field : ProductosDTO.class.getDeclaredFields()) {
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
        valores.add(productos.id());
        @Cleanup PreparedStatement ps = conn.prepareStatement(sql.toString());
        for (int i = 0; i < valores.size(); i++) {
            ps.setObject(i + 1, valores.get(i));
        }
        ps.executeUpdate();
    }

    public ResponseDTO<Object> eliminar(Usuario usu, ProductosDTO productos) throws SQLException {
        @Cleanup Connection conn = cone.getConnection(usu);
        @Cleanup PreparedStatement ps = conn.prepareStatement("""
                DELETE FROM public.productos WHERE id = ?;
                """);
        ps.setLong(1, productos.id());
        ps.executeUpdate();
        return ResponseDTO.builder()
                .dataResponse(ProductosModel.builder()
                .id(productos.id())
                .build())
                .messageResponse("PRODUCTO ELIMINADO CON EXITO")
                .build();
    }

    public ResponseDTO<?> listar(Usuario usu, GlobalFilters filter) throws SQLException {
        @Cleanup Connection conn = cone.getConnection(usu);
        StringBuilder sb = new StringBuilder("""
                SELECT p.*, (SELECT COUNT(*) FROM public.productos) AS totalRegistros, c.nombrecategoria
                FROM public.productos p
                inner join categorias c on c.id = p.categoria_id
                """);
        if(!filter.getCodigo().isEmpty()){
            sb.append(" WHERE codigo LIKE '%").append(filter.getCodigo()).append("%'");
        }
        if(!filter.getDescripcion().isEmpty()){
            sb.append(" WHERE descripcion LIKE '%").append(filter.getDescripcion()).append("%'");
        }

        sb.append(" LIMIT ")
                .append(filter.getLimit())
                .append(" OFFSET ")
                .append(filter.getOffset());
        @Cleanup PreparedStatement ps = conn.prepareStatement(sb.toString());
        //System.out.println(ps);
        @Cleanup ResultSet rs = ps.executeQuery();
        List<ProductosModel> productosList = new ArrayList<>();
        long totalRegistros = 0;
        while (rs.next()) {
            ProductosModel producto = ProductosModel.builder()
                    .id(rs.getLong("id"))
                    .codigo(rs.getString("codigo"))
                    .nombre(rs.getString("nombre"))
                    .stock(rs.getBoolean("stock"))
                    .unidadMed(rs.getString("unidad_medida"))
                    .familia(rs.getString("nombrecategoria"))
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

    public ProductosModel buscarPorCodigo(Usuario usu, String codigo) throws SQLException {
        @Cleanup Connection conn = cone.getConnection(usu);
        String sql = "SELECT id, codigo FROM productos WHERE codigo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ProductosModel p = new ProductosModel();
                    p.setId(rs.getLong("id"));
                    p.setCodigo(rs.getString("codigo"));
                    return p;
                }
            }
        }

        throw new ResponseStatusException(HttpStatus.CONFLICT, "EL CODIGO PUEDE UTILIZARSE");
    }

    private String convertirNombreCampo(String nombreCampo) {
        return switch (nombreCampo){
            case "unidadMedida" -> "unidad_medida";
            case "categoriaId" -> "categoria_id";
            default -> nombreCampo.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
        };
    }

    //CATEGORIAS
    public ResponseDTO<?> crearCategoria(Usuario usu, CategoriaDTO data) throws SQLException {
        long categoriaId;
        @Cleanup Connection conn = cone.getConnection(usu);
        @Cleanup PreparedStatement psIns = conn.prepareStatement("""
                INSERT INTO categorias
                (nombrecategoria, codigocategoria, fechacreacion, activo, subcategoriaid)
                VALUES(?, ?, CURRENT_TIMESTAMP, ?, ?);
                """, Statement.RETURN_GENERATED_KEYS);
        psIns.setString(1, data.nombre());
        psIns.setString(2, data.codigo());
        psIns.setBoolean(3, true);
        psIns.setLong(4, data.subCategoriaId());
        psIns.executeUpdate();

        try (ResultSet generatedKeys = psIns.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                categoriaId = generatedKeys.getLong(1);
            } else {
                throw new SQLException("No se pudo obtener el ID del producto insertado.");
            }
        }

        CategoriaModel categoriaModel = new CategoriaModel(categoriaId, data.nombre(), data.codigo(), true, data.fechaCreacion(), data.subCategoriaId());
        return ResponseDTO.builder()
                .dataResponse(categoriaModel)
                .messageResponse("CATEGORIA CREADO CON EXITO")
                .build();
    }

    private String obtenerFamiliaProducto(Usuario usu, long id) throws SQLException {
        @Cleanup Connection conn = cone.getConnection(usu);
        @Cleanup PreparedStatement ps = conn.prepareStatement("SELECT nombrecategoria FROM categorias WHERE id = ?");
        ps.setLong(1, id);

        @Cleanup ResultSet rs = ps.executeQuery();
        if(rs.next()){
            return rs.getString(1);
        }

        return "Sin Categoria";
    }
}
