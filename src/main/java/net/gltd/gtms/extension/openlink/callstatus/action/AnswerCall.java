package net.gltd.gtms.extension.openlink.callstatus.action;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AnswerCall")
public class AnswerCall extends CallAction {
	@Override
	public String getId() {
		return "AnswerCall";
	}

}
