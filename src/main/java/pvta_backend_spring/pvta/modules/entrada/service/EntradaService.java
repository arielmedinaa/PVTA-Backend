package pvta_backend_spring.pvta.modules.entrada.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;
import pvta_backend_spring.pvta.connection.ConexionBusiness;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.entities.response.ResponseDTO;
import pvta_backend_spring.pvta.modules.entrada.model.DTO.EntradaDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class EntradaService {
    private ConexionBusiness conexionBusiness;

    public ResponseDTO<EntradaDTO> grabar(Usuario usu, @Validated EntradaDTO data){
        try(Connection conn = conexionBusiness.getConnection(usu)){
            conn.setAutoCommit(false);

            try(PreparedStatement psInsert = conn.prepareStatement("""
                    INSERT INTO public.entradastock
                    (sucursal_id, numero_entrada, moneda, fecha_creacion, fecha_modificacion, cotizacion)
                    VALUES(0, 0, '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);
                    """)){

            }catch (Exception e){
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al insertar los datos", e);
            }

        }catch (SQLException e){
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al intentar conectar a la base de datos", e);
        }
        return null;
    }

//    private void insertarDetalles(Connection conn, @Validated EntradaDetalleModel detalle){
//        try(PreparedStatement psInsert = conn.prepareStatement("""
//
//                """))
//    }
}
