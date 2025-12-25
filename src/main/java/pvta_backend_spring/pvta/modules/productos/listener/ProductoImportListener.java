package pvta_backend_spring.pvta.modules.productos.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pvta_backend_spring.pvta.modules.productos.event.ProductoImportEvent;
import pvta_backend_spring.pvta.modules.productos.service.ProductosService;

import java.util.concurrent.Semaphore;

@Component
@RequiredArgsConstructor
public class ProductoImportListener {
    private final ProductosService service;
    private final Semaphore semaphore = new Semaphore(5);

    @Async("asyncExecutor")
    @EventListener
    public void procesarProducto(ProductoImportEvent event) throws Exception {
        try {
            semaphore.acquire();
            service.grabar(event.getUsuario(), event.getProductoDTO());
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            semaphore.release();
        }
    }

}
