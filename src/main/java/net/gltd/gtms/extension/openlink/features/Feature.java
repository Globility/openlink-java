package net.gltd.gtms.extension.openlink.features;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "feature")
@XmlType(propOrder = { "id", "type", "label" })
public class Feature {

	@XmlAttribute
	private String id;

	@XmlAttribute
	private String type;

	@XmlAttribute
	private String label;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * List of potential feature types.
	 * 
	 * @author leon
	 *
	 */
	public enum FeatureType {
		MessageWaiting, MicrophoneGain, MicrophoneMute, RingerStatus, SpeakerMute, SpeedDial, GroupIntercom, SpeakerChannel, VoiceMessage, VoiceMessagePlaylist, VoiceRecorder, VoiceBridge, Privacy, Handset, DoNotDisturb, CallForward, CallBack, Conference, MediaStream, DeviceKeys;
	}

}
