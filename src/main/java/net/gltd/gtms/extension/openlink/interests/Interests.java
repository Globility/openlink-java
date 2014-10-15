package net.gltd.gtms.extension.openlink.interests;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "interests")
public class Interests extends HashSet<Interest> {

	private static final long serialVersionUID = 3048987552131198292L;

	@XmlElement(name = "interest")
	public Set<Interest> getInterests() {
		return this;
	}

	public Interest getInterestById(String id) {
		for (Interest i : this) {
			if (id.equals(i.getId())) {
				return i;
			}
		}
		return null;
	}
	
}
