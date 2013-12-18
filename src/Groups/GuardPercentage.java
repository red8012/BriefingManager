package Groups;
import BriefingManager.C;
import BriefingManager.M;

public class GuardPercentage extends GroupModule {
	public GuardPercentage(String type) {
		name = type;
	}

	@Override
	Integer calculate(int row) throws NullPointerException {
		if (name.equals(C.guard0)) {
			Double open = M.get(code, row, C.normalizedOpen),
					close = M.get(code, row, C.normalizedClose);
			if (close - open > 0) return 1;
		} else if (name.equals(C.guard1)) {
			Double open = M.get(code, row, C.normalizedOpen),
					close = M.get(code, row, C.normalizedClose);
			if (close - open > open * 0.01) return 1;
		} else if (name.equals(C.guard2)) {
			Double open = M.get(code, row, C.normalizedOpen),
					high = M.get(code, row, C.normalizedHigh);
			if (high - open > open * 0.02) return 1;
		} else if (name.equals(C.guard3)) {
			Double open = M.get(code, row, C.normalizedOpen),
					close = M.get(code, row + 2, C.normalizedClose);
			if (close - open > open * 0.03) return 1;
		} else if (name.equals(C.guard4)) {
			Double open0 = M.get(code, row, C.normalizedOpen),
					open1 = M.get(code, row + 1, C.normalizedOpen);
			if (open1 - open0 > 0) return 1;
		} else if (name.equals(C.guard5)) {
			Double open = M.get(code, row, C.normalizedOpen);
			if (M.get(code, row, C.normalizedHigh) - open > open * 0.05) return 1;
			if (M.get(code, row + 1, C.normalizedHigh) - open > open * 0.05) return 1;
			if (M.get(code, row + 2, C.normalizedHigh) - open > open * 0.05) return 1;
			if (M.get(code, row + 3, C.normalizedHigh) - open > open * 0.05) return 1;
			if (M.get(code, row + 4, C.normalizedHigh) - open > open * 0.05) return 1;
		} else System.err.println("Unknown type!");

		return -1;
	}
}
