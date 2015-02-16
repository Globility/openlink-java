package net.gltd.gtms.extension.openlink.features.voicemessage;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import net.gltd.gtms.openlinkcommon.constants.DeviceStatusAction;
import net.gltd.gtms.openlinkcommon.constants.DeviceStatusStatus;
import net.gltd.gtms.openlinkcommon.constants.VmsState;

@XmlRootElement(name = "voicemessage")
public class VoiceMessage {

	@XmlElement
	private String playlist;

	@XmlElement
	private DeviceStatusStatus status;

	@XmlElement
	private VmsState state;

	@XmlElement
	private String sequence;

	@XmlElement
	private String label;

	@XmlElement(name = "statusdescriptor")
	private String statusDescriptor;

	@XmlElement
	private DeviceStatusAction action;

	@XmlElement(name = "msglen")
	private String msgLength;

	@XmlElement
	private String exten;

	@XmlElement
	private String destination;

	@XmlElementWrapper(name = "playlists")
	@XmlElement(name = "playlist")
	private Set<String> playlists = new HashSet<String>();

	@XmlElement(name = "creationdate")
	private String creationDate;

	@XmlElement(name = "playprogress")
	private String playProgress;

	@XmlElement
	private String blastid;

	@XmlElement(name = "blastitemid")
	private String blastItemId;

	@XmlElement
	private String timestamp;

	//Properties
	
	public String getPlaylist() {
		return playlist;
	}

	public void setPlaylist(String playlist) {
		this.playlist = playlist;
	}

	public DeviceStatusStatus getStatus() {
		return status;
	}

	public void setStatus(DeviceStatusStatus status) {
		this.status = status;
	}

	public VmsState getState() {
		return state;
	}

	public void setState(VmsState state) {
		this.state = state;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getStatusDescriptor() {
		return statusDescriptor;
	}

	public void setStatusDescriptor(String statusDescriptor) {
		this.statusDescriptor = statusDescriptor;
	}

	public DeviceStatusAction getAction() {
		return action;
	}

	public void setAction(DeviceStatusAction action) {
		this.action = action;
	}

	public String getMsgLength() {
		return msgLength;
	}

	public void setMsgLength(String msgLength) {
		this.msgLength = msgLength;
	}

	public String getExten() {
		return exten;
	}

	public void setExten(String exten) {
		this.exten = exten;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Set<String> getPlaylists() {
		return playlists;
	}

	public void setPlaylists(Set<String> playlists) {
		this.playlists = playlists;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getPlayProgress() {
		return playProgress;
	}

	public void setPlayProgress(String playProgress) {
		this.playProgress = playProgress;
	}

	public String getBlastid() {
		return blastid;
	}

	public void setBlastid(String blastid) {
		this.blastid = blastid;
	}

	public String getBlastItemId() {
		return blastItemId;
	}

	public void setBlastItemId(String blastItemId) {
		this.blastItemId = blastItemId;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}
