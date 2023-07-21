package it.cnr.igg.rest;

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
import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import it.cnr.igg.helper.Global;
import it.cnr.igg.helper.ResultBuilder;
import it.cnr.igg.sheetx.xls.Xls;
import it.cnr.igg.isotopedb.beans.SampleBean;
import it.cnr.igg.isotopedb.beans.SampleFieldBean;
import it.cnr.igg.isotopedb.beans.ComponentBean;
import it.cnr.igg.isotopedb.queries.SampleQuery;
import it.cnr.igg.helper.RestResult;

@Path("")
public class Sample extends ResultBuilder {
	@Context
	private HttpServletRequest request;

	@Path("/query-samples")
	@OPTIONS
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response querySamplesOpt() {
		return ok("");
	}

	@Path("/query-samples")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response querySamples() {
		try {
			SampleQuery sampleQuery = new SampleQuery();
			ArrayList<SampleBean> beans = sampleQuery.querySamples(null);
			String json = "";
			Gson gson = new Gson();
			json = gson.toJson(beans);
			return ok(json);
		} catch (Exception ex) {
			return error(ex.getMessage());
		}
	}
/*	
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
	public Response getSamples() {
		try {
			SampleQuery sampleQuery = new SampleQuery();
			ArrayList<SampleBean> beans = sampleQuery.getSampleList(null);
			String json = "";
			Gson gson = new Gson();
			json = gson.toJson(beans);
			return ok(json);
		} catch (Exception ex) {
			return error(ex.getMessage());
		}
	}
	
	@Path("/get-sample")
	@OPTIONS
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSampleOpt() {
		return ok("");
	}

	@Path("/get-sample")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSample() {
		try {
			SampleQuery sampleQuery = new SampleQuery();
			ArrayList<SampleBean> beans = sampleQuery.getSamples(null);
			String json = "";
			Gson gson = new Gson();
			json = gson.toJson(beans);
			return ok(json);
		} catch (Exception ex) {
			return error(ex.getMessage());
		}
	}
*/
	@Path("/insert-sample")
	@OPTIONS
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response insertOpt() {
		return ok("");
	}

	@Path("/insert-sample")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response insert() {
		Gson gson = new Gson();
		try {
			final BufferedReader rd = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));

			String line = null;
			final StringBuffer buffer = new StringBuffer(2048);

			while ((line = rd.readLine()) != null) {
				buffer.append(line);
			}
			final String data = buffer.toString();
			
			LinkedTreeMap payload = gson.fromJson(data, LinkedTreeMap.class);
			ArrayList<SampleBean> beans = toSampleBean(payload);
			SampleQuery sampleQuery = new SampleQuery();
			sampleQuery.insertSamples(beans);
			return ok(gson.toJson(RestResult.resultOk("")));
		} catch (Exception x) {
			return error(gson.toJson(RestResult.resultError("" + x.getMessage())));
		}
	}

	private ArrayList<SampleBean> toSampleBean(LinkedTreeMap payload) {
		ArrayList<SampleBean> beans = new ArrayList<SampleBean>();
		ArrayList<LinkedTreeMap> samples = (ArrayList<LinkedTreeMap>) payload.get("samples");
		for (int i = 0; i < samples.size(); i++) {
			LinkedTreeMap ltm = samples.get(i);
			ArrayList<LinkedTreeMap> fields = (ArrayList<LinkedTreeMap>) ltm.get("fields");
			ArrayList<LinkedTreeMap> components = (ArrayList<LinkedTreeMap>) ltm.get("components");
			SampleBean sb = new SampleBean();
			sb.setFields(new ArrayList<SampleFieldBean>());
			sb.setComponents(new ArrayList<ComponentBean>());
			for (LinkedTreeMap s : fields) {
				Object field = s.get("field");
				Object value = s.get("value");
				sb.getFields().add(new SampleFieldBean((String) field, (String) value));
			}
			for (LinkedTreeMap c : components) {
				Object component = c.get("component");
				Object value = c.get("value");
				Object isotope = c.get("isIsotope");
				try {
					Double val = Double.parseDouble("" + value);
					boolean isIsotope = (boolean) isotope;
					sb.getComponents().add(new ComponentBean((String) component, val, isIsotope));
				} catch (Exception x) {
					// x.printStackTrace();
				}
			}
			beans.add(sb);
		}
		return beans;
	}
}
