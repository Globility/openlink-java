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

import org.xmpp.Jid;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "command", namespace = "http://jabber.org/protocol/commands")
public class GetProfiles extends Command {

	@XmlTransient
	private IoData iodata;

	@XmlTransient
	private GetProfilesIn in;

	public GetProfiles() {
		setNode(OpenlinkNamespaces.NS_OPENLINK_GETPROFILES);
		setAction(CommandAction.EXECUTE);

		this.in = new GetProfiles.GetProfilesIn();

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

	public GetProfilesIn getIn() {
		return in;
	}

	public void setIn(GetProfiles.GetProfilesIn in) {
		this.in = in;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "in", namespace = "")
	public static class GetProfilesIn {

		@XmlTransient
		private Jid jid;

		@XmlElement(name = "jid")
		public String getJidAsString() {
			return getJid().asBareJid().toString();
		}

		public Jid getJid() {
			return jid;
		}

		public void setJid(Jid jid) {
			this.jid = jid;
		}

	}

}
