package net.gltd.gtms.extension.openlink.callstatus.action;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "TransferCall")
public class TransferCall extends CallAction {
	@Override
	public String getId() {
		return "TransferCall";
	}
}
