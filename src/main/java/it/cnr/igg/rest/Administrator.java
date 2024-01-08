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
import it.cnr.igg.isotopedb.beans.AdministratorBean;
import it.cnr.igg.isotopedb.beans.ComponentBean;
import it.cnr.igg.isotopedb.beans.DatasetBean;
import it.cnr.igg.isotopedb.beans.SampleBean;
import it.cnr.igg.isotopedb.beans.SampleFieldBean;
import it.cnr.igg.isotopedb.queries.AdministratorQuery;

class AccessKey {
	public String status;
	public String key;
}

@Path("")
public class Administrator extends ResultBuilder {
	@Context
	private HttpServletRequest request;

	@Path("/insertAdministrator")
	@OPTIONS
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response insertOpt() {
		return ok("");
	}

	@Path("/insertAdministrator")
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
			AdministratorBean bean = getAdministratorBean((LinkedTreeMap) payload.get("data"));
			AdministratorQuery aq = new AdministratorQuery();
			bean = aq.putAdministrator(bean);
			return ok(gson.toJson(bean));
		} catch (Exception x) {
			return error(gson.toJson(RestResult.resultError("" + x.getMessage())));
		}
	}

	@Path("/login")
	@OPTIONS
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response loginOpt() {
		return ok("");
	}

	@Path("/login")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login() {
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
			AdministratorBean bean = getAdministratorBean((LinkedTreeMap) payload.get("data"));

			AdministratorQuery aq = new AdministratorQuery();
			String token = aq.login(bean);
			AccessKey key = new AccessKey();
			key.status= "success";
			key.key = token;
			return ok(gson.toJson(key));
		} catch (Exception x) {
			return error(gson.toJson(RestResult.resultError("" + x.getMessage())));
		}
	}

	private AdministratorBean getAdministratorBean(LinkedTreeMap data) {
		String account = "" + data.get("account");
		String password = "" + data.get("password");
		String email = "" + data.get("email");
		return new AdministratorBean(account, password, email);
	}

}
