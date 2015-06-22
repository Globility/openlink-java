package net.gltd.gtms.client.openlink;

public class OpenlinkClientUtil {

	private OpenlinkClientUtil() {
	};

	public static void validateNonNullObject(Object input) throws IllegalArgumentException {
		boolean valid = input != null;
		if (!valid) {
			throw new IllegalArgumentException("Input must be non-null.");
		}
	}

	public static void validateNonNullObject(Object input, String message) throws IllegalArgumentException {
		boolean valid = input != null;
		if (!valid) {
			throw new IllegalArgumentException(message);
		}
	}
	
}
