package it.cnr.igg.helper;

import javax.servlet.http.HttpServletRequest;

public class Commons {
	public static final String KEY = "token";
	
	public Commons() {
	}
	
	public static String getKeyFromHeader(HttpServletRequest request) throws Exception {
		String key = request.getHeader(Commons.KEY);
		if (key == null)
			throw new Exception("Missing authorization key");
		return key;
	}

}
