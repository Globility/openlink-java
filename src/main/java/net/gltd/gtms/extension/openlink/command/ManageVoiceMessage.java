package net.gltd.gtms.extension.openlink.command;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.gltd.gtms.client.openlink.OpenlinkNamespaces;
import net.gltd.gtms.extension.command.Command;
import net.gltd.gtms.extension.iodata.IoData;
import net.gltd.gtms.extension.iodata.IoData.IoDataType;
import net.gltd.gtms.openlinkcommon.constants.DeviceStatusAction;

@XmlRootElement(name = "command")
public class ManageVoiceMessage extends Command {

	@XmlTransient
	private IoData iodata;

	@XmlTransient
	private ManageVoiceMessageIn in;

	public ManageVoiceMessage() {
		setNode(OpenlinkNamespaces.NS_OPENLINK_MANAGEVOICEMESSAGE);
		setAction(CommandAction.EXECUTE);

		this.in = new ManageVoiceMessage.ManageVoiceMessageIn();

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

	public ManageVoiceMessageIn getIn() {
		return in;
	}

	public void setIn(ManageVoiceMessage.ManageVoiceMessageIn in) {
		this.in = in;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "in")
	@XmlType(name = "", propOrder = { "profile", "action", "label" })
	public static class ManageVoiceMessageIn {

		@XmlElement
		private String profile;

		@XmlElement
		private DeviceStatusAction action;

		@XmlElement
		private String label;

		public String getProfile() {
			return profile;
		}

		public void setProfile(String profile) {
			this.profile = profile;
		}

		public DeviceStatusAction getAction() {
			return action;
		}

		public void setAction(DeviceStatusAction action) {
			this.action = action;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

	}

}
