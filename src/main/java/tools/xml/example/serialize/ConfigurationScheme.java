package tools.xml.example.serialize;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root
public class ConfigurationScheme {
	
	@Element
	private String color;
	
	@Element
	private int size;

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	

}
