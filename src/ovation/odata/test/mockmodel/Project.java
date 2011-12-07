package ovation.odata.test.mockmodel;

public class Project extends EntityBase {
	private String _name;
	private AnalysisRecord[] _analysisRecords = new AnalysisRecord[] {new AnalysisRecord(1), new AnalysisRecord(2)};

	public Project(String name) { _name = name; }
	public String getName() { return _name; }
	
	public AnalysisRecord[] getAnalysisRecords() { return _analysisRecords; }
	public String toString() { return super.toString() + ", name:" + _name; }
}
