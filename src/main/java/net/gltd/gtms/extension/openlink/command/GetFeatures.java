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
public class GetFeatures extends Command {

	@XmlTransient
	private IoData iodata;

	@XmlTransient
	private GetFeaturesIn in;

	public GetFeatures() {
		setNode(OpenlinkNamespaces.NS_OPENLINK_GETFEATURES);
		setAction(CommandAction.EXECUTE);

		this.in = new GetFeatures.GetFeaturesIn();

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

	public GetFeaturesIn getIn() {
		return in;
	}

	public void setIn(GetFeatures.GetFeaturesIn in) {
		this.in = in;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "in", namespace = "")
	public static class GetFeaturesIn {

		@XmlElement
		private String profile;

		public String getProfile() {
			return profile;
		}

		public void setProfile(String profile) {
			this.profile = profile;
		}

	}

}
