package net.gltd.gtms.extension.gtx.privatedata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "property", namespace = "http://gltd.net/protocol/gtx/properties")
@XmlAccessorType(XmlAccessType.FIELD)
public class Property {

	@XmlAttribute(required = true)
	private String id;

	@XmlAttribute
	private Boolean required = false;

	@XmlElement
	private String value;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Boolean getRequired() {
		return this.required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

}
