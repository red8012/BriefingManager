package BriefingManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class GenerateSVMTrain implements Runnable {
	final static String[] features = {"RO0", "RO1", "RO2", "RH0", "RH1", "RH2", "RL0", "RL1", "RL2", "RC0", "RC1", "RC2"};
	final static String[] groups = {C.guard0, C.guard1, C.guard2, C.guard3, C.guard4, C.guard5};
	Counter[] counter = new Counter[6];
	BufferedWriter[] writers = new BufferedWriter[6];

	@Override
	public void run() {
		System.out.println("\nGenerating training data.");
		try {
			BufferedReader reader = new BufferedReader(new FileReader("train/train.csv"));
			String line = reader.readLine();
			for (int i = 0; i < groups.length; i++)
				writers[i] = new BufferedWriter(new FileWriter("train/" + groups[i]));
			for (int i = 0; i < 6; i++) counter[i] = new Counter();

			String[] split = line.split(",");
			ArrayList<Integer> columnList = new ArrayList<Integer>();
			for (int i = 0; i < split.length; i++)
				for (String f : features) {
					if (split[i].equals(f)) {
						columnList.add(i);
						break;
					}
				}

			while ((line = reader.readLine()) != null) {
				split = line.split(",");
				StringBuffer buffer = new StringBuffer();
				for (int i = 0; i < columnList.size(); i++)
					buffer.append(i + 1).append(":").append(split[columnList.get(i)]).append("\t");
				String svmFeature = buffer.toString();
				for (int i = 0; i < writers.length; i++) {
					counter[i].count(Integer.parseInt(split[i + 2]));
					writers[i].write(split[i + 2]);
					writers[i].write("\t");
					writers[i].write(svmFeature);
					writers[i].newLine();
				}
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
