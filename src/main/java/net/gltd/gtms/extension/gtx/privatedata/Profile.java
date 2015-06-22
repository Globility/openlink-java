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

@XmlType(propOrder = { "id", "properties", "systems" })
@XmlAccessorType(XmlAccessType.FIELD)
public class Profile {

	@XmlAttribute(required = true)
	String id;

	@XmlElementWrapper(name = "properties", namespace = "http://gltd.net/protocol/gtx/properties")
	@XmlElement(name = "property", namespace = "http://gltd.net/protocol/gtx/properties")
	private Set<Property> properties;

	@XmlElementWrapper(name = "systems", namespace = "http://gltd.net/protocol/gtx/profile")
	@XmlElement(name = "system", namespace = "http://gltd.net/protocol/gtx/profile")
	private Set<GtxSystem> systems;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set<Property> getProperties() {
		return this.properties;
	}

	public void setProperties(Set<Property> properties) {
		this.properties = properties;
	}

	public Set<GtxSystem> getSystems() {
		return this.systems;
	}

	public void setSystems(Set<GtxSystem> systems) {
		this.systems = systems;
	}

	public void updateProperty(String id, String value) {
		OpenlinkClientUtil.validateNonNullObject(id, "Property ID invalid");
		OpenlinkClientUtil.validateNonNullObject(value, "Property value invalid");

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
		OpenlinkClientUtil.validateNonNullObject(property.getId(), "Property invalid");
		OpenlinkClientUtil.validateNonNullObject(property.getId(), "Property ID invalid");
		OpenlinkClientUtil.validateNonNullObject(property.getValue(), "Property value invalid");

		if (this.properties == null) {
			this.properties = new HashSet<Property>();
		}

		this.properties.remove(property);
		this.properties.add(property);
	}

	public GtxSystem addGtxSystem(String id, String type, String category) {
		OpenlinkClientUtil.validateNonNullObject(id, "System ID invalid");
		OpenlinkClientUtil.validateNonNullObject(type, "System type invalid");
		OpenlinkClientUtil.validateNonNullObject(category, "System category invalid");

		if (this.systems == null) {
			this.systems = new HashSet<GtxSystem>();
		}

		GtxSystem system = new GtxSystem();
		system.setId(id);
		system.setType(type);
		system.setCategory(category);

		this.systems.remove(system); // drop any existing with same ID
		this.systems.add(system);
		return system;
	}

	public GtxSystem addGtxSystem(GtxSystem system) {
		OpenlinkClientUtil.validateNonNullObject(system);
		OpenlinkClientUtil.validateNonNullObject(system.getId(), "System invalid");
		OpenlinkClientUtil.validateNonNullObject(system.getId(), "System ID invalid");
		OpenlinkClientUtil.validateNonNullObject(system.getType(), "System type invalid");
		OpenlinkClientUtil.validateNonNullObject(system.getCategory(), "System category invalid");

		if (this.systems == null) {
			this.systems = new HashSet<GtxSystem>();
		}

		this.systems.remove(system); // drop any existing with same ID
		this.systems.add(system);
		return system;
	}

	public GtxSystem getGtxSystem(String id) {
		if ((this.systems == null) || (id == null)) {
			return null;
		}

		for (GtxSystem gtxSystem : this.systems) {
			if (id.equals(gtxSystem.getId())) {
				return gtxSystem;
			}
		}

		return null;
	}

	public Set<GtxSystem> getGtxSystemsByType(String type) {
		if ((this.systems == null) || (type == null)) {
			return new HashSet<GtxSystem>();
		}

		Set<GtxSystem> typeSystems = new HashSet<GtxSystem>();
		for (GtxSystem gtxSystem : this.systems) {
			if (type.equals(gtxSystem.getType())) {
				typeSystems.add(gtxSystem);
			}
		}

		return typeSystems;
	}

	public Set<GtxSystem> getGtxSystemsByCategory(String category) {
		if ((this.systems == null) || (category == null)) {
			return new HashSet<GtxSystem>();
		}

		Set<GtxSystem> typeSystems = new HashSet<GtxSystem>();
		for (GtxSystem gtxSystem : this.systems) {
			if (category.equals(gtxSystem.getCategory())) {
				typeSystems.add(gtxSystem);
			}
		}

		return typeSystems;
	}

}
