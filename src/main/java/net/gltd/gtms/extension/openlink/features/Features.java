package net.gltd.gtms.extension.openlink.features;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "features")
public class Features extends HashSet<Feature> {

	private static final long serialVersionUID = 2572429224700373304L;

	@XmlElement(name = "feature")
	public Set<Feature> getFeatures() {
		return this;
	}

	public Feature getFeatureById(String id) {
		for (Feature f : this) {
			if (id.equals(f.getId())) {
				return f;
			}
		}
		return null;
	}

}
