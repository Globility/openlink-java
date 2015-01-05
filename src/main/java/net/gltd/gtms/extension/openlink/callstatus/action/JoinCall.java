package net.gltd.gtms.extension.openlink.callstatus.action;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "JoinCall")
public class JoinCall extends CallAction {
	@Override
	public String getId() {
		return "JoinCall";
	}
}
