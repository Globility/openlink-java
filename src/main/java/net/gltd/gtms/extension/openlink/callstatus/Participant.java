package net.gltd.gtms.extension.openlink.callstatus;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.gltd.gtms.extension.openlink.callstatus.Call.CallDirection;
import rocks.xmpp.core.Jid;

@XmlRootElement(name="participant")
public class Participant {

	@XmlAttribute
	private Jid jid;

	@XmlAttribute
	private String exten;

	@XmlAttribute
	private String timestamp;

	@XmlAttribute
	private CallDirection direction;

	@XmlAttribute
	private ParticipantType type;

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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
	}

}
