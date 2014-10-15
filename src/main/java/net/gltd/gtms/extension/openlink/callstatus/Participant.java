package net.gltd.gtms.extension.openlink.callstatus;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.gltd.gtms.extension.openlink.callstatus.Call.CallDirection;

import org.xmpp.Jid;

@XmlType(propOrder = { "type", "direction", "exten" })
public class Participant {

	@XmlTransient
	private Jid jid;

	@XmlAttribute
	private String exten;

	@XmlAttribute
	private String timestamp;

	@XmlAttribute
	private CallDirection direction;

	@XmlAttribute
	private ParticipantType type;

	@XmlAttribute(name = "jid")
	public String getJidAsString() {
		String result = null;
		if (getJid() != null) {
			result = getJid().asBareJid().toString();
		}
		return result;
	}

	public Jid getJid() {
		return jid;
	}

	public void setJid(Jid jid) {
		this.jid = jid;
	}

	public CallDirection getDirection() {
		return direction;
	}

	public void setDirection(CallDirection direction) {
		this.direction = direction;
	}

	public ParticipantType getType() {
		return type;
	}

	public void setType(ParticipantType type) {
		this.type = type;
	}

	public String getExten() {
		return exten;
	}

	public void setExten(String exten) {
		this.exten = exten;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public enum ParticipantType {
		Active, Inactive
	}

}
