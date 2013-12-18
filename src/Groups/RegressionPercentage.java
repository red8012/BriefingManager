package Groups;
import BriefingManager.C;
import BriefingManager.M;

public class RegressionPercentage extends RegressionModule {
	public RegressionPercentage(String type) {
		name = type;
	}

	@Override
	Double calculate(int row) {
		Double open = M.get(code, row, C.normalizedOpen);

		if (name.equals(C.regressionClose))
			return (M.get(code, row, C.normalizedClose) - open) / open;

		else if (name.equals(C.regressionHigh))
			return (M.get(code, row, C.normalizedHigh) - open) / open;

		else if (name.equals(C.regressionOpen))
			return (M.get(code, row + 1, C.normalizedOpen) - open) / open;

		else System.err.println("Unknown type!");

		return null;
	}
}
