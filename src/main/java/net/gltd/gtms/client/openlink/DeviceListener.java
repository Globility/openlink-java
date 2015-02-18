package net.gltd.gtms.client.openlink;

import java.util.Collection;

import net.gltd.gtms.extension.openlink.devicestatus.DeviceStatusFeature;

/**
 * Device Status listener which listens for Openlink Device Status events.
 *
 */
public interface DeviceListener {

	/**
	 * called with a list of Device Status features on a new Device Status Event.
	 * 
	 * @param profile
	 *            device status profile.
	 * @param features
	 *            device status features.
	 */
	public void deviceEvent(String profile, Collection<DeviceStatusFeature> features);

}
