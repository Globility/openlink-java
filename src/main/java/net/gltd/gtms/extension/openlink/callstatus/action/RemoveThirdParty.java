package net.gltd.gtms.extension.openlink.callstatus.action;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "RemoveThirdParty")
public class RemoveThirdParty extends CallAction {
	@Override
	public String getId() {
		return "RemoveThirdParty";
	}
}
