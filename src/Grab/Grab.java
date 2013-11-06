package Grab;
import BriefingManager.C;
import BriefingManager.M;
import BriefingManager.Utility;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Grab {
	static int YYYY, MM, DD, yyyy, mm, dd;
	static Calendar start, current, end;
	static HashMap<String, ArrayList<String>> map;
	static HashMap<String, String> returnIndexMap, volumeMap, upMap, downMap;

	public static void startWorking(boolean isUpdateMode) {
		System.out.print((isUpdateMode ? "You are using update mode. \tThe data will be downloaded" :
				"The data will be grabbed ") +
				"from [ " + start.getTime().toString().replace("00:00:00 CST ", "") +
				" ] to [ " + end.getTime().toString().replace("00:00:00 CST ", "") + " ]\n" +
				"Downloading data, please wait... ");
		try {
			grab();
			System.out.print("finished!\nArranging data, please wait... ");
			arrange();
			System.out.print("finished!\nProcessing index, please wait... ");
			new ProcessIndex().run();
			System.out.print("finished!\nSaving arranged data to database, please wait... ");
			writeArranged();
			System.out.println("Finished!");
			Utility.cleanUpDir("temp/index");
			Utility.cleanUpDir("temp");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void determineGrabRange(String args[]) throws NumberFormatException {
		YYYY = Integer.parseInt(args[0]);
		MM = Integer.parseInt(args[1]);
		DD = Integer.parseInt(args[2]);
		yyyy = Integer.parseInt(args[3]);
		mm = Integer.parseInt(args[4]);
		dd = Integer.parseInt(args[5]);
		start = new GregorianCalendar(YYYY, MM - 1, DD, 0, 0, 0);
		current = new GregorianCalendar(YYYY, MM - 1, DD, 0, 0, 0);
		end = new GregorianCalendar(yyyy, mm - 1, dd, 0, 0, 0);
	}

	static void grab() throws Exception {
		Utility.cleanUpDir("temp");
		Utility.cleanUpDir("temp/index");
		ExecutorService pool = Executors.newFixedThreadPool(4);
		int lastMonth = -100;

		while (current.compareTo(end) <= 0) {
			int month = current.get(Calendar.MONTH) + 1;
			pool.execute(new DownloadWorker(current.get(Calendar.YEAR), month, current.get(Calendar.DAY_OF_MONTH)));
			if (month != lastMonth) {
				lastMonth = month;
				pool.execute(new DownloadIndex(current.get(Calendar.YEAR), month));
			}
			current.add(Calendar.DAY_OF_MONTH, 1);
		}
		pool.shutdown();
		if (pool.awaitTermination(10, TimeUnit.MINUTES)) {
//			System.out.println("\nFile downloading finished.");
		} else System.out.println("Error: Timeout! (cannot connect to server)");
	}

	static void arrange() throws Exception {
		map = new HashMap<String, ArrayList<String>>();
		returnIndexMap = new HashMap<String, String>();
		volumeMap = new HashMap<String, String>();
		upMap = new HashMap<String, String>();
		downMap = new HashMap<String, String>();
		String line;
		current = new GregorianCalendar(YYYY, MM - 1, DD, 0, 0, 0);

		while (current.compareTo(end) <= 0) {
			Integer yy = current.get(Calendar.YEAR),
					mm = current.get(Calendar.MONTH) + 1,
					dd = current.get(Calendar.DAY_OF_MONTH);
			current.add(Calendar.DAY_OF_MONTH, 1);
			String year = yy.toString(),
					month = (mm < 10 ? "0" : "") + mm.toString(),
					date = (dd < 10 ? "0" : "") + dd.toString();
			File f = new File("temp/" + year + month + date + ".csv");
			if (!f.exists()) continue;
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "big5"));
			while ((line = reader.readLine()) != null) {
				String[] split = line.split(",");
				try {
					if (split[0].equals("發行量加權股價報酬指數")) {
						split = Utility.removeUrusaiTokens(line).split(",");
						returnIndexMap.put(year + "-" + month + "-" + date, split[1]);
						continue;
					} else if (split[0].equals("1.一般股票")) {
						split = Utility.removeUrusaiTokens(line).split(",");
						volumeMap.put(year + "-" + month + "-" + date, split[1]);
						continue;
					} else if (split[0].equals("上漲(漲停)")) {
						split = Utility.removeUrusaiTokens(line).split(",");
						upMap.put(year + "-" + month + "-" + date, split[1].split("\\(")[0]);
						continue;
					} else if (split[0].equals("下跌(跌停)")) {
						split = Utility.removeUrusaiTokens(line).split(",");
						downMap.put(year + "-" + month + "-" + date, split[1].split("\\(")[0]);
						continue;
					}
					if (split[0].length() != 4) continue;
					split = Utility.removeUrusaiTokens(line).split(",");
					Integer.parseInt(split[0]); // check legal 4 digit code
				} catch (NumberFormatException e) {
					continue;
				}
				if (map.get(split[0]) == null) map.put(split[0], new ArrayList<String>());
				ArrayList<String> list = map.get(split[0]);
				StringBuffer arranged = new StringBuffer(year);
				arranged.append("-").append(month).append("-").append(date).append(",")
						.append(split[4]).append(",").append(split[5]).append(",").append(split[6])
						.append(",").append(split[7]).append(",").append(split[8]);
				list.add(arranged.toString());
			}
			reader.close();
			System.out.print("*");
		}
	}

	static void writeArranged() throws Exception {
		for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
			int row = M.getRowCount(entry.getKey());
			for (String s : entry.getValue()) {
				String[] split = s.split(",");
				if (entry.getKey().equals("0000")) {
				 M.insert("0000", row, split[0]);
					M.set("0000", row, C.open, stringToDouble(split[1]));
					M.set("0000", row, C.high, stringToDouble(split[2]));
					M.set("0000", row, C.low, stringToDouble(split[3]));
					M.set("0000", row, C.close, stringToDouble(split[4]));
					M.set("0000", row, C.ret, stringToDouble(split[5]));
				} else {
					M.insert(entry.getKey(), row, split[0],
							stringToDouble(split[1]),
							stringToDouble(split[2]),
							stringToDouble(split[3]),
							stringToDouble(split[4]),
							stringToDouble(split[5]));
				}
				row++;
			}
			if (entry.getKey().equals("0000")) {
				for (Map.Entry<String, String> e : volumeMap.entrySet())
					M.set("0000", e.getKey(), C.volume, stringToDouble(e.getValue()));
				for (Map.Entry<String, String> e : upMap.entrySet())
					M.set("0000", e.getKey(), C.up, stringToDouble(e.getValue()));
				for (Map.Entry<String, String> e : downMap.entrySet())
					M.set("0000", e.getKey(), C.down, stringToDouble(e.getValue()));
			}
			System.out.print("*");
		}
	}

	public static void determineUpdateRange() throws Exception {
		String line, prevLine = "";
		File f = new File("arranged/2330.csv");
		if (!f.exists()) {
			System.out.println("You have not run batched grab yet, please run in batched grab mode first.");
			return;
		}
		BufferedReader reader = new BufferedReader(new FileReader(f));
		while ((line = reader.readLine()) != null) prevLine = line;
		reader.close();
		String date[] = prevLine.split(",")[0].split("-");
		YYYY = Integer.parseInt(date[0]);
		MM = Integer.parseInt(date[1]);
		DD = Integer.parseInt(date[2]) + 1;
		Calendar calendar = Calendar.getInstance();
		yyyy = calendar.get(Calendar.YEAR);
		mm = calendar.get(Calendar.MONTH) + 1;
		dd = calendar.get(Calendar.DAY_OF_MONTH);
		start = new GregorianCalendar(YYYY, MM - 1, DD, 0, 0, 0);
		current = new GregorianCalendar(YYYY, MM - 1, DD, 0, 0, 0);
		end = new GregorianCalendar(yyyy, mm - 1, dd, 0, 0, 0);
	}

	static Double stringToDouble(String s) throws Exception {
		try {
			return new Double(s);
		} catch (Exception e) {
			if (s.equals("--")) return null;
			else throw new Exception(s);
		}
	}
}