package net.gltd.gtms.extension.openlink.callstatus;

import javax.xml.bind.annotation.XmlElement;

public class CallerCallee {

	@XmlElement
	private String number;

	@XmlElement
	private String name;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
