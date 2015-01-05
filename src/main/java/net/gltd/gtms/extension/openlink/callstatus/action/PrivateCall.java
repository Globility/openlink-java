package net.gltd.gtms.extension.openlink.callstatus.action;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PrivateCall")
public class PrivateCall extends CallAction {
	@Override
	public String getId() {
		return "PrivateCall";
	}
}
