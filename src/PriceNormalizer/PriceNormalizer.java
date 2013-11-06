package PriceNormalizer;
import BriefingManager.C;
import BriefingManager.M;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;

public class PriceNormalizer implements Runnable {
	final String code;
	final LinkedList<Pair<String, Double>> list = new LinkedList<Pair<String, Double>>();

	public PriceNormalizer(String code) {
		this.code = code;
	}

	@Override
	public void run() {
		int debug1 = 0, debug2 = 0;
		ArrayList<String[]> row = new ArrayList<String[]>();
		String line = "", output = "";

		try {
			String url = "http://www.money-link.com.tw//twstock/stockFA.aspx?sm=2&pagecode=fic04&symid=[id]"
					.replace("[id]", code);
			URLConnection conn = new URL(url).openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			boolean startRecording = false;

			int columnCounter = 0;
			row.add(new String[16]);
			while ((line = reader.readLine()) != null) {
				if (line.contains("yyy")) startRecording = true;
				if (startRecording) {
					if (line.matches("</tr>")) continue;
					if (line.matches("</table>")) break;
					if (line.matches("<tr>")) {
						row.add(new String[20]);
						columnCounter = 0;
						continue;
					}
					debug1 = row.size() - 1;
					debug2 = columnCounter;
					row.get(row.size() - 1)[columnCounter++] = line.replaceAll("<[^>]*>", "");
				}
			}
			row.remove(row.size() - 1);
			for (String[] sa : row) output += expand(sa);
			writeToList(output);
			writeResult();
			System.out.print("*");
		} catch (Exception e) {
			System.err.println("error on " + code);
			System.out.println(debug1);
			System.out.println(debug2);
			System.out.println(line);
			e.printStackTrace();
		}
	}

	String expand(String[] sa) {
		try {
			if (sa[6].equals("&nbsp;") && !sa[2].equals("&nbsp;"))
				return sa[2] + "\t" + String.valueOf(new Float(sa[5])) + "\n";
			if (sa[2].equals("&nbsp;") && !sa[6].equals("&nbsp;"))
				return sa[6] + "\t" + String.valueOf(new Float(sa[9])) + "\n";

			int compare = sa[2].compareTo(sa[6]);
			if (compare == 0 && !sa[2].equals("&nbsp;"))
				return sa[2] + "\t" + String.valueOf(new Float(sa[5]) + new Float(sa[9])) + "\n";
			if (compare > 0 && !sa[2].equals("&nbsp;") && !sa[6].equals("&nbsp;"))
				return sa[2] + "\t" + sa[5] + "\n" + sa[6] + "\t" + sa[9] + "\n";
			if (!sa[2].equals("&nbsp;") && !sa[6].equals("&nbsp;"))
				return sa[6] + "\t" + sa[9] + "\n" + sa[2] + "\t" + sa[5] + "\n";
		} catch (NumberFormatException e) {
			return "";
		}
		return "";
	}

	void writeToList(String lines) {
		for (String line : lines.split("\n")) {
			String[] split = line.split("\t");
			String[] date = split[0].split("/");
			try {
				split[0] = String.valueOf(new Integer(date[0]) + 1911) + "/" + date[1] + "/" + date[2];
			} catch (NumberFormatException e) {
				continue;
			}
			try {
				list.addFirst(new Pair<String, Double>(split[0], new Double(split[1])));
			} catch (NumberFormatException e) {
			}
		}
	}

	void writeResult() {
		try {
			// copy
			int rowCount = M.getRowCount(code);
			// process normalize
			for (Pair<String, Double> p : list) {
				String currentDate = p.getKey().replaceAll("/", "-");
				if (M.getRow(code, currentDate) == null) continue;
				for (int i = M.getRow(code, currentDate) + 1; i < rowCount; i++) {
					Double v = M.get(code, i, C.normalizedOpen);
					if (v != null) M.set(code, i, C.normalizedOpen, v + p.getValue());
					v = M.get(code, i, C.normalizedHigh);
					if (v != null) M.set(code, i, C.normalizedHigh, v + p.getValue());
					v = M.get(code, i, C.normalizedLow);
					if (v != null) M.set(code, i, C.normalizedLow, v + p.getValue());
					v = M.get(code, i, C.normalizedClose);
					if (v != null) M.set(code, i, C.normalizedClose, v + p.getValue());
				}
			}
		} catch (Exception e) {
			System.err.println("Error writing result: " + code);
			e.printStackTrace();
		}
	}
}
