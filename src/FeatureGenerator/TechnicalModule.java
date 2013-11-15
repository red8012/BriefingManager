package FeatureGenerator;
public class TechnicalModule {
	public final String NAME;
	public DataSet D;

	public TechnicalModule(String name) {
		NAME = name;
	}

	public Double calculate(int row) throws Exception {
		if (D == null) throw new Exception("DataSet D is null");
		return null;
	}
}