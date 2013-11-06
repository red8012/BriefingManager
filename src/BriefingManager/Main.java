package BriefingManager;
import Grab.*;
import PriceNormalizer.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
	public static void main(String[] args) throws Exception {
		M.connect(true);
		if (args[0].equals("rebuild")) rebuild("2013 07 01 2013 07 30".split(" "));
//		else if (args[0].equals("update")) update();
//		else System.out.println("hello world");
//		System.out.println(M.get("0000", "2013-11-01", "return"));



//		new PriceNormalizer("2498").run();
//		M.print("0000", "2013-07-22");
		M.print("0000", "2013-07-23");
//		M.print("0000", "2013-07-24");
		M.disconnect();
	}

	static void rebuild(String[] dates) throws Exception {
		System.out.print("Dropping table... ");
		M.collection.drop();
		M.collection = M.db.getCollection("tw");

		System.out.print("finished!\nDownloading data... ");
		Grab.determineGrabRange(dates);
		Grab.startWorking(false);
//		M.checkRowDateConsistency();

		M.collection.ensureIndex("securityCode");
		M.collection.ensureIndex("row");
		M.collection.ensureIndex("date");
		System.out.print("Running price normalizer... ");
		ExecutorService pool = Executors.newFixedThreadPool(4);
		for (String s : M.listSecurities())
			if (Integer.parseInt(s) > 1000) pool.execute(new PriceNormalizer(s));
		pool.execute(new IndexNormalizer());
		pool.shutdown();
		if (pool.awaitTermination(10, TimeUnit.MINUTES)) {
			System.out.println("finished!");
		} else System.out.println("Error: Timeout! (cannot connect to server)");

		System.out.print("finished");
	}

	static void update() throws Exception {
		Grab.determineUpdateRange();
		Grab.startWorking(true);
		M.checkRowDateConsistency();
	}
}
