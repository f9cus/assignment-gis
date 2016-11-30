package managers;

import models.Specie;
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
public class SpeciesDAO {

    private static final String QUERY_ALL
            = "select st_asgeojson(st_transform(s.geom, 4326)) as geometry, s.species_na from species s";

    @Language("SQL")
    private static final String QUERY_BY_PROTECTED_AREA
            = "select distinct s.species_na, s.plant from species_transformed s "
            + "join protected_areas_transformed a on st_intersects(s.geom, a.geom) "
            + "where a.nazov = ?";

    @Inject
    DatabaseConnector dbConnector;

    public List<Specie> getAllSpecies() throws SQLException {
        List<Specie> species = new ArrayList<>();

        Statement statement = dbConnector.createStatement();
        ResultSet result = statement.executeQuery(QUERY_ALL);

        while (result.next()) {
            species.add(Specie.fromResultSet(result));
        }

        statement.close();
        return species;
    }

    public List<Specie> getSpeciesByProtectedArea(String protectedArea) throws SQLException {
        List<Specie> species = new ArrayList<>();

        PreparedStatement statement = dbConnector.prepareStatement(QUERY_BY_PROTECTED_AREA);
        statement.setString(1, protectedArea);
        statement.executeQuery();

        ResultSet result = statement.getResultSet();

        while (result.next()) {
            species.add(Specie.fromResultSet(result));
        }

        statement.close();
        return species;
    }

}
