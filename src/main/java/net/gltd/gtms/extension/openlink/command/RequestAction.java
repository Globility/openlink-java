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
import net.gltd.gtms.extension.openlink.callstatus.Call.CallAction;

@XmlRootElement(name = "command")
public class RequestAction extends Command {

	@XmlTransient
	private IoData iodata;

	@XmlTransient
	private RequestActionIn in;

	public RequestAction() {
		setNode(OpenlinkNamespaces.NS_OPENLINK_REQUESTACTION);
		setAction(CommandAction.EXECUTE);

		this.in = new RequestAction.RequestActionIn();

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

	public RequestActionIn getIn() {
		return in;
	}

	public void setIn(RequestAction.RequestActionIn in) {
		this.in = in;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "in")
	@XmlType(name = "", propOrder = { "interest", "action", "call", "value1", "value2" })
	public static class RequestActionIn {

		@XmlElement
		private String interest;

		@XmlElement
		private CallAction action;

		@XmlElement
		private String call;

		@XmlElement
		private String value1;

		@XmlElement
		private String value2;

		public String getInterest() {
			return interest;
		}

		public void setInterest(String interest) {
			this.interest = interest;
		}

		public CallAction getAction() {
			return action;
		}

		public void setAction(CallAction action) {
			this.action = action;
		}

		public String getCall() {
			return call;
		}

		public void setCall(String call) {
			this.call = call;
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

	}

}
