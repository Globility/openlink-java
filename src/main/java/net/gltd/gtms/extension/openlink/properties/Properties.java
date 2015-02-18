package net.gltd.gtms.extension.openlink.properties;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "properties")
public class Properties extends HashSet<Property> {

	private static final long serialVersionUID = 1242010432115892931L;

	@XmlElement(name = "property")
	public Set<Property> getProperties() {
		return this;
	}

	public Property getPropertyById(String id) {
		for (Property p : this) {
			if (id.equals(p.getId())) {
				return p;
			}
		}
		return null;
	}

}
