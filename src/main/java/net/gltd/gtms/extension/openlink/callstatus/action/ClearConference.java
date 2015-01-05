package net.gltd.gtms.extension.openlink.callstatus.action;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ClearConference")
public class ClearConference extends CallAction {
	@Override
	public String getId() {
		return "ClearConference";
	}
}
