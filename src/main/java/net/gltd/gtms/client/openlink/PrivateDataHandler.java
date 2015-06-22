package net.gltd.gtms.client.openlink;

import net.gltd.gtms.extension.gtx.privatedata.GtxProfile;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.extensions.privatedata.PrivateDataManager;

public class PrivateDataHandler {

	public static final String PROFILE_PROPERTY_SITE_ID = "siteId";
	
	private final XmppSession xmppSession;

	private final PrivateDataManager privateDataManager;

	public PrivateDataHandler(XmppSession xmppSession) {
		this.xmppSession = xmppSession;
		this.privateDataManager = this.xmppSession.getExtensionManager(PrivateDataManager.class);
	}

	public GtxProfile getGtxProfile() throws XmppException {
		return this.privateDataManager.getData(GtxProfile.class);
	}

}
