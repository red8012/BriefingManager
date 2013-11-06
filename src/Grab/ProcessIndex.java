package Grab;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class ProcessIndex implements Runnable {
	final static String TABLE_STRING = "<table width=590 border=0 align=center cellpadding=0 cellspacing=1 class=board_trad>";

	@Override
	public void run() {
		try {
			File dir = new File("temp/index");
			File[] files = dir.listFiles();
			Arrays.sort(files);
			ArrayList<String> list = new ArrayList<String>();

			for (File f : files) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(f), "big5"));
				String line;
				while ((line = reader.readLine()) != null)
					if (line.startsWith(TABLE_STRING)) break;
				line = line.substring(line.indexOf("<tr height=20 bgcolor=#FFFFFF class=gray12>"));
				String[] split = line.split("</tr>");
				for (String s : split)
					if (s.length() > 10) {
						s = s.replaceAll("<[^>]*>", "").trim()
								.replaceAll(",", "").replaceAll(" ", ",").replaceAll("/", "-");
						String yearMingGuo = s.split("-")[0],
								yearXiYuan = String.valueOf(Integer.parseInt(yearMingGuo) + 1911);
						s = yearXiYuan + s.substring(yearMingGuo.length());
						String result = Grab.returnIndexMap.get(s.split(",")[0]);
						if (result!=null)
							list.add(s + "," + result);
					}
				reader.close();
			}
			Grab.map.put("0000", list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

