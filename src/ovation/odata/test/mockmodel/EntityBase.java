package ovation.odata.test.mockmodel;

import java.util.UUID;

public abstract class EntityBase {
	private final String _uuid;
	protected EntityBase() 	 { _uuid = UUID.randomUUID().toString(); }
	public String getUuid()  { return _uuid; }
	public String toString() { return super.toString() + ", uuid:" + _uuid; }
}

