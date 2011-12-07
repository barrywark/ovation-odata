package ovation.odata.test.mockmodel;

public class AnalysisRecord extends EntityBase {
	private int _idx;
	public AnalysisRecord(int idx) { _idx = idx; }
	public String toString() { return super.toString() + ", idx:" + _idx; }
}
