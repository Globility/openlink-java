package net.gltd.gtms.extension.gtx.privatedata;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.gltd.gtms.client.openlink.OpenlinkClientUtil;

@XmlRootElement(name = "gtx-profile", namespace = "http://gltd.net/protocol/gtx/profile")
@XmlType(propOrder = { "uid", "properties", "profiles" })
@XmlAccessorType(XmlAccessType.FIELD)
public class GtxProfile {

	@XmlAttribute(required = true)
	private String uid;

	@XmlElementWrapper(name = "properties", namespace = "http://gltd.net/protocol/gtx/properties")
	@XmlElement(name = "property", namespace = "http://gltd.net/protocol/gtx/properties")
	private Set<Property> properties;

	@XmlElementWrapper(name = "profiles", namespace = "http://gltd.net/protocol/gtx/profile")
	@XmlElement(name = "profile", namespace = "http://gltd.net/protocol/gtx/profile")
	private Set<Profile> profiles;

	public Set<Property> getProperties() {
		return this.properties;
	}

	public void setProperties(Set<Property> properties) {
		this.properties = properties;
	}

	public Set<Profile> getProfiles() {
		return this.profiles;
	}

	public void setProfiles(Set<Profile> profiles) {
		this.profiles = profiles;
	}

	public String getUid() {
		return this.uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public void updateProperty(String id, String value) {
		OpenlinkClientUtil.validateNonNullObject(id);
		OpenlinkClientUtil.validateNonNullObject(value);

		if (this.properties == null) {
			this.properties = new HashSet<Property>();
		}

		Property p = new Property();
		p.setId(id);
		p.setValue(value);

		this.properties.remove(p);
		this.properties.add(p);
	}

	public void updateProperty(Property property) {
		OpenlinkClientUtil.validateNonNullObject(property);
		OpenlinkClientUtil.validateNonNullObject(property.getId());
		OpenlinkClientUtil.validateNonNullObject(property.getValue());

		if (this.properties == null) {
			this.properties = new HashSet<Property>();
		}

		this.properties.remove(property);
		this.properties.add(property);
	}

	public void updateProfile(Profile profile) {
		OpenlinkClientUtil.validateNonNullObject(profile);

		if (this.profiles == null) {
			this.profiles = new HashSet<Profile>();
		}

		this.profiles.remove(profile);
		this.profiles.add(profile);
	}

	public void removeProfile(Profile profile) {
		OpenlinkClientUtil.validateNonNullObject(profile);

		if (this.profiles == null) {
			this.profiles = new HashSet<Profile>();
		}

		this.profiles.remove(profile);
	}

	public Profile getProfile(String id) {
		if ((this.profiles == null) || (id == null)) {
			return null;
		}

		for (Profile profile : this.profiles) {
			if (id.equals(profile.getId())) {
				return profile;
			}
		}

		return null;
	}

}
