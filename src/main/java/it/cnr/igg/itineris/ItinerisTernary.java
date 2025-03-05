package it.cnr.igg.itineris;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import it.cnr.igg.helper.Commons;
import it.cnr.igg.helper.ItinerisResult;
import it.cnr.igg.helper.RestResult;
import it.cnr.igg.helper.ResultBuilder;
import it.cnr.igg.isotopedb.exceptions.DbException;
import it.cnr.igg.isotopedb.exceptions.NotAuthorizedException;
import it.cnr.igg.isotopedb.queries.ItinerisCommon;
import it.cnr.igg.itineris.NoKeyException;
import it.cnr.igg.itineris.beans.ItinerisTernaryInputBean;

@Path("")
public class ItinerisTernary extends ResultBuilder {
	@Context
	private HttpServletRequest request;

	@Path("/compute-ternary")
	@OPTIONS
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response ternaryOpt() {
		return ok("");
	}

	@Path("/compute-ternary")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response ternary() {
		Gson gson = new Gson();
		try {
			String key = Commons.getItinerisKeyFromHeader(request);
			
			new ItinerisCommon().checkItinerisKey(key);
			
			final BufferedReader rd = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));

			String line = null;
			final StringBuffer buffer = new StringBuffer(2048);

			while ((line = rd.readLine()) != null) {
				buffer.append(line);
			}
			final String data = buffer.toString();

			LinkedTreeMap payload = gson.fromJson(data, LinkedTreeMap.class);
			
			ItinerisTernaryInputBean bean = getInput(payload);
			
			return ok(gson.toJson(RestResult.resultOk(null, "Success")));

		} catch (Exception x) {
			return error(gson.toJson(RestResult.resultError("" + x.getMessage())));
		}	
	}
	
	private ItinerisTernaryInputBean getInput(LinkedTreeMap payload) {
		ItinerisTernaryInputBean itib = new ItinerisTernaryInputBean();
		ArrayList<LinkedTreeMap> elements = (ArrayList<LinkedTreeMap>)payload.get("elements");
		LinkedTreeMap e = elements.get(0);
		ArrayList<Double> values = (ArrayList<Double>) e.get("values");
		itib.setA((String)e.get("element"), values);
		e = elements.get(1);
		values = (ArrayList<Double>) e.get("values");
		itib.setB((String)e.get("element"), values);
		e = elements.get(2);
		values = (ArrayList<Double>) e.get("values");
		itib.setC((String)e.get("element"), values);
		return itib;
	}
	
	private void compute(ItinerisTernaryInputBean bean) {
		
	}

}
