package net.gltd.gtms.extension.openlink.callstatus.action;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SingleStepTransfer")
public class SingleStepTransfer extends CallAction {
	@Override
	public String getId() {
		return "SingleStepTransfer";
	}
}
