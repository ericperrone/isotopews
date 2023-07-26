package it.cnr.igg.rest;

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import java.util.ArrayList;
import it.cnr.igg.helper.Global;
import it.cnr.igg.helper.ResultBuilder;
import it.cnr.igg.sheetx.xlsx.Xlsx;
import it.cnr.igg.sheetx.xlsx.Xsl;
import it.cnr.igg.sheetx.xlsx.Sheetx;

class ContentHelper {
	public ArrayList<String> sheets;
	public String key;
}

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
		String key = null;
		ArrayList<String> sheets = null;
		try {			
			if (name.toLowerCase().endsWith(".xlsx")) {
				// Global.xls = new Xls(Global.dataFolder + Global.fileSeparator + name);
				key = Global.openXlsx(name);
				Xlsx xls = Global.getXlsx(key);
				sheets = xls.getSheets();
			} else if (name.toLowerCase().endsWith(".xls")) {
				key = Global.openXls(name);
				Xsl xls = Global.getXls(key);
				sheets = xls.getSheets();
			} else {
				throw new Exception("Unsupported file type");
			}

			ContentHelper ch = new ContentHelper();
			ch.key = key;
			ch.sheets = sheets;
			String json = "";
			Gson gson = new Gson();
			json = gson.toJson(ch);
			return ok(json);
			
		} catch (Exception x) {
			return error(x.getMessage());
		}
	}
	
	@Path("/get-content-xls")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getContent(@QueryParam("sheet") String sheet, @QueryParam("key") String key) {
		try {
			if (Global.pool != null) {
				ArrayList<ArrayList<String>> content = null;
				Sheetx sheetx = Global.getSheet(key);
				if (sheetx instanceof Xlsx)
					content = ((Xlsx)sheetx).getContent(sheet);
				else
					content = ((Xsl)sheetx).getContent(sheet);
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
	
	@Path("/close-contentdir")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Response closeContentDir() {
		return ok();
	}
	
	@Path("/close-contentdir")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response closeContentDir(@QueryParam("key") String key) {
		try {
			Global.releasXls(key);
			return ok();
		} catch (Exception x) {
			return error(x.getMessage());
		}
	}

}