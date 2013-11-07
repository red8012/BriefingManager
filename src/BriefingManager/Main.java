package BriefingManager;
import Grab.Grab;
import PriceNormalizer.PriceNormalizer;
import Groups.*;

public class Main {
	public static void main(String[] args) throws Exception {
		M.connect(true);
		if (args[0].equals("rebuild")) rebuild("2013 10 01 2013 10 30".split(" "));
		else if (args[0].equals("update")) update();
		else if (args[0].equals("calc")) calculate();
//		System.out.println(M.get("0000", "2013-11-01", "return"));

		M.print("2330", "2013-10-01");
		M.print("2330", "2013-10-02");
		M.print("2330", "2013-10-03");
		M.print("2330", "2013-10-04");
		M.print("2330", "2013-10-27");
		M.print("2330", "2013-10-30");
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

	static void calculate() throws Exception{
//		new GuardPercentage(C.guard1).run();
//		new GuardPercentage(C.guard2).run();
//		new GuardPercentage(C.guard3).run();
//		new RegressionPercentage(C.regressionClose).run();
//		new RegressionPercentage(C.regressionHigh).run();
	}
}
