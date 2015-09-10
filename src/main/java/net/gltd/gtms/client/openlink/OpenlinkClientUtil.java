package net.gltd.gtms.client.openlink;

import net.gltd.gtms.extension.command.Command;
import net.gltd.gtms.extension.command.Note;

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

	public static String getNoteError(Command command) {
		String result = null;
		if (command != null) {
			if (command.getNote() != null && Note.NoteType.ERROR == command.getNote().getType()) {
				result = command.getNote().getValue();
			}
		}
		return result;
	}

}
