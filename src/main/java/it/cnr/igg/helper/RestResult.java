package it.cnr.igg.helper;

public class RestResult {
	private String status;
	private String message;
	
	public RestResult(String status, String message) {
		this.status = status;
		this.message = message;
	}
	
	public static RestResult resultOk(String message) {
		RestResult rr = new RestResult("success", message);
		return rr;
	}

	public static RestResult resultError(String message) {
		RestResult rr = new RestResult("error", message);
		return rr;
	}
}
