package net.gltd.gtms.extension.openlink.command;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.gltd.gtms.client.openlink.OpenlinkNamespaces;
import net.gltd.gtms.extension.command.Command;
import net.gltd.gtms.extension.iodata.IoData;
import net.gltd.gtms.extension.iodata.IoData.IoDataType;

@XmlRootElement(name = "command")
public class SetFeatures extends Command {

	@XmlTransient
	private IoData iodata;

	@XmlTransient
	private SetFeaturesIn in;

	public SetFeatures() {
		setNode(OpenlinkNamespaces.NS_OPENLINK_SETFEATURES);
		setAction(CommandAction.EXECUTE);

		this.in = new SetFeatures.SetFeaturesIn();

		this.iodata = new IoData();
		iodata.setType(IoDataType.INPUT);
		iodata.setIn(in);
		getExtensions().add(iodata);
	}

	public IoData getIodata() {
		return iodata;
	}

	public void setIodata(IoData iodata) {
		this.iodata = iodata;
	}

	public SetFeaturesIn getIn() {
		return in;
	}

	public void setIn(SetFeatures.SetFeaturesIn in) {
		this.in = in;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "in", namespace = "")
	public static class SetFeaturesIn {

		@XmlElement
		private String profile;

		@XmlElement
		private String feature;

		@XmlElement
		private String value1;

		@XmlElement
		private String value2;

		@XmlElement
		private String value3;

		public String getProfile() {
			return profile;
		}

		public void setProfile(String profile) {
			this.profile = profile;
		}

		public String getFeature() {
			return feature;
		}

		public void setFeature(String feature) {
			this.feature = feature;
		}

		public String getValue1() {
			return value1;
		}

		public void setValue1(String value1) {
			this.value1 = value1;
		}

		public String getValue2() {
			return value2;
		}

		public void setValue2(String value2) {
			this.value2 = value2;
		}

		public String getValue3() {
			return value3;
		}

		public void setValue3(String value3) {
			this.value3 = value3;
		}

	}

}
