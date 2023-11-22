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
import it.cnr.igg.isotopedb.tools.GeoCoord;
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
	public Response getSamples(@QueryParam("meta") String meta, @QueryParam("auth") String authors,
			@QueryParam("ref") String ref, @QueryParam("year") String year, @QueryParam("x0") String x0,
			@QueryParam("x1") String x1, @QueryParam("y0") String y0, @QueryParam("y1") String y1) {
		try {
			QueryFilter filter = initFilter(meta, authors, ref, year, x0, y0, x1, y1);
			ArrayList<SampleBean> beans = querySamples(filter);
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
	public Response query(@QueryParam("meta") String meta, @QueryParam("auth") String authors,
			@QueryParam("ref") String ref, @QueryParam("year") String year, @QueryParam("x0") String x0,
			@QueryParam("x1") String x1, @QueryParam("y0") String y0, @QueryParam("y1") String y1) {
		try {
			QueryFilter filter = initFilter(meta, authors, ref, year, x0, y0, x1, y1);
			ArrayList<SampleBean> beans = querySamples(filter);
			String json = "";
			Gson gson = new Gson();
			json = gson.toJson(beans);
			return ok(json);
		} catch (Exception ex) {
			return error(ex.getMessage());
		}
	}

	private QueryFilter initFilter(String meta, String authors, String ref, String year, String x0, String y0,
			String x1, String y1) {
		QueryFilter filter = new QueryFilter();
		if (x0 != null && x0.length() > 0 && y0 != null && y0.length() > 0 && x1 != null && x1.length() > 0
				&& y1 != null && y1.length() > 0) {
			Double lat0 = Double.valueOf(y0);
			Double lon0 = Double.valueOf(x0);
			Double lat1 = Double.valueOf(y1);
			Double lon1 = Double.valueOf(x1);
			Double minLatitude = lat0 <= lat1 ? lat0 : lat1;
			Double maxLatitude = lat0 <= lat1 ? lat1 : lat0;
			Double minLongitude = lon0 <= lon0 ? lon0 : lon1;
			Double maxLongitude = lon0 <= lon1 ? lon1 : lon0;
			filter.geoCoord = new GeoCoord(minLatitude, minLongitude, maxLatitude, maxLongitude);
		}
		if (meta != null) {
			filter.keywords = meta.split(" ");
		}
		if (authors != null) {
			filter.authors = authors.split(";");
		}
		if (ref != null) {
			filter.ref = ref;
		}
		if (year != null) {
			filter.year = Integer.valueOf(year);
		}

		return filter;
	}

	private ArrayList<SampleBean> querySamples(QueryFilter filter)
			throws Exception, DbException {
		MainQuery mainQuery = new MainQuery();
		ArrayList<SampleBean> beans = mainQuery.query(filter);
		return beans;
	}

}
