package net.gltd.gtms.extension.gtx.privatedata;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import net.gltd.gtms.client.openlink.OpenlinkClientUtil;

@XmlType(propOrder = { "id", "type", "category", "enabled", "properties", "features" })
@XmlAccessorType(XmlAccessType.FIELD)
public class GtxSystem {

	@XmlAttribute(required = true)
	private String id;

	@XmlAttribute(required = true)
	private String type;

	@XmlAttribute
	private String category;

	@XmlAttribute(required = true)
	private boolean enabled;

	@XmlElementWrapper(name = "properties", namespace = "http://gltd.net/protocol/gtx/properties")
	@XmlElement(name = "property", namespace = "http://gltd.net/protocol/gtx/properties")
	private Set<Property> properties;

	@XmlElementWrapper(name = "features", namespace = "http://gltd.net/protocol/gtx/features")
	@XmlElement(name = "feature", namespace = "http://gltd.net/protocol/gtx/features")
	private Set<Feature> features;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Set<Property> getProperties() {
		return this.properties;
	}

	public void setProperties(Set<Property> properties) {
		this.properties = properties;
	}

	public Set<Feature> getFeatures() {
		return this.features;
	}

	public void setFeatures(Set<Feature> features) {
		this.features = features;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public boolean getEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void updateProperty(String id, String value, boolean required) {
		OpenlinkClientUtil.validateNonNullObject(id);
		OpenlinkClientUtil.validateNonNullObject(value);
		OpenlinkClientUtil.validateNonNullObject(required);

		if (this.properties == null) {
			this.properties = new HashSet<Property>();
		}

		Property p = new Property();
		p.setId(id);
		p.setValue(value);
		p.setRequired(required);

		this.properties.remove(p);
		this.properties.add(p);
	}

	public void updateProperty(String id, String value) {
		this.updateProperty(id, value, false);
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

	public void updateFeature(String id, String value) {
		OpenlinkClientUtil.validateNonNullObject(id);
		OpenlinkClientUtil.validateNonNullObject(value);

		if (this.features == null) {
			this.features = new HashSet<Feature>();
		}

		Feature f = new Feature();
		f.setId(id);
		f.setValue(value);

		this.features.remove(f);
		this.features.add(f);
	}

	public void updateFeature(Feature feature) {
		OpenlinkClientUtil.validateNonNullObject(feature);
		OpenlinkClientUtil.validateNonNullObject(feature.getId());
		OpenlinkClientUtil.validateNonNullObject(feature.getValue());

		if (this.features == null) {
			this.features = new HashSet<Feature>();
		}

		this.features.remove(feature);
		this.features.add(feature);
	}

	public Property getProperty(String id) {
		if ((this.properties == null) || (id == null)) {
			return null;
		}

		for (Property p : this.properties) {
			if (id.equals(p.getId())) {
				return p;
			}
		}

		return null;
	}

}
