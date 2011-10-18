package tools.xml.example.serialize;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;


@Root
public class Inventory2 {
	
	private List<AdvancedLure> lures;
	
	private String warehouse;
	
	public Inventory2(@ElementList(name="lures") List<AdvancedLure> lures, @Attribute(name="warehouse") String warehouse) {
		this.lures = lures;
		this.warehouse = warehouse;
	}

	@ElementList(name="lures")
	public List<AdvancedLure> getLures() {
		return lures;
	}

	@Attribute(name="warehouse")
	public String getWarehouse() {
		return warehouse;
	}
	
	

}
