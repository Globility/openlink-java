package net.gltd.gtms.extension.openlink.features.dtmf;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.gltd.gtms.extension.openlink.callstatus.Participant;

@XmlRootElement(name = "dtmf")
public class Dtmf {
	
	@XmlElement(name = "callid")
	private String callId;
	
	@XmlElement
	private String value;
	
	@XmlElement
	private DtmfDirection direction;
	
	@XmlElement
	Participant participant;
	
	public enum DtmfDirection {
		sent, received
	}

}
