package net.gltd.gtms.extension.iodata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "iodata", namespace = "urn:xmpp:tmp:io-data")
@XmlAccessorType(XmlAccessType.FIELD)
public class IoData {

	@XmlAttribute(name = "type", required = true)
	private IoDataType type;

	@XmlAnyElement(lax = true)
	private Object in;

	@XmlElement
	private Out out;

	public static class Out {

		@XmlAnyElement(lax = true)
		private Object extension;

		public Object getExtension() {
			return extension;
		}

		public void setExtension(Object extension) {
			this.extension = extension;
		}

		@SuppressWarnings("unchecked")
		public <T> T getExtension(Class<T> type) {
			if (extension != null && extension.getClass() == type) {
				return (T) extension;
			}
			return null;
		}

	}

	public IoData() {
	}

	public IoDataType getType() {
		return type;
	}

	public void setType(IoDataType type) {
		this.type = type;
		if (this.type == IoDataType.OUTPUT) {
			this.out = new IoData.Out();
		} else if (this.type == IoDataType.INPUT) {
			// this.in = new IoData.In();
		}
	}

	public Out getOut() {
		return out;
	}

	public void setOut(Out out) {
		this.out = out;
	}

	@SuppressWarnings("unchecked")
	public <T> T getIn(Class<T> type) {
		if (in != null && in.getClass() == type) {
			return (T) in;
		}
		return null;
	}

	public void setIn(Object in) {
		this.in = in;
	}

	@XmlEnum
	public enum IoDataType {
		@XmlEnumValue("io-schemata-get")
		IO_SCHEMATA_GET, @XmlEnumValue("io-schemata-result")
		IO_SCHEMATA_RESULT, @XmlEnumValue("input")
		INPUT, @XmlEnumValue("output")
		OUTPUT, @XmlEnumValue("getStatus")
		GET_STATUS, @XmlEnumValue("getOutput")
		GET_OUTPUT, @XmlEnumValue("error")
		ERROR, @XmlEnumValue("status")
		STATUS
	}

}
