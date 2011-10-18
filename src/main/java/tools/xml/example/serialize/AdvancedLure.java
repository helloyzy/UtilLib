package tools.xml.example.serialize;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;



@Root
public class AdvancedLure {

	@Attribute
	private String type;
	
	@Element
	private String company;
	
	@Element
	private int quantityInStock;
	
	@Element
	private String model;
	
	@Element
	private ConfigurationScheme configurationScheme;
	
	

	public ConfigurationScheme getConfigurationScheme() {
		return configurationScheme;
	}

	public void setConfigurationScheme(ConfigurationScheme configurationScheme) {
		this.configurationScheme = configurationScheme;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public int getQuantityInStock() {
		return quantityInStock;
	}

	public void setQuantityInStock(int quantityInStock) {
		this.quantityInStock = quantityInStock;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}
	
	
	
	
}
