package FeatureGenerator;
import BriefingManager.C;
import BriefingManager.M;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class WriteCSV implements Runnable {
	final String code;
	BufferedWriter writer;

	public WriteCSV(String code) {
		this.code = code;
	}

	@Override
	public void run() {
		String[] col = new String[CalculateWorker.modules.length];
		for (int j = 0; j < CalculateWorker.modules.length; j++)
			col[j] = CalculateWorker.modules[j].NAME;
		try {
			int rowCount = M.getRowCount(code);
			writer = new BufferedWriter(new FileWriter(code + ".csv"));
			writer.write("date,open,high,low,close,vol,guard0,guard1,guard2,guard3,guard4,guard5," +
					"regressionClose,regressionHigh");
			for (String s : col)
				writer.write("," + s);
			writer.newLine();
			for (int i = 0; i < rowCount; i++) {
				writer.write(M.getDate(code, i).toString() + ",");
				write(code, i, C.open, C.high, C.low, C.close, C.volume);
				writer.write(",");
				writeInt(code, i, C.guard0, C.guard1, C.guard2, C.guard3, C.guard4, C.guard5);
				write(code, i, C.regressionClose, C.regressionHigh);
				writer.write(",");

				write(code, i, col);
				writer.newLine();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void writeInt(String code, int row, String... column) throws IOException {
		for (String c : column) {
			try {
				writer.write(M.getInt(code, row, c).toString());
			} catch (Exception e) {
			}
			writer.write(",");
		}
	}

	void write(String code, int row, String... column) throws IOException {
		for (int i = 0; i < column.length - 1; i++) {
			try {
				writer.write(M.get(code, row, column[i]).toString());
			} catch (Exception e) {
			}
			writer.write(",");
		}
		try {
			writer.write(M.get(code, row, column[column.length - 1]).toString());
		} catch (Exception e) {
		}
	}
}
