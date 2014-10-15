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
public class GetInterests extends Command {

	@XmlTransient
	private IoData iodata;

	@XmlTransient
	private GetInterestsIn in;

	public GetInterests() {
		setNode(OpenlinkNamespaces.NS_OPENLINK_GETINTERESTS);
		setAction(CommandAction.EXECUTE);

		this.in = new GetInterests.GetInterestsIn();

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

	public GetInterestsIn getIn() {
		return in;
	}

	public void setIn(GetInterests.GetInterestsIn in) {
		this.in = in;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "in")
	public static class GetInterestsIn {

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
