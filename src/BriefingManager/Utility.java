package BriefingManager;
import java.io.File;
import java.util.Calendar;

public class Utility {
	public static void cleanUpDir(String name) {
		File temp = new File(name);
		if (!temp.exists()) temp.mkdir();
		File[] files = temp.listFiles();
		if (files != null)
			for (File file : files) file.delete();
	}

	public static String removeUrusaiTokens(String s) {
		int start = s.indexOf("\""), end = s.indexOf("\"", start + 1);
		String left, middle, right;
		if (start > 0 && end > 0) {
			left = s.substring(0, start);
			middle = s.substring(start + 1, end).replaceAll(",", "");
			right = s.substring(end + 1);
			return removeUrusaiTokens(left + middle + right);
		}
		return s;
	}

	public static String calendarToString(Calendar c) {
		Integer yyyy = c.get(Calendar.YEAR), mm = c.get(Calendar.MONTH) + 1, dd = c.get(Calendar.DAY_OF_MONTH);
		String y = yyyy.toString();
		String m = (mm < 10 ? "0" : "") + mm.toString();
		String d = (dd < 10 ? "0" : "") + dd.toString();
		return y + "-" + m + "-" + d;
	}

	public static Double stringToDouble(String s) throws Exception {
		try {
			return new Double(s);
		} catch (Exception e) {
			if (s.equals("--")) return null;
			else throw new Exception(s);
		}
	}
}
