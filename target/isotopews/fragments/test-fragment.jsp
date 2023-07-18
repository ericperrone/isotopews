<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="org.apache.poi.ss.usermodel.*"%>
<%@ page import="org.apache.poi.xssf.usermodel.XSSFWorkbook"%>
<%@ page import="it.cnr.igg.sheetx.xls.*"%>
<%
Xls xls = new Xls("\\dev\\2022_09-0SVW6S_Stracke_data.xlsx");
ArrayList<ArrayList<String>> content = xls.getContent("Data_MORB");
%>
<div class="fragment-start">
	<div>
		<p id="step-one">Select the fields describing the sample. When
			done click "Submit" button.</p>
		<p id="step-two" style="display: none">Select the chemical
			elements. When done click "Submit" button.</p>
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
					<td class="clickable Data processing-fragment" onclick="toggleSelection(<%= "" + counter %>)" id="<%= "" + counter %>"><%=r != null ? r : "&nbsp;"%></td>					
					<%
					counter ++;
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