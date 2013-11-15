package FeatureGenerator;
import BriefingManager.C;
import BriefingManager.M;
import Features.FeaturePrerequisites.MovingAverage;

import java.util.ArrayList;

public class CalculateWorker implements Runnable {
	DataSet d = new DataSet();
	String code;
	public final static TechnicalModule[] modules = {
			new MovingAverage(MovingAverage.ALL, 3),
			new MovingAverage(MovingAverage.ALL, 5)
	};

	public CalculateWorker(String code) {
		this.code = code;
	}

	@Override
	public void run() {
		readData();
		addModule();
		writeData();
	}

	void readData() {
		d.addNewColumn(C.date, new ArrayList<String>());
		d.addNewColumn(C.open, new ArrayList<Double>());
		d.addNewColumn(C.high, new ArrayList<Double>());
		d.addNewColumn(C.low, new ArrayList<Double>());
		d.addNewColumn(C.close, new ArrayList<Double>());
		int rowCount = M.getRowCount(code);
		for (int i = 0; i < rowCount; i++) {
			try {
				d.add(C.date, M.getDate(code, i));
				d.add(C.open, M.get(code, i, C.normalizedOpen));
				d.add(C.high, M.get(code, i, C.normalizedHigh));
				d.add(C.low, M.get(code, i, C.normalizedLow));
				d.add(C.close, M.get(code, i, C.normalizedClose));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (TechnicalModule m : modules)
			m.D = d;
	}

	void addModule() {
		for (TechnicalModule m : modules) {
			d.addNewColumn(m.NAME, new ArrayList<Double>(d.getLength()));
			for (int i = 0; i < d.getLength(); i++) {
				try {
					d.add(m.NAME, m.calculate(i));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (d.getLength() != d.get(m.NAME).size())
				System.err.println("length not match: " + code + "@" + m.NAME);
		}
	}

	void writeData() {
		for (TechnicalModule m : modules) {
			for (int i = 0; i < d.getLength(); i++) {
				try {
					M.set(code, d.getStr(C.date, i), m.NAME, d.getDoubleChecked(m.NAME, i));
				} catch (NullPointerException e) {
//					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
