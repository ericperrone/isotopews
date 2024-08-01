package it.cnr.igg.rest;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import it.cnr.igg.helper.ResultBuilder;
import it.cnr.igg.isotopedb.beans.MatrixBean;
import it.cnr.igg.isotopedb.queries.MatrixQuery;

@Path("")
public class Matrix extends ResultBuilder {

	@Path("/matrix")
	@OPTIONS
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMatricesOpt() {
		return ok("");
	}
	
	@Path("/matrix")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMatricesOpt(@QueryParam("ids") String ids) {
		try {
			MatrixQuery query = new MatrixQuery();
			ArrayList<MatrixBean> beans = query.getAll();

			String json = "";
			Gson gson = new Gson();
			json = gson.toJson(beans);
			return ok(json);
		} catch (Exception ex) {
			return error(ex.getMessage());
		}
	}
}
