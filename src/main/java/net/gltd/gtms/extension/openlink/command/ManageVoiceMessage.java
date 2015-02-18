package net.gltd.gtms.extension.openlink.command;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.gltd.gtms.client.openlink.OpenlinkNamespaces;
import net.gltd.gtms.extension.command.Command;
import net.gltd.gtms.extension.iodata.IoData;
import net.gltd.gtms.extension.iodata.IoData.IoDataType;
import net.gltd.gtms.extension.openlink.audiofiles.AudioFile;

@XmlRootElement(name = "command")
public class ManageVoiceMessage extends Command {

	@XmlTransient
	private IoData iodata;

	@XmlTransient
	private ManageVoiceMessageIn in;

	public ManageVoiceMessage() {
		setNode(OpenlinkNamespaces.NS_OPENLINK_MANAGEVOICEMESSAGE);
		setAction(CommandAction.EXECUTE);

		this.in = new ManageVoiceMessage.ManageVoiceMessageIn();

		this.iodata = new IoData();
		iodata.setType(IoDataType.INPUT);
		iodata.setIn(in);
		getExtensions().add(iodata);
	}

	public IoData getIodata() {
		return iodata;
	}

	public void setIodata(IoData iodata) {
		this.iodata = iodata;
	}

	public ManageVoiceMessageIn getIn() {
		return in;
	}

	public void setIn(ManageVoiceMessage.ManageVoiceMessageIn in) {
		this.in = in;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "in")
	@XmlType(name = "")
	public static class ManageVoiceMessageIn {

		@XmlElement
		private String profile;

		@XmlElement
		private ManageVoiceMessageAction action;

		@XmlElement
		private String label;

		@XmlElementWrapper(name = "features")
		@XmlElement(name = "feature")
		private Set<ManageVoiceMessageFeature> features = new HashSet<ManageVoiceMessageFeature>();

		@XmlElementWrapper(name = "audiofiles", namespace = OpenlinkNamespaces.NS_OPENLINK_AUDIOFILES)
		@XmlElement(name = "audiofile", namespace = OpenlinkNamespaces.NS_OPENLINK_AUDIOFILES)
		private Set<AudioFile> audioFiles = new HashSet<AudioFile>();

		public Set<AudioFile> getAudioFiles() {
			return audioFiles;
		}

		public void setAudioFiles(Set<AudioFile> audioFiles) {
			this.audioFiles = audioFiles;
		}

		public String getProfile() {
			return profile;
		}

		public void setProfile(String profile) {
			this.profile = profile;
		}

		public ManageVoiceMessageAction getAction() {
			return action;
		}

		public void setAction(ManageVoiceMessageAction action) {
			this.action = action;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public Set<ManageVoiceMessageFeature> getFeatures() {
			return features;
		}

		public void setFeatures(Set<ManageVoiceMessageFeature> features) {
			this.features = features;
		}

	}

	public static class ManageVoiceMessageFeature {

		@XmlElement
		private String id;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

	}

	public enum ManageVoiceMessageAction {
		Create, Record, Edit, Playback, Save, Delete, Query, Search, Archive;
	}

}
