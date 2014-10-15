package net.gltd.gtms.client.openlink;

import java.util.Collection;

import net.gltd.gtms.extension.openlink.callstatus.Call;

/**
 * Call listener which listens for Openlink call events.
 *
 */
public interface CallListener {

	/**
	 * called with a list of call objects on a call event change.
	 * 
	 * @param calls
	 *            list of call objects.
	 */
	public void callEvent(Collection<Call> calls);

}
