package it.cnr.igg.helper;

import javax.ws.rs.core.Response;

public class ResultBuilder {

	public Response ok() {
		return Response.ok().header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.header("Access-Control-Allow-Headers", "*").header("Content-Type", "application/json; charset=UTF-8")
				.build();
	}
	
	public Response ok(Object entity) {
		return Response.ok().entity(entity).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.header("Access-Control-Allow-Headers", "*").header("Content-Type", "application/json; charset=UTF-8")
				.build();
	}

	public Response error(Object entity) {
		return Response.serverError().entity(entity).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.header("Access-Control-Allow-Headers", "*").header("Content-Type", "application/json; charset=UTF-8")
				.build();
	}

}
