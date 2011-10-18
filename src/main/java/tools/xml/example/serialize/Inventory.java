package tools.xml.example.serialize;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;


@Root
public class Inventory {
	
	@ElementList
	private List<AdvancedLure> lures;
	
	@Attribute
	private String warehouse;

	public List<AdvancedLure> getLures() {
		return lures;
	}

	public void setLures(List<AdvancedLure> lures) {
		this.lures = lures;
	}

	public String getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}
	
	

}
