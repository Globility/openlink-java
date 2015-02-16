package net.gltd.gtms.extension.openlink.features.callback;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;

import net.gltd.gtms.openlinkcommon.constants.CallbackState;

public class Callback {

	@XmlElement
	private boolean allocation;

	@XmlElement
	private String destination;

	@XmlElement
	private Active active;

	public class Active {

		@XmlValue
		private boolean value;

		@XmlAttribute
		private CallbackState state;

		public boolean isValue() {
			return value;
		}

		public void setValue(boolean value) {
			this.value = value;
		}

		public CallbackState getState() {
			return state;
		}

		public void setState(CallbackState state) {
			this.state = state;
		}

	}

}
