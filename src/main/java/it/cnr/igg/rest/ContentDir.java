package it.cnr.igg.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import it.cnr.igg.helper.Commons;
import it.cnr.igg.helper.Global;
import it.cnr.igg.helper.RestResult;
import it.cnr.igg.helper.ResultBuilder;
import it.cnr.igg.isotopedb.beans.AuthorBean;
import it.cnr.igg.isotopedb.beans.DatasetBean;
import it.cnr.igg.isotopedb.beans.DatasetFullLinkBean;
import it.cnr.igg.isotopedb.exceptions.DbException;
import it.cnr.igg.isotopedb.queries.DatasetQuery;
import it.cnr.igg.sheetx.csv.Csv;
import it.cnr.igg.sheetx.xlsx.Sheetx;
import it.cnr.igg.sheetx.xlsx.Xlsx;
import it.cnr.igg.sheetx.xlsx.Xsl;

class ContentHelper {
	public ArrayList<String> sheets;
	public String key;
}

class MetadataInfo {
	public String keywords;
}

@Path("")
public class ContentDir extends ResultBuilder {
	@Context
	private HttpServletRequest request;

	@Path("/get-full-references")
	@OPTIONS
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getfullReferencesOpt() {
		return ok();
	}

	@Path("/get-full-references")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getfullReferences() {
		Gson gson = new Gson();
		DatasetQuery datasetQuery = new DatasetQuery();
		try {
			ArrayList<DatasetFullLinkBean> beans = datasetQuery.getFullLinks();
			return ok(gson.toJson(RestResult.resultOk(gson.toJson(beans), "")));
		} catch (Exception e) {
			return error(gson.toJson(RestResult.resultError("" + e.getMessage())));
		}
	}

	@Path("/insert-dataset")
	@OPTIONS
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response pushDatasetOpt() {
		return ok();
	}

	@Path("/insert-dataset")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response pushDataset() {
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
			DatasetBean bean = toDataseteBean(payload);
			DatasetQuery datasetQuery = new DatasetQuery();
			DatasetBean newBean = datasetQuery.insertDataset(bean);
			return ok(gson.toJson(RestResult.resultOk("" + gson.toJson(newBean))));
		} catch (Exception x) {
			return error(gson.toJson(RestResult.resultError("" + x.getMessage())));
		}
	}

	@Path("/delete-dataset/{id}")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteDatasetOpt(@PathParam("id") String id) {
		return ok();
	}

	@Path("/delete-dataset/{id}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteDataset(@PathParam("id") String id) {
		Gson gson = new Gson();
		try {
			DatasetQuery datasetQuery = new DatasetQuery();
			String fileName = datasetQuery.deleteDataset(Long.valueOf(id));
			deleteFile(fileName);
			return ok(gson.toJson(RestResult.resultOk("deleted")));
		} catch (Exception x) {
			return error(gson.toJson(RestResult.resultError("" + x.getMessage())));
		}
	}

	@Path("/close-dataset")
	@OPTIONS
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response closeDatasetOpt() {
		return ok();
	}

	@Path("/close-dataset")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response closeDataset() {
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
			LinkedTreeMap ltm = (LinkedTreeMap) payload.get("dataset");
			Double id = (Double) ltm.get("id");
			DatasetBean bean = new DatasetBean();
			bean.setId(Math.round(id));
			bean.setProcessed(true);
			DatasetQuery datasetQuery = new DatasetQuery();
			String fileName = datasetQuery.updateProcessed(bean);
			deleteFile(fileName);
			return ok(gson.toJson(RestResult.resultOk("" + gson.toJson(bean))));
		} catch (Exception x) {
			return error(gson.toJson(RestResult.resultError("" + x.getMessage())));
		}
	}

	private DatasetBean toDataseteBean(LinkedTreeMap payload) {
		// ArrayList<LinkedTreeMap> dataset = (ArrayList<LinkedTreeMap>)
		// payload.get("dataset");
		LinkedTreeMap ltm = (LinkedTreeMap) payload.get("dataset");
		String ref = (String) ltm.get("ref");
		String authors = (String) ltm.get("authors");
		String fileName = (String) ltm.get("file");
		String year = (String) ltm.get("year");
		String keywords = (String) ltm.get("keywords");
		Gson gson = new Gson();
		DatasetBean sb = new DatasetBean();
		sb.setFileName(fileName);
		sb.setKeywords(keywords);
		sb.setLink(ref);
		sb.setAuthors(authors);
		sb.setYear(Integer.valueOf(year));
		sb.setProcessed(false);
		return sb;
	}

	@Path("/get-available-dataset-list")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUnprocessedDatasets() {
		try {
			DatasetBean filter = new DatasetBean();
			String json = "";
			Gson gson = new Gson();
			ArrayList<DatasetBean> list = (new DatasetQuery()).getDatasets(filter, true);
			json = gson.toJson(list);
			return ok(json);
		} catch (Exception x) {
			return error(x.getMessage());
		}
	}

	@Path("/get-years")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response geYears() {
		try {
			DatasetQuery dq = new DatasetQuery();
			ArrayList<Integer> list = dq.getYears();
			String json = "";
			Gson gson = new Gson();
			json = gson.toJson(list);
			return ok(json);
		} catch (Exception x) {
			return error(x.getMessage());
		}
	}

	@Path("/get-links")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLinks() {
		try {
			DatasetQuery dq = new DatasetQuery();
			ArrayList<String> list = dq.getLinks();
			String json = "";
			Gson gson = new Gson();
			json = gson.toJson(list);
			return ok(json);
		} catch (Exception x) {
			return error(x.getMessage());
		}
	}

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
				key = Global.openXlsx(name);
				Xlsx xls = Global.getXlsx(key);
				sheets = xls.getSheets();
			} else if (name.toLowerCase().endsWith(".xls")) {
				key = Global.openXls(name);
				Xsl xls = Global.getXls(key);
				sheets = xls.getSheets();
			} else {
				// throw new Exception("Unsupported file type");
				return getUnprocessedDatasets();
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

	@Path("/set-separator")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getContentWithSeparator(@QueryParam("fileName") String fileName,
			@QueryParam("separator") String separator) {
		char recordSeparator;
		if (separator.toLowerCase().equals("tab"))
			recordSeparator = '\t';
		else
			recordSeparator = separator.charAt(0);
		Csv csv = new Csv(Global.dataFolder + Global.fileSeparator + fileName, recordSeparator);
		return getContentCsv(csv);
	}

	@Path("/get-content-csv")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getContent(@QueryParam("fileName") String fileName) {
		Csv csv = new Csv(Global.dataFolder + Global.fileSeparator + fileName);
		return getContentCsv(csv);
	}

	private Response getContentCsv(Csv csv) {
		ArrayList<ArrayList<String>> content = null;
		try {
			content = csv.getContent();
			String json = "";
			Gson gson = new Gson();
			json = gson.toJson(content);
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
					content = ((Xlsx) sheetx).getContent(sheet);
				else
					content = ((Xsl) sheetx).getContent(sheet);
				boolean itineris = Commons.checkItinerisTemplate(content.get(0));
				if (true == itineris) {
					String doi = Commons.getDoi(content);
					ArrayList<AuthorBean> authors = Commons.getAuthors(content);
				}
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
			if (key != null && key.length() > 0)
				Global.releasXls(key);
			return ok();
		} catch (Exception x) {
			return error(x.getMessage());
		}
	}

	private void deleteFile(String fileName) {
		String file = Global.dataFolder + Global.fileSeparator + fileName;
		File f = new File(file);
		f.delete();
	}
}