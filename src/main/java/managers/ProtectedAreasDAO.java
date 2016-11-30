package managers;

import models.ProtectedArea;
import org.intellij.lang.annotations.Language;
import util.DatabaseConnector;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mko
 */
@Stateless
public class ProtectedAreasDAO {

    private static final String QUERY_ALL_ZONES
            = "select a.nazov, a.zonacia, a.kategoria, a.datumvyhla, a.legislativ, a.vymera / 1000000 as size, "
            + "a.legislativ, a.urlpredpis, "
            + "st_asgeojson(st_transform(a.geom, 4326)) as geometry from protected_areas a ";

    @Language("SQL")
    private static final String QUERY_AGGREGATE_AREA_SIZE
            = "select row_to_json(row) as json "
            + "from (select nazov as name, sum(vymera)/1000000 as totalSize from protected_areas "
            + "group by nazov) row";

    @Language("SQL")
    private static final String QUERY_NEAREST
            = "select row_to_json(row) as json from ("
                + "select * from (select nazov as name, min(st_distanceSphere(st_setsrid(ST_makepoint(?, ?), 4326), st_transform(geom, 4326))) / 1000 as distance "
                + "from protected_trees "
                + "group by nazov "
                + "order by distance "
                + "limit 3) nearest_trees "
                + "UNION ALL "
                + "select * from (select nazov, min(st_distanceSphere(st_setsrid(ST_makepoint(?, ?), 4326), geom)) / 1000 as min "
                + "from protected_areas_transformed "
                + "group by nazov "
                + "order by min "
                + "limit 3) nearest_areas "
                + "UNION ALL "
                + "select * from (select s.species_na as name, 0 from species_transformed s "
                + "where st_contains(s.geom, (st_setsrid(ST_makepoint(?, ?), 4326))) "
                + ") nearest_species "
            + ") row";

    @Inject
    DatabaseConnector dbConnector;

    public List<ProtectedArea> getAllAreas() throws SQLException {
        List<ProtectedArea> protectedAreaList = new ArrayList<>();

        Statement statement = dbConnector.createStatement();
        ResultSet result = statement.executeQuery(QUERY_ALL_ZONES);

        while (result.next()) {
            protectedAreaList.add(ProtectedArea.fromResultSet(result));
        }

        statement.close();
        return protectedAreaList;
    }

    public JsonArray getAggregateAreaSizes() throws SQLException {
        Statement statement = dbConnector.createStatement();
        ResultSet result = statement.executeQuery(QUERY_AGGREGATE_AREA_SIZE);

        JsonArrayBuilder builder = Json.createArrayBuilder();

        while (result.next()) {
            builder.add(result.getString("json"));
        }
        return builder.build();
    }

    public JsonArray getNearestFeatures(double latitude, double longitude) throws SQLException {
        JsonArrayBuilder builder = Json.createArrayBuilder();

        PreparedStatement statement = dbConnector.prepareStatement(QUERY_NEAREST);
        statement.setDouble(1, longitude);
        statement.setDouble(2, latitude);
        statement.setDouble(3, longitude);
        statement.setDouble(4, latitude);
        statement.setDouble(5, longitude);
        statement.setDouble(6, latitude);
        statement.executeQuery();

        ResultSet result = statement.getResultSet();

        while (result.next()) {
            builder.add(result.getString("json"));
        }

        statement.close();
        return builder.build();
    }

}
