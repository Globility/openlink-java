package net.gltd.gtms.extension.openlink.devicestatus;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * ARGH - and yet another implementation of feature.
 * 
 * @author leon
 *
 */
public class DeviceStatusFeature {

	@XmlAttribute
	private String id;

	@XmlAnyElement(lax = true)
	private Object value;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
	}
	
}
