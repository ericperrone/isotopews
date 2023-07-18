<%@page import="javax.validation.Payload"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.io.BufferedReader"%>
<%@ page import="java.nio.CharBuffer"%>
<%@ page import="it.cnr.igg.helper.Global"%>
<% 
	BufferedReader reader = request.getReader();
	// reader.read(target)
	String payload = "", line = "";
	while (line != null) {
		line = reader.readLine();
		if (line != null) 
			payload += line;
	}
	Global.receivedPayLoad = payload;
%>
