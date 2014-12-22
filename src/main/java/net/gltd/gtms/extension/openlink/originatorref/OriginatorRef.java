package net.gltd.gtms.extension.openlink.originatorref;

import java.util.HashSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "originator-ref")
public class OriginatorRef extends HashSet<Property> {

	private static final long serialVersionUID = 7421670246282445626L;

	@XmlElement(name="property")
	public HashSet<Property> getProperties() {
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
