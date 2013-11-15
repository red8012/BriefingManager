package Features.FeaturePrerequisites;
import FeatureGenerator.TechnicalModule;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class Std extends TechnicalModule {
	int period;

	public Std(int period) {
		super("std"+String.valueOf(period));
		this.period = period;
	}

	@Override
	public Double calculate(int row) {
		double[] dou = new double[period * 4];
		int x=0;
		try {
			for (int i = row - period + 1; i <= row; i++){
				dou[x++]=D.getDoubleChecked("open", i);
				dou[x++]=D.getDoubleChecked("high", i);
				dou[x++]=D.getDoubleChecked("low", i);
				dou[x++]=D.getDoubleChecked("close", i);
			}
		} catch (NullPointerException e) {
			return null;
		}
		StandardDeviation std=new StandardDeviation(false);
		return new Double(std.evaluate(dou));
	}
}