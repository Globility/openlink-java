package net.gltd.gtms.extension.openlink.profiles;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "profile")
@XmlType(propOrder = { "id", "device", "defaultProfile", "online", "label", "actions" })
public class Profile {

	@XmlAttribute(required = true)
	private String id;

	@XmlAttribute
	private String device;

	@XmlAttribute(name = "default")
	private boolean defaultProfile;

	@XmlAttribute
	private boolean online;

	@XmlAttribute
	private String label;

	@XmlElementWrapper(name = "actions")
	@XmlElement(name = "action")
	private Set<Action> actions = new HashSet<Action>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isDefaultProfile() {
		return defaultProfile;
	}

	public void setDefaultProfile(boolean defaultProfile) {
		this.defaultProfile = defaultProfile;
	}

	public Set<Action> getActions() {
		return actions;
	}

	public void setActions(Set<Action> actions) {
		this.actions = actions;
	}

}
