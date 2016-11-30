package util;

import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author mko
 */
@Stateless
public class DatabaseConnector {

    private static final String databaseURL = "jdbc:postgresql://localhost:5432/nature_of_slovakia";
    private static final Properties properties = new Properties();

    static {
        properties.put("user", "postgres");
        properties.put("password", "pgadmin");
    }

    public Statement createStatement() throws SQLException {
        return DriverManager.getConnection(databaseURL, properties).createStatement();
    }

    public PreparedStatement prepareStatement(String query) throws SQLException {
        return DriverManager.getConnection(databaseURL, properties).prepareStatement(query);
    }

}
