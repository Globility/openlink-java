package net.gltd.gtms.extension.openlink.callstatus.action;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "RetrieveCall")
public class RetrieveCall extends CallAction {
	@Override
	public String getId() {
		return "RetrieveCall";
	}
}
