package Groups;
import BriefingManager.M;

public class GroupModule implements Runnable {
	String code;
	String name;

	@Override
	public void run() {
		try {
			for (String code : M.listSecurities()) {
				int rowCount = M.getRowCount(code);
				this.code = code;
				for (int i = 0; i < rowCount; i++) {
					Integer result = tryCalculate(i);
					if (result != null)
						M.set(code, i, name, result);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Integer tryCalculate(int row) {
		try {
			return calculate(row);
		} catch (NullPointerException e) {
			return null;
		}
	}

	Integer calculate(int row) {
		return null;
	}
}
