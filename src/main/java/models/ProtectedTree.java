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
public class ProtectedTree {

    private final JsonObject geometry;
    private final JsonObject properties;

    private ProtectedTree(JsonObject geometry, JsonObject properties) {
        this.geometry = geometry;
        this.properties = properties;
    }

    public static ProtectedTree fromResultSet(ResultSet rs) throws SQLException {
        JsonObject geometry = Json.createReader(new StringReader(rs.getString("geometry"))).readObject();

        JsonObject props = Json.createObjectBuilder()
                .add("name", rs.getString("nazov"))
                .add("type", rs.getString("nazovsk"))
                .add("typelat", rs.getString("nazovlat"))
                .add("since", rs.getDate("datumvyhla").toString())
                .add("photo", rs.getString("fotourl"))
                .add("url", rs.getString("urlpredpis"))
                .add("paragraph", rs.getString("legislativ"))
                .add("area", rs.getString("posobnost"))
                .add("cadaster", rs.getString("katasterna"))
                .build();

        return new ProtectedTree(geometry, props);
    }

    public static ProtectedTree fromSimpleResult(ResultSet rs) throws SQLException {
        return new ProtectedTree(
                null,
                Json.createObjectBuilder()
                    .add("name", rs.getString("nazov"))
                    .add("url", rs.getString("urlpredpis"))
                    .build()
        );
    }

    public JsonObjectBuilder toJson() {
        return geometry == null ? Json.createObjectBuilder().add("properties", properties)
                                : Json.createObjectBuilder()
                                    .add("geometry", geometry)
                                    .add("properties", properties);
    }

}