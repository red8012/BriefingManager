package BriefingManager;
import java.io.File;

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
}
