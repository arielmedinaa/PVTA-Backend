package pvta_backend_spring.pvta.modules.productos.event;

import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.modules.productos.model.dto.ProductosDTO;

public class ProductoImportEvent {
    private final ProductosDTO productoDTO;
    private final Usuario usuario;
    private final int fila;

    public ProductoImportEvent(ProductosDTO productoDTO, Usuario usuario, int fila) {
        this.productoDTO = productoDTO;
        this.usuario = usuario;
        this.fila = fila;
    }

    public ProductosDTO getProductoDTO() { return productoDTO; }
    public Usuario getUsuario() { return usuario; }
    public int getFila() { return fila; }


}
