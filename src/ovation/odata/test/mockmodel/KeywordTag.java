package ovation.odata.test.mockmodel;

public class KeywordTag extends EntityBase {
	private final String _key;
	private final String _value;
	public KeywordTag(String key, String value) { _key = key; _value = value; }
	public String getKey() { return _key; }
	public String getValue() { return _value; }
	public String toString() { return super.toString() + ", key:" + _key + ", value:" + _value; }
}
