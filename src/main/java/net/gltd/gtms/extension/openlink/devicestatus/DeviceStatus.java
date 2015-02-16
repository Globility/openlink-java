package net.gltd.gtms.extension.openlink.devicestatus;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.gltd.gtms.extension.openlink.features.Features;

@XmlRootElement(name = "devicestatus")
public class DeviceStatus {

	@XmlElement
	private String profile;

	@XmlElement
	private Features features;

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public Features getFeatures() {
		return features;
	}

	public void setFeatures(Features features) {
		this.features = features;
	}

}
