package it.cnr.igg.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import it.cnr.igg.sheetx.exceptions.SheetxException;
import it.cnr.igg.sheetx.xls.Xls;
import it.cnr.igg.sheetx.helper.HexFilter;

public class Global {
	public static String fileSeparator = File.separator;
	public static String receivedPayLoad = null;
	public static String dataFolder = fileSeparator + "dev";
	public static Xls xls = null;
	public static HashMap<String, Xls> pool = null;

	public static ArrayList<String> contentDir() {
		ArrayList<String> dir = new ArrayList<String>();
		File folder = new File(dataFolder);

		for (final File entry : folder.listFiles()) {
			if (entry.isDirectory())
				continue;
			String name = entry.getName();
			String nameToLower = name.toLowerCase();
			if (nameToLower.endsWith(".xlsx") || nameToLower.endsWith(".csv")) {
				dir.add(name);
			}
		}
		return dir;
	}

	public static String openXls(String fileName) throws SheetxException {
		try {
			String inKey = fileName + "-" + System.currentTimeMillis();
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] b = md.digest(inKey.getBytes());
			String key = HexFilter.toHexFromBytes(b).toString();
			if (pool == null) {
				pool = new HashMap<String, Xls>();
			}

			if (pool.containsKey(key)) {
				return key;
			}

			pool.put(key, new Xls(Global.dataFolder + Global.fileSeparator + fileName));
			return key;
			// return pool.get(key);
		} catch (Exception e) {
			throw new SheetxException(e);
		}
	}
	
//	public static void main(String[] args) {
//		String in = "Pippo.xlsx";
//		String in2 = "2Pippo.xlsx";
//		MessageDigest md;
//		try {
//			md = MessageDigest.getInstance("MD5");
//			// md.update(in.getBytes());
//			byte[] b = md.digest(in.getBytes());
//			StringBuilder sb = HexFilter.toHexFromBytes(b);
//			String d = sb.toString();
//			System.out.println(d);
//			b = md.digest(in2.getBytes());
//			d = b.toString();
//			System.out.println(d);
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}

	public static Xls getXls(String key) throws SheetxException {
		try {
			if (pool == null || !pool.containsKey(key)) {
				throw new Exception("Invalid key");
			}

			return pool.get(key);
			
		} catch (Exception e) {
			throw new SheetxException(e);
		}
	}

	public static void releasXls(String key) {
		if (pool.containsKey(key)) {
			pool.remove(key);
		}
	}
}
