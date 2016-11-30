package rest;

import managers.TreesDAO;
import models.ProtectedTree;

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
 * D.
 *
 * @author mko
 */
@Path("/trees")
public class ProtectedTreesResource {

    @Inject
    TreesDAO treesDAO;

    @GET
    @Produces("application/json; charset=UTF-8")
    public JsonArray getTrees(@QueryParam(value="area") String area) {
        JsonArrayBuilder builder = Json.createArrayBuilder();

        try {
            List<ProtectedTree> protectedTrees = area == null ? treesDAO.getAllTrees()
                                            : treesDAO.getTreesByProtectedArea(area);

            for (ProtectedTree protectedTree : protectedTrees) {
                builder.add(protectedTree.toJson());
            }
        } catch (SQLException e) {
            return builder.add(e.getMessage()).build();
        }

        return builder.build();
    }
}
