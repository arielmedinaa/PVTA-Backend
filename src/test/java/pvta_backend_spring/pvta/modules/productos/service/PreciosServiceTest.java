package pvta_backend_spring.pvta.modules.productos.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pvta_backend_spring.pvta.conexion.ConexionBusiness;
import pvta_backend_spring.pvta.entities.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreciosServiceTest {

    @Mock
    private ConexionBusiness conexionBusiness;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @InjectMocks
    private PreciosService preciosService;

    private Usuario usuario;
    private final long PRECIO_ID = 1L;

    @BeforeEach
    void setUp() throws SQLException {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");

        when(conexionBusiness.getConnection(any(Usuario.class))).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    void eliminar_SuccessfullyDeletesExistingPrice() throws SQLException {
        // Arrange
        when(preparedStatement.executeUpdate()).thenReturn(1); // Simulate 1 row affected

        // Act & Assert
        assertDoesNotThrow(() -> preciosService.eliminar(usuario, PRECIO_ID));

        // Verify
        verify(conexionBusiness).getConnection(usuario);
        verify(connection).prepareStatement("DELETE FROM public.precios WHERE id = ?;");
        verify(preparedStatement).setLong(1, PRECIO_ID);
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).close();
        verify(connection).close();
    }
}