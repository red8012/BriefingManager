package BriefingManager;
import FeatureGenerator.CalculateWorker;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class GenerateCSVTrain implements Runnable {
	BufferedWriter writer;
	StringBuffer buffer = new StringBuffer();
	final boolean DEBUG = true;
	@Override
	public void run() {
		System.out.println("\nGenerating CSV.");
		try {
			Utility.cleanUpDir("train");
			List<String> securityList = M.listSecurities();
			if (DEBUG) {
				securityList = new LinkedList<String>();
				securityList.add("2330");
				securityList.add("2498");
				securityList.add("9945");
				securityList.add("1101");
			}
			int rowCount = M.getRowCount("2330");
			String[] col = new String[CalculateWorker.modules.length];
			for (int j = 0; j < CalculateWorker.modules.length; j++)
				col[j] = CalculateWorker.modules[j].NAME;

			writer = new BufferedWriter(new FileWriter("train/train.csv"));
			writer.write("date,code,guard0,guard1,guard2,guard3,guard4,guard5,open,high,low,close,vol");
			for (String s : col) writer.write("," + s);
			writer.newLine();

			for (int row = 0; row < rowCount; row++) {
				String date = M.getDate("2330", row);
				for (String code : securityList) {
					if (code.equals("0000")) continue;
					buffer= new StringBuffer();
					buffer.append(date + "," + code + ",");
					if (!writeInt(code, date, C.guard0, C.guard1, C.guard2, C.guard3, C.guard4, C.guard5)) continue;
					if (!write(code, date, C.open, C.high, C.low, C.close, C.volume)) continue;
					if (!write(code, date, col)) continue;

					writer.write(buffer.toString());
					writer.newLine();
				}
				System.out.print("*");
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("CSV generated");
	}

	boolean writeInt(String code, String date, String... column) throws Exception {
		Integer row = M.getRow(code, date);
		for (String c : column) {
			try {
				this.buffer.append(M.getInt(code, row, c).toString());
			} catch (Exception e) {
				return false;
			}
			this.buffer.append(",");
		}
		return true;
	}

	boolean write(String code, String date, String... column) throws IOException {
		for (String c : column) {
			try {
				this.buffer.append(M.get(code, date, c).toString());
			} catch (Exception e) {
				return false;
			}
			this.buffer.append(",");
		}
		return true;
	}
}
