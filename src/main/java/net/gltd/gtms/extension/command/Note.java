package net.gltd.gtms.extension.command;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlValue;

/**
 * @author Christian Schudt
 */
public class Note {

    @XmlValue
    private String value;

    @XmlAttribute(name = "type")
    private NoteType type;

    public enum NoteType {
        @XmlEnumValue(value = "error")
        ERROR,
        @XmlEnumValue(value = "info")
        INFO,
        @XmlEnumValue(value = "warn")
        WARN
    }

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public NoteType getType() {
		return type;
	}

	public void setType(NoteType type) {
		this.type = type;
	}
    
    
    
}
