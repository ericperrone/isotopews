<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="org.apache.poi.ss.usermodel.*"%>
<%@ page import="org.apache.poi.xssf.usermodel.XSSFWorkbook"%>
<%@ page import="it.cnr.igg.sheetx.xlsx.*"%>
<%@ page import="it.cnr.igg.models.SheetBean" %>
<%@ page import="it.cnr.igg.helper.Global"%>
<%@ page import="com.google.gson.Gson"%>

<%
String inputFile = null;
String selectedSheet = null;
ArrayList<String> dir = null;
%>
<div class="fragment-start">
	<div id="sheet-processor">
		<%
		Gson gson = new Gson();
		SheetBean sheet = gson.fromJson(Global.receivedPayLoad, SheetBean.class);
		ArrayList<ArrayList<String>> content = Global.xls.getContent(sheet.getSheet());
		%>
		<div style="padding: 5px;">
			<p id="step-one" class="text-bold">
				Select the fields describing the sample. When done click "Submit"
				button.
				<button type="button" class="btn btn-primary"
					onclick="submitSamples()">Submit</button>
			</p>
			<p id="step-two" class="text-bold" style="display: none">
				Select the chemical elements. When done click "Submit" button.
				<button type="button" class="btn btn-primary">Submit</button>
				<%=Global.receivedPayLoad%>
			</p>
		</div>
		<div>
			<table class="table table-bordered">
				<tbody>
					<%
					int counter = 0;
					int size = content.size();
					size = 20;
					for (int i = 0; i < size; i++) {
						ArrayList<String> row = content.get(i);
					%>
					<tr>
						<%
						for (String r : row) {
						%>
						<td class="clickable xslx-fragment"
							onclick="toggleSelection(<%="" + counter%>)"
							id="<%="" + counter%>"><%=r != null ? r : "&nbsp;"%></td>
						<%
						counter++;
						}
						%>
					</tr>
					<%
					}
					%>
				</tbody>
			</table>
		</div>
	</div>
</div>