package rest;

import managers.ProtectedAreasDAO;
import models.ProtectedArea;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.sql.SQLException;

/**
 * @author mko
 */
@Path("/areas")
public class ProtectedAreasResource {

    @Inject
    private ProtectedAreasDAO protectedAreasDAO;

    @GET
    @Produces("application/json; charset=UTF-8")
    public JsonArray getAllAreas() {
        JsonArrayBuilder builder = Json.createArrayBuilder();

        try {
            for (ProtectedArea protectedArea : protectedAreasDAO.getAllAreas()) {
                builder.add(protectedArea.toJson());
            }
        } catch (SQLException e) {
            return builder.add(e.getMessage()).build();
        }

        return builder.build();
    }

    @GET
    @Path("/totalSizes")
    @Produces("application/json; charset=UTF-8")
    public JsonArray getTotalSizes(@QueryParam(value="area") String area) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        try {
            return protectedAreasDAO.getAggregateAreaSizes();
        } catch (SQLException e) {
            return builder.add(e.getMessage()).build();
        }
    }

    @GET
    @Path("/nearestFeatures/{lat}/{long}")
    @Produces("application/json; charset=UTF-8")
    public JsonArray getNearestFeatures(
            @PathParam("lat") double latitude,
            @PathParam("long") double longitude
    ) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        try {
            return protectedAreasDAO.getNearestFeatures(latitude, longitude);
        } catch (SQLException e) {
            return builder.add(e.getMessage()).build();
        }
    }

}
