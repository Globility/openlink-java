package net.gltd.gtms.extension.openlink.audiofiles;

import java.util.Collection;
import java.util.HashSet;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import net.gltd.gtms.client.openlink.OpenlinkNamespaces;
import net.gltd.gtms.extension.openlink.properties.Property;

@XmlRootElement(name = "audiofile")
public class AudioFile {

	@XmlElement
	private String label;

	// @XmlElement
	// private String profile;

	@XmlElement
	private String lifetime;

	@XmlElement(name = "msglen")
	private String msgLength;

	@XmlElement
	private String size;

	@XmlElement(name = "creationdate")
	private String creationDate;

	@XmlElement
	private Location location;

	@XmlElementWrapper(name = "properties", namespace = OpenlinkNamespaces.NS_OPENLINK_PROPERTIES)
	@XmlElement(name = "property", namespace = OpenlinkNamespaces.NS_OPENLINK_PROPERTIES)
	private Collection<Property> properties = new HashSet<Property>();

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	// public String getProfile() {
	// return profile;
	// }
	//
	// public void setProfile(String profile) {
	// this.profile = profile;
	// }

	public String getLifetime() {
		return lifetime;
	}

	public void setLifetime(String lifetime) {
		this.lifetime = lifetime;
	}

	public String getMsgLength() {
		return msgLength;
	}

	public void setMsgLength(String msgLength) {
		this.msgLength = msgLength;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Collection<Property> getProperties() {
		return properties;
	}

	public void setProperties(Collection<Property> properties) {
		this.properties = properties;
	}

	public static class Location {

		@XmlAttribute
		private String url;

		@XmlElement
		private Auth auth;

		public Auth getAuth() {
			return auth;
		}

		public void setAuth(Auth auth) {
			this.auth = auth;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public static class Auth {

			@XmlAttribute
			private AuthType type;

			@XmlElement
			private String userid;

			@XmlElement
			private String password;

			public AuthType getType() {
				return type;
			}

			public void setType(AuthType type) {
				this.type = type;
			}

			public String getUserid() {
				return userid;
			}

			public void setUserid(String userid) {
				this.userid = userid;
			}

			public String getPassword() {
				return password;
			}

			public void setPassword(String password) {
				this.password = password;
			}

			public enum AuthType {
				none, required;
			}

		}

	}

}
