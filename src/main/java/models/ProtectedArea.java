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
public class ProtectedArea {

    private final JsonObject geometry;
    private final JsonObject properties;

    private ProtectedArea(JsonObject geometry, JsonObject properties) {
        this.geometry = geometry;
        this.properties = properties;
    }

    public static ProtectedArea fromResultSet(ResultSet rs) throws SQLException {
        JsonObject geometry = Json.createReader(new StringReader(rs.getString("geometry"))).readObject();

        JsonObject props = Json.createObjectBuilder()
                .add("name",  rs.getString("nazov"))
                .add("zone", rs.getString("zonacia"))
                .add("category", rs.getString("kategoria"))
                .add("since", rs.getDate("datumvyhla").toString())
                .add("paragraph",  rs.getString("legislativ"))
                .add("size", rs.getInt("size"))
                .add("url", rs.getString("urlpredpis"))
                .build();

        return new ProtectedArea(geometry, props);
    }

    public JsonObjectBuilder toJson() {
        return Json.createObjectBuilder()
                .add("geometry", geometry)
                .add("properties", properties);
    }

}