package it.cnr.igg.rest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import it.cnr.igg.helper.RestResult;
import it.cnr.igg.helper.ResultBuilder;
import it.cnr.igg.isotopedb.beans.ComponentBean;
import it.cnr.igg.isotopedb.beans.SampleBean;
import it.cnr.igg.isotopedb.beans.SampleFieldBean;
import it.cnr.igg.isotopedb.exceptions.DbException;
import it.cnr.igg.isotopedb.queries.MainQuery;
import it.cnr.igg.isotopedb.tools.QueryFilter;
import it.cnr.igg.models.TableSampleBean;

@Path("")
public class Query extends ResultBuilder {
	@Context
	private HttpServletRequest request;

	@Path("/get-samples")
	@OPTIONS
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSamplesOpt() {
		return ok("");
	}
	
	@Path("/get-samples")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSamples(@QueryParam("meta") String meta, 
			@QueryParam("auth") String authors,
			@QueryParam("ref") String ref,
			@QueryParam("year") String year) {
		try {
			ArrayList<SampleBean> beans = querySamples(meta, authors, ref, year);
			TableSampleBean tsb = new TableSampleBean();
			tsb.build(beans, true);
			String json = "";
			Gson gson = new Gson();
			json = gson.toJson(tsb.getBody());
			return ok(json);
		} catch (Exception ex) {
			return error(ex.getMessage());
		}
	}
	
	
	@Path("/query")
	@OPTIONS
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response queryOpt() {
		return ok("");
	}

	@Path("/query")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response query(@QueryParam("meta") String meta, 
			@QueryParam("auth") String authors,
			@QueryParam("ref") String ref,
			@QueryParam("year") String year) {
		try {
			ArrayList<SampleBean> beans = querySamples(meta, authors, ref, year);
			String json = "";
			Gson gson = new Gson();
			json = gson.toJson(beans);
			return ok(json);
		} catch (Exception ex) {
			return error(ex.getMessage());
		}
	}
	
	private ArrayList<SampleBean> querySamples(String meta,
			String authors,
			String ref,
			String year) throws Exception, DbException {
		MainQuery mainQuery = new MainQuery();
		QueryFilter filter = new QueryFilter();
		if (meta != null) {
			filter.keywords = meta.split(" ");
		}
		if (authors != null) {
			filter.authors = authors.split(",");
		}
		if (ref != null) {
			filter.ref = ref;
		}
		if (year != null) {
			filter.year = Integer.valueOf(year);
		}
		ArrayList<SampleBean> beans = mainQuery.query(filter);
		return beans;
	}

}
