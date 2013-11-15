package Features.FeaturePrerequisites;
import BriefingManager.C;
import FeatureGenerator.TechnicalModule;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

public class MovingAverage extends TechnicalModule {
	public final static int HIGH = 0, LOW = 1, MID = 2, ALL = 3;
	final static String NAME_POSTFIX[] = {"H", "L", "M", "A"};
	final static int[] LENGTH = {1, 1, 2, 4};
	final int period, type;

	public MovingAverage(int type, int period) {
		super("MA" + NAME_POSTFIX[type] + String.valueOf(period));
		this.period = period;
		this.type = type;
	}
	@Override
	public Double calculate(int row) {
		double[] dou = new double[period * LENGTH[type]];
		int x = 0;
		try {
			for (int i = row - period + 1; i <= row; i++) {
				if (type == ALL) dou[x++] = D.getDoubleChecked(C.open, i);
				if (type != LOW) dou[x++] = D.getDoubleChecked(C.high, i);
				if (type != HIGH) dou[x++] =D.getDoubleChecked(C.low, i);
				if (type == ALL) dou[x++] = D.getDoubleChecked(C.close, i);
			}
		} catch (NullPointerException e) {
			return null;
		}
		Mean mean = new Mean();
		return new Double(mean.evaluate(dou));
	}
}
