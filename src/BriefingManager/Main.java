package BriefingManager;
import FeatureGenerator.CalculateWorker;
import Grab.Grab;
import Groups.GuardPercentage;
import Groups.RegressionPercentage;
import PriceNormalizer.PriceNormalizer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
	public static boolean DEBUG = false;

	public static void main(String[] args) throws Exception {
		M.connect(true, true);

//		String[] col = new String[CalculateWorker.modules.length];
//		for (int j = 0; j < CalculateWorker.modules.length; j++)
//			col[j] = CalculateWorker.modules[j].NAME;
//		for (String s: col)
//			System.out.println(s+": "+ M.get("2330","2012-04-17",s));
		if (args[0].equals("rebuild")) rebuild("2011 10 01 2013 11 30".split(" "));
		else if (args[0].equals("update")) update();
		else if (args[0].equals("calc")) calculate();
		else if (args[0].equals("csv")) new GenerateCSVTrain().run();
		else if (args[0].equals("svm")) new GenerateSVMTrain().run();

//		new GenerateCSVTrain("0000").run();
//		new GenerateCSVTrain("1101").run();
//		new GenerateCSVTrain("2330").run();

		M.disconnect();
	}

	static void rebuild(String[] dates) throws Exception {
		System.out.print("Dropping table... ");
		M.collection.drop();
		M.collection = M.db.getCollection("tw");

		System.out.print("finished!\nDownloading data... ");
		Grab.determineGrabRange(dates);
		Grab.startWorking(false);

		M.collection.ensureIndex("securityCode");
		M.collection.ensureIndex("row");
		M.collection.ensureIndex("date");

		PriceNormalizer.start(false);

		System.out.print("All finished!");
	}

	static void update() throws Exception {
		Grab.setUpdateRangeToday();
		Grab.startWorking(true);
//		M.checkRowDateConsistency();
		Grab.setUpdateRangeToday();
		PriceNormalizer.start(true);
	}

	static void calculate() throws Exception {
		ExecutorService pool = Executors.newFixedThreadPool(4);
		System.out.println("Pool opened.");

		pool.execute(new GuardPercentage(C.guard0));
		pool.execute(new GuardPercentage(C.guard1));
		pool.execute(new GuardPercentage(C.guard2));
		pool.execute(new GuardPercentage(C.guard3));
		pool.execute(new GuardPercentage(C.guard4));
		pool.execute(new GuardPercentage(C.guard5));
		pool.execute(new RegressionPercentage(C.regressionClose));
		pool.execute(new RegressionPercentage(C.regressionHigh));

		for (String code : M.listSecurities()) {
			if (code.equals("0000")) continue;
			pool.execute(new CalculateWorker(code));
		}
		pool.shutdown();
		if (pool.awaitTermination(100, TimeUnit.MINUTES)) {
			System.out.println("\nPool closed.");
		}
	}
}
