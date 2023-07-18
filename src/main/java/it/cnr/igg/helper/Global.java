package it.cnr.igg.helper;
import java.util.ArrayList;
import java.io.File;
import it.cnr.igg.sheetx.xls.Xls;


public class Global {
	public static String fileSeparator = File.separator;
	public static String receivedPayLoad = null;
	public static String dataFolder = fileSeparator + "dev";
	public static Xls xls = null;
	
	public static ArrayList<String> contentDir() {
		ArrayList<String> dir = new ArrayList<String>();
		File folder = new File(dataFolder);
		
		for (final File entry : folder.listFiles()) {
			if (entry.isDirectory())
				continue;
			String name = entry.getName();
			if (name.endsWith(".xlsx")) {
				dir.add(name);
			}
		}
		return dir;
	}
}
