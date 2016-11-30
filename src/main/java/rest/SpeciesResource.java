package rest;

import managers.SpeciesDAO;
import managers.TreesDAO;
import models.Specie;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.sql.SQLException;
import java.util.List;

/**
 * @author mko
 */
@Path("/species")
public class SpeciesResource {

    @Inject
    private SpeciesDAO speciesDAO;

    @Inject
    private TreesDAO treesDAO;

    @GET
    @Produces("application/json")
    public JsonArray getAllSpecies(@QueryParam(value="area") String area) {
        JsonArrayBuilder builder = Json.createArrayBuilder();

        try {
            List<Specie> species = area == null ? speciesDAO.getAllSpecies()
                                                : speciesDAO.getSpeciesByProtectedArea(area);

            for (Specie specie : species) {
                builder.add(specie.toJson());
            }
        } catch (SQLException e) {
            return builder.add(e.getMessage()).build();
        }

        return builder.build();
    }

}
