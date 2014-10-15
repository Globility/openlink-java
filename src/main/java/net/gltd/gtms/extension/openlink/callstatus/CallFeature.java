package net.gltd.gtms.extension.openlink.callstatus;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * ARGH - yet another implementation of feature.
 * 
 * @author leon
 *
 */
public class CallFeature {

	@XmlAttribute
	private String id;

    @XmlValue
	private boolean value1;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isValue1() {
		return value1;
	}

	public void setValue1(boolean value) {
		this.value1 = value;
	}

}
