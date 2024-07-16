package it.cnr.igg.helper;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

public class Commons {
	public static final String KEY = "token";
	public static final String ITINERIS_DOC = "itineris document";
	
	public Commons() {
	}
	
	public static String getKeyFromHeader(HttpServletRequest request) throws Exception {
		String key = request.getHeader(Commons.KEY);
		if (key == null)
			throw new Exception("Missing authorization key");
		return key;
	}
	
	public static boolean checkItinerisTemplate(ArrayList<String> header) {
		for (String h : header) {
			String local = h.toLowerCase();
			local = local.replaceAll("_", " ");
			local = local.replaceAll("-", " ");
			if (ITINERIS_DOC.equals(local))
				return true;
		}
		return false;
	}

}
