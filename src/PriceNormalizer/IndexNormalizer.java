package PriceNormalizer;
import BriefingManager.C;
import BriefingManager.M;

public class IndexNormalizer implements Runnable{

	@Override
	public void run(){
		try {
			int rowCount = M.getRowCount("0000");
			for (int i = 0; i<rowCount; i++){
				double delta = M.get("0000", i, C.ret) - M.get("0000", i, C.close);
				M.set("0000", i, C.normalizedOpen, M.get("0000", i, C.open) + delta);
				M.set("0000", i, C.normalizedHigh, M.get("0000", i, C.high) + delta);
				M.set("0000", i, C.normalizedLow, M.get("0000", i, C.low) + delta);
				M.set("0000", i, C.normalizedClose, M.get("0000", i, C.close) + delta);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
