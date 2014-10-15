package net.gltd.gtms.extension.openlink.interests;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "interest")
@XmlType(propOrder = { "id", "type", "label" })
public class Interest {

	@XmlAttribute
	private String id;

	@XmlAttribute
	private String type;

	@XmlAttribute(name="default")
	private boolean defaultInterest;

	@XmlAttribute
	private String value;

	@XmlAttribute
	private String label;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isDefaultInterest() {
		return defaultInterest;
	}

	public void setDefaultInterest(boolean defaultInterest) {
		this.defaultInterest = defaultInterest;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
