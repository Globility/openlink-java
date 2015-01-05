package net.gltd.gtms.extension.openlink.callstatus.action;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ConsultationCall")
public class ConsultationCall extends CallAction {
	@Override
	public String getId() {
		return "ConsultationCall";
	}
}
