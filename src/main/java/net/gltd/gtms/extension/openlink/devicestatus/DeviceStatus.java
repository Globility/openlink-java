package net.gltd.gtms.extension.openlink.devicestatus;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "devicestatus")
public class DeviceStatus {

	@XmlElement
	private String profile;

	@XmlElementWrapper(name = "features")
	@XmlElement(name = "feature")
	private Set<DeviceStatusFeature> features = new HashSet<DeviceStatusFeature>();

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public Set<DeviceStatusFeature> getFeatures() {
		return features;
	}

	public void setFeatures(Set<DeviceStatusFeature> features) {
		this.features = features;
	}

	public enum DeviceStatusAction {
		Playback, Record, VoiceBlast, VoiceDrop;
	}

	public enum DeviceStatusStatus {
		ok, error, warn, unknown;
	}

}
