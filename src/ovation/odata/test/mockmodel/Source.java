package ovation.odata.test.mockmodel;

public class Source extends EntityBase {
	private KeywordTag[] _tags = new KeywordTag[] {
			new KeywordTag("src-key1", "src-val1"),
			new KeywordTag("src-key2", "src-val2"),
			new KeywordTag("src-key3", "src-val3"),
	};
	public Source() { }
	public KeywordTag[] getKeywordTags() { return _tags; }
	public String toString() { return super.toString() + ", tags:" + _tags; }
}
