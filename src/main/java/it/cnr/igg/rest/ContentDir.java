package it.cnr.igg.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import java.util.ArrayList;
import it.cnr.igg.helper.Global;
import it.cnr.igg.helper.ResultBuilder;
import it.cnr.igg.sheetx.xls.Xls;

@Path("")
public class ContentDir extends ResultBuilder {
	@Path("/contentdir")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response contentDir() {
		try {
			ArrayList<String> list = Global.contentDir();
			String json = "";
			Gson gson = new Gson();
			json = gson.toJson(list);
			return ok(json);
		} catch (Exception x) {
			return error(x.getMessage());
		}
	}
	
	@Path("/process-file")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response processFile(@QueryParam("fileName") String name) {
		try {
			if (name.toLowerCase().endsWith(".xlsx")) {
				Global.xls = new Xls(Global.dataFolder + Global.fileSeparator + name);
				ArrayList<String> sheets = Global.xls.getSheets();
				String json = "";
				Gson gson = new Gson();
				json = gson.toJson(sheets);
				return ok(json);
			} else {
				throw new Exception("Unsupported file type");
			}
		} catch (Exception x) {
			return error(x.getMessage());
		}
	}
	
	@Path("/get-content-xls")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getContent(@QueryParam("sheet") String sheet) {
		try {
			if (Global.xls != null) {
				ArrayList<ArrayList<String>> content = Global.xls.getContent(sheet);
				String json = "";
				Gson gson = new Gson();
				json = gson.toJson(content);
				return ok(json);
			} else {
				throw new Exception("Invalid request");
			}
		} catch (Exception x) {
			return error(x.getMessage());
		}
	}

}