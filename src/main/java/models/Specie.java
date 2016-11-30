package models;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author mko
 */
public class Specie {

    private final JsonObject properties;

    private Specie(JsonObject properties) {
        this.properties = properties;
    }

    public static Specie fromResultSet(ResultSet rs) throws SQLException {
        return new Specie(
                Json.createObjectBuilder()
                    .add("name",  rs.getString("species_na"))
                    .add("plant", "y".equals(rs.getString("plant")))
                    .build()
        );
    }

    public JsonObjectBuilder toJson() {
        return Json.createObjectBuilder()
                .add("properties", properties);
    }

}
