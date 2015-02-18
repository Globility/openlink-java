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

	public String getCallId() {
		return callId;
	}

	public void setCallId(String callId) {
		this.callId = callId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public DtmfDirection getDirection() {
		return direction;
	}

	public void setDirection(DtmfDirection direction) {
		this.direction = direction;
	}

	public Participant getParticipant() {
		return participant;
	}

	public void setParticipant(Participant participant) {
		this.participant = participant;
	}

	public enum DtmfDirection {
		sent, received
	}

}
