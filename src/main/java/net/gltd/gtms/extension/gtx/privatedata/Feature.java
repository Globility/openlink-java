package net.gltd.gtms.extension.gtx.privatedata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "feature", namespace = "http://gltd.net/protocol/gtx/features")
@XmlType(propOrder = { "id"/* , "feature" */})
@XmlAccessorType(XmlAccessType.FIELD)
public class Feature {

	@XmlAttribute(required = true)
	String id;

	// @XmlElement
	// Feature feature;

	@XmlValue
	String value;

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
	
}
