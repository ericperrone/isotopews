<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="org.apache.poi.ss.usermodel.*"%>
<%@ page import="org.apache.poi.xssf.usermodel.XSSFWorkbook"%>
<%@ page import="it.cnr.igg.sheetx.xlsx.*"%>
<%@ page import="it.cnr.igg.models.SheetBean"%>
<%@ page import="it.cnr.igg.helper.Global"%>
<%@ page import="com.google.gson.Gson"%>
<div class="fragment-start">
	<div id="sheet-selector">
		<ul class="list-group">
			<%
			Global.xls = new Xlsx("\\dev\\2022_09-0SVW6S_Stracke_data.xlsx");
				ArrayList<String> sheets = Global.xls.getSheets();
				for (int i = 0; i < sheets.size(); i++) {
					String s = sheets.get(i);
			%>
			<li class="clickable list-group-item" onclick="selectSheet('<%=s%>')"><%=s%></li>
			<%
			}
			%>
		</ul>
	</div>
</div>