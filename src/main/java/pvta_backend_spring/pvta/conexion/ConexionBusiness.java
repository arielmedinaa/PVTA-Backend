package pvta_backend_spring.pvta.conexion;

import lombok.RequiredArgsConstructor;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.stereotype.Component;
import pvta_backend_spring.pvta.entities.Usuario;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class ConexionBusiness {
    private final HashMap<String, DataSource> dataSources;

    public Connection getConnection(Usuario usuario) throws SQLException {
        if(dataSources.containsKey(usuario.getUsulic())) {
            return dataSources.get(usuario.getUsulic()).getConnection();
        }else {
            String url = "jdbc:postgresql://" + usuario.getUsuip() + ":" + usuario.getUsupuerto() + "/" + usuario.getUsubd() + "?autoReconnect=true";
            BasicDataSource basicDataSource = new BasicDataSource();
            basicDataSource.setValidationQuery("select 1 as dbcp_connection_test");
            basicDataSource.setTestOnBorrow(true);
            basicDataSource.setDriverClassName("org.postgresql.Driver");
            basicDataSource.setUsername(usuario.getUsuariobd());
            basicDataSource.setPassword(usuario.getPassbd());
            basicDataSource.setUrl(url);
            basicDataSource.setMaxActive(10);
            basicDataSource.setMaxIdle(10);
            basicDataSource.setInitialSize(5);
            basicDataSource.setMinIdle(3);
            basicDataSource.setMinEvictableIdleTimeMillis(900000);
            basicDataSource.setLogAbandoned(false);
            basicDataSource.setRemoveAbandoned(true);
            basicDataSource.setRemoveAbandonedTimeout(900);
            Connection connection = basicDataSource.getConnection();
            dataSources.put(usuario.getUsulic(), basicDataSource);
            return connection;
        }
    }
}
