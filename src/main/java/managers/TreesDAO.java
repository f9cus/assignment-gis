package managers;

import models.ProtectedTree;
import org.intellij.lang.annotations.Language;
import util.DatabaseConnector;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mko
 */
public class TreesDAO {

    private static final String QUERY_ALL
            = "select nazov, nazovsk, nazovlat, datumvyhla, fotourl, urlpredpis, legislativ, posobnost, katasterna, "
            + "st_asgeojson(st_transform(geom, 4326)) as geometry "
            + "from protected_trees";

    @Language("SQL")
    private static final String QUERY_BY_PROTECTED_AREA
            = "select distinct t.nazov, t.urlpredpis from protected_trees t "
            + "join protected_areas_transformed a on st_within(st_transform(t.geom, 4326), a.geom) "
            + "where a.nazov = ?";

    @Inject
    private DatabaseConnector dbConnector;

    public List<ProtectedTree> getAllTrees() throws SQLException {
        List<ProtectedTree> protectedTrees = new ArrayList<>();

        Statement statement = dbConnector.createStatement();
        ResultSet result = statement.executeQuery(QUERY_ALL);

        while (result.next()) {
            protectedTrees.add(ProtectedTree.fromResultSet(result));
        }

        statement.close();
        return protectedTrees;
    }

    public List<ProtectedTree> getTreesByProtectedArea(String area) throws SQLException {
        List<ProtectedTree> protectedTrees = new ArrayList<>();

        PreparedStatement statement = dbConnector.prepareStatement(QUERY_BY_PROTECTED_AREA);
        statement.setString(1, area);
        statement.executeQuery();

        ResultSet result = statement.getResultSet();

        while (result.next()) {
            protectedTrees.add(ProtectedTree.fromSimpleResult(result));
        }

        statement.close();
        return protectedTrees;
    }
}
