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

import it.cnr.igg.helper.Commons;
import it.cnr.igg.helper.RestResult;
import it.cnr.igg.helper.ResultBuilder;
import it.cnr.igg.isotopedb.beans.AuthorBean;
import it.cnr.igg.isotopedb.beans.ComponentBean;
import it.cnr.igg.isotopedb.beans.DatasetBean;
import it.cnr.igg.isotopedb.beans.MatrixBean;
import it.cnr.igg.isotopedb.beans.SampleBean;
import it.cnr.igg.isotopedb.beans.SampleFieldBean;
import it.cnr.igg.isotopedb.queries.SampleQuery;
import it.cnr.igg.isotopedb.queries.AdministratorQuery;
import it.cnr.igg.isotopedb.queries.MatrixQuery;

class ExternalSampleResult {
	public String status;
	public int result;
}

@Path("")
public class Sample extends ResultBuilder {
	@Context
	private HttpServletRequest request;

	@Path("/query-samples-by-id")
	@OPTIONS
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response querySamplesByIdOpt() {
		return ok("");
	}

	@Path("/query-samples-by-id")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response querySamplesById(@QueryParam("ids") String ids) {
		try {
			SampleQuery sampleQuery = new SampleQuery();
			ArrayList<Integer> idList = new ArrayList<Integer>();
			String[] list = ids.split(",");
			if (list.length <= 0) {
				throw new Exception("Bad parameters (empty list)");
			}
			for (String s : list) {
				idList.add(Integer.parseInt(s.trim()));
			}
			ArrayList<SampleBean> beans = sampleQuery.querySamplesById(idList);
			String json = "";
			Gson gson = new Gson();
			json = gson.toJson(beans);
			return ok(json);
		} catch (Exception ex) {
			return error(ex.getMessage());
		}
	}

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

	@Path("/insert-fulldata")
	@OPTIONS
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response insertfullDataOpt() {
		return ok("");
	}

	@Path("/insert-fulldata")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response insertfullData() {
		Gson gson = new Gson();
		try {
			String key = Commons.getKeyFromHeader(request);
			AdministratorQuery aq = new AdministratorQuery();
			aq.checkKey(key);

			final BufferedReader rd = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));

			String line = null;
			final StringBuffer buffer = new StringBuffer(2048);

			while ((line = rd.readLine()) != null) {
				buffer.append(line);
			}
			final String data = buffer.toString();

			LinkedTreeMap payload = gson.fromJson(data, LinkedTreeMap.class);
			SampleBean sample = getSample((LinkedTreeMap) payload.get("data"));
			DatasetBean dataset = getDatasetInfo((LinkedTreeMap) payload.get("data"));
			ArrayList<AuthorBean> authors = getAuthorList((LinkedTreeMap) payload.get("data"));
			ArrayList<MatrixBean> matrices = getMatrices((LinkedTreeMap) payload.get("data"));
			if (matrices != null && matrices.size() > 0) {
				sample.setMatrix(matrices.get(0));
			}

			SampleQuery sq = new SampleQuery();
			int result = sq.insertExternalSample(authors, dataset, sample);
			ExternalSampleResult res = new ExternalSampleResult();
			res.status = "success";
			res.result = result;
			return ok(gson.toJson(res));
		} catch (Exception x) {
			x.printStackTrace();
			return error(gson.toJson(RestResult.resultError("" + x.getMessage())));
		}

	}

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
			String key = Commons.getKeyFromHeader(request);
			AdministratorQuery aq = new AdministratorQuery();
			aq.checkKey(key);

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

	private ArrayList<AuthorBean> getAuthorList(LinkedTreeMap payload) {
		ArrayList<AuthorBean> beans = new ArrayList<AuthorBean>();
		ArrayList<LinkedTreeMap> authors = (ArrayList<LinkedTreeMap>) payload.get("authors");
		for (int i = 0; i < authors.size(); i++) {
			LinkedTreeMap a = authors.get(i);
			AuthorBean bean = new AuthorBean("" + a.get("name"), "" + a.get("surname"));
			beans.add(bean);
		}
		return beans;
	}
	
	private ArrayList<MatrixBean> getMatrices(LinkedTreeMap payload) {
		ArrayList<MatrixBean> beans = new ArrayList<MatrixBean>();
		ArrayList<LinkedTreeMap> m = (ArrayList<LinkedTreeMap>) payload.get("matrix");
		for (int i = 0; i < m.size(); i++) {
			String a = "" + m.get(i);
			try {
				beans = (new MatrixQuery()).getByName(a);
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
		return beans;
	}

	private SampleBean getSample(LinkedTreeMap payload) {
		SampleBean sb = new SampleBean();
		LinkedTreeMap sample = (LinkedTreeMap) payload.get("sample");
		if (sample != null) {
			ArrayList<LinkedTreeMap> components = (ArrayList<LinkedTreeMap>) sample.get("components");
			ArrayList<LinkedTreeMap> fields = (ArrayList<LinkedTreeMap>) sample.get("fields");
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
				Object um = c.get("um");
				try {
					Double val = Double.parseDouble("" + value);
					boolean isIsotope = (boolean) isotope;
					sb.getComponents().add(new ComponentBean((String) component, val, isIsotope, (String)um));
				} catch (Exception x) {
					// x.printStackTrace();
				}
			}
		}
		return sb;
	}

	private DatasetBean getDatasetInfo(LinkedTreeMap payload) {
		LinkedTreeMap dataset = (LinkedTreeMap) payload.get("dataset");
		DatasetBean bean = new DatasetBean();
		bean.setLink(dataset.get("ref") != null ? "" + dataset.get("ref") : null);
		bean.setAuthors(dataset.get("authors") != null ? "" + dataset.get("authors") : null);
		bean.setFileName(dataset.get("fileName") != null ? "" + dataset.get("fileName") : null);
		bean.setYear(dataset.get("year") != null ? Double.valueOf("" + dataset.get("year")).intValue() : null);
		bean.setMetadata(dataset.get("metadata") != null ? "" + dataset.get("metadata") : null);
		bean.setProcessed(true);
		return bean;
	}

	private ArrayList<SampleBean> toSampleBean(LinkedTreeMap payload) {
		ArrayList<SampleBean> beans = new ArrayList<SampleBean>();
		ArrayList<LinkedTreeMap> samples = (ArrayList<LinkedTreeMap>) payload.get("samples");
		for (int i = 0; i < samples.size(); i++) {
			LinkedTreeMap ltm = samples.get(i);
			Long datasetId = ((Double) ltm.get("datasetId")).longValue();
			ArrayList<LinkedTreeMap> fields = (ArrayList<LinkedTreeMap>) ltm.get("fields");
			ArrayList<LinkedTreeMap> components = (ArrayList<LinkedTreeMap>) ltm.get("components");
			SampleBean sb = new SampleBean();
			if (datasetId >= 0)
				sb.setDatasetId(datasetId);
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
					Double val = toDouble("" + value);
					boolean isIsotope = (boolean) isotope;
					sb.getComponents().add(new ComponentBean((String) component, val, isIsotope));
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
			beans.add(sb);
		}
		return beans;
	}
	
	public Double toDouble(String value) {
		int comma = value.lastIndexOf(',');
		int point = value.lastIndexOf('.');
		if (comma < point) {
			value = value.replaceAll(",", "");
		} else {
			value = value.replace(",", ".");
		}
		return Double.parseDouble(value);
	}
//	
//	public static void main(String[] args) {
//		String val = "12001";
//		Sample sample = new Sample();
//		double dVal = sample.toDouble(val);
//		System.out.println(dVal);
//	}
}
