package net.gltd.gtms.extension.openlink.profiles;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "profiles")
public class Profiles extends HashSet<Profile> {

	private static final long serialVersionUID = 5127730143855830165L;

	@XmlElement(name = "profile")
	public Set<Profile> getProfiles() {
		return this;
	}
	
	public Profile getProfileById(String id) {
		for (Profile p : this) {
			if (id.equals(p.getId())) {
				return p;
			}
		}
		return null;
	}

}
