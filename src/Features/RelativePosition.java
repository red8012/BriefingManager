package Features;
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
			Double end = null,
					mean = D.getDoubleChecked("maA5", row),
					std = D.getDoubleChecked("std5", row);

			if (type.equals("RO0")) end = D.getDoubleChecked("open", row);
			else if (type.equals("RO1")) end = D.getDoubleChecked("open", row-1);
			else if (type.equals("RO2")) end = D.getDoubleChecked("open", row-2);

			else if (type.equals("RH0")) end = D.getDoubleChecked("high", row);
			else if (type.equals("RH1")) end = D.getDoubleChecked("high", row-1);
			else if (type.equals("RH2")) end = D.getDoubleChecked("high", row-2);

			else if (type.equals("RL0")) end = D.getDoubleChecked("low", row);
			else if (type.equals("RL1")) end = D.getDoubleChecked("low", row-1);
			else if (type.equals("RL2")) end = D.getDoubleChecked("low", row-2);

			else if (type.equals("RC0")) end = D.getDoubleChecked("close", row);
			else if (type.equals("RC1")) end = D.getDoubleChecked("close", row-1);
			else if (type.equals("RC2")) end = D.getDoubleChecked("close", row-2);

			return new Double((end - mean ) / (2*std));

		} catch (NullPointerException e) {
			return null;
		}
	}
}