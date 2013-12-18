package Features;
import BriefingManager.C;
import FeatureGenerator.TechnicalModule;

public class RelativePosition extends TechnicalModule {
	final String type;

	public RelativePosition(String type) {
		super(type);
		this.type = type;
	}

	@Override
	public Double calculate(int row) {
		try {
			Double end,
					mean = D.getDoubleChecked("MAA5", row),
					std = D.getDoubleChecked("std5", row);

			if (type.equals("RO0")) end = D.getDoubleChecked(C.open, row);
			else if (type.equals("RO1")) end = D.getDoubleChecked(C.open, row - 1);
			else if (type.equals("RO2")) end = D.getDoubleChecked(C.open, row - 2);

			else if (type.equals("RH0")) end = D.getDoubleChecked(C.high, row);
			else if (type.equals("RH1")) end = D.getDoubleChecked(C.high, row - 1);
			else if (type.equals("RH2")) end = D.getDoubleChecked(C.high, row - 2);

			else if (type.equals("RL0")) end = D.getDoubleChecked(C.low, row);
			else if (type.equals("RL1")) end = D.getDoubleChecked(C.low, row - 1);
			else if (type.equals("RL2")) end = D.getDoubleChecked(C.low, row - 2);

			else if (type.equals("RC0")) end = D.getDoubleChecked(C.close, row);
			else if (type.equals("RC1")) end = D.getDoubleChecked(C.close, row - 1);
			else if (type.equals("RC2")) end = D.getDoubleChecked(C.close, row - 2);
			else {
				System.err.println("error on type");
				return null;
			}
			return new Double((end - mean) / (2 * std));
		} catch (NullPointerException e) {
			return null;
		}
	}
}