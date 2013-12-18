package BriefingManager;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class GenerateSVMTrain implements Runnable {
	final static String[] features = {"RO0", "RO1", "RO2", "RH0", "RH1", "RH2", "RL0", "RL1", "RL2", "RC0", "RC1", "RC2"};
	final static String[] groups = {
			C.guard0, C.guard1, C.guard2, C.guard3,
			C.guard4
			, C.guard5
//			,	C.regressionClose, C.regressionHigh
	};
	Counter[] counter = new Counter[6];
	BufferedWriter[] writers = new BufferedWriter[8];

	@Override
	public void run() {
		System.out.println("\nGenerating training data.");
		try {
			Utility.cleanUpDir("train");
			for (int i = 0; i < groups.length; i++) writers[i] = new BufferedWriter(new FileWriter("train/" + groups[i]));
			for (int i = 0; i < 6; i++) counter[i] = new Counter();

//			for (String code : M.listSecurities()) {
			int rowCount = M.getRowCount("2330");
			for (int row = 0; row < rowCount; row++) {
				String date = M.getDate("2330", row);
				for (String code : M.listSecurities()) {
					if (code.equals("0000")) continue;
//				final int rowCount = M.getRowCount(code);
//				for (int row = 0; row < rowCount; row++) {
					StringBuilder builder = new StringBuilder(
//							"\t" + code + "@" + M.getDate(code, row) +
							"\t");
					Double d = null;
					try {
						for (int i = 0; i < features.length; i++) {
							d = M.get(code, date, features[i]);
							if (d == null) break;
							builder.append("\t").append(i + 1).append(":").append(d);
						}
					} catch (Exception e) {
						continue;
					}
					if (d == null) continue;
					try {
						String featureString = builder.toString();
						for (int i = 0; i < 6; i++) {
							int answer = M.getInt(code, row + 1, groups[i]);
							counter[i].count(answer);
							writers[i].write(String.valueOf(answer));
							writers[i].write(featureString);
							writers[i].newLine();
						}
						for (int i = 6; i < 8; i++) {
							double answer = M.get(code, row + 1, groups[i]);
							writers[i].write(String.valueOf(answer));
							writers[i].write(featureString);
							writers[i].newLine();
						}
					} catch (Exception e) {
						continue;
					}
				}
				System.out.print("*");
			}
			for (BufferedWriter w : writers) w.close();
			System.out.println("\nTraining data generated.");
			report();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void report() {
		System.out.println("\nReport:");
		for (int i = 0; i < 6; i++)
			System.out.println(groups[i] + "\t" + counter[i].toString());
	}

	class Counter {
		int[] counts = new int[5];
		int el = 0;

		public Counter() {
			for (int i = 0; i < 5; i++) counts[i] = 0;
		}

		public void count(int i) {
			if (i <= 2 && i >= -2) counts[i + 2] += 1;
			else el += 1;
		}

		public String toString() {
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < 5; i++)
				if (counts[i] != 0)
					buffer.append(i - 2).append(": ").append(counts[i]).append("\t\t");
			if (el != 0) buffer.append("else").append(": ").append(el);
			buffer.append("positive: ").append(counts[3]).append("/").append(counts[1] + counts[3])
					.append(" = ").append((double) counts[3] / (counts[1] + counts[3]));
			return buffer.toString();
		}
	}
}
