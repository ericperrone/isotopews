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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import it.cnr.igg.helper.RestResult;
import it.cnr.igg.helper.ResultBuilder;

import it.cnr.igg.geomodels.Mixing;
import it.cnr.igg.geomodels.GeoData;

@Path("")
public class MixingModel extends ResultBuilder {
	@Context
	private HttpServletRequest request;

	@Path("/mixing-model")
	@OPTIONS
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response mixingOpt() {
		return ok("");
	}

	@Path("/mixing-model")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response mixing() {
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
			GeoData[] geoData = jsonToGeoData(payload);
			
			Mixing mixing = new Mixing(geoData);
			mixing.apply();
			Object result = mixing.getResult();
			return ok(gson.toJson(result));
		} catch (Exception x) {
			return error(gson.toJson(RestResult.resultError("" + x.getMessage())));
		}
	}
	
	private GeoData[] jsonToGeoData(LinkedTreeMap payload) {
		ArrayList<GeoData> geoData = new ArrayList<GeoData>();
		ArrayList<LinkedTreeMap> data = (ArrayList<LinkedTreeMap>) payload.get("data");
		for (int i = 0; i < data.size(); i++) {
			LinkedTreeMap item = data.get(i);
			String element = (String)item.get("element");
			ArrayList<LinkedTreeMap> members = (ArrayList<LinkedTreeMap>)item.get("members");
			GeoData gd = new GeoData();
			gd.setElement(element);
			gd.setMembers(members.size());
			for (int j = 0; j < members.size(); j++) {
				String member = (String)members.get(j).get("member");
				Double value =  Double.valueOf("" + members.get(j).get("value"));
				gd.setMember(member, value, j);
			}
			geoData.add(gd);
		}
		return (GeoData[])geoData.toArray(new GeoData[geoData.size()]);
	}

}
