package net.gltd.gtms.extension.openlink.callstatus.action;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "IntercomTransfer")
public class IntercomTransfer extends CallAction {
	@Override
	public String getId() {
		return "IntercomTransfer";
	}
}
