package net.gltd.gtms.extension.command;

import net.gltd.gtms.extension.command.Note.NoteType;
import rocks.xmpp.core.XmppException;

/**
 * This is thrown when a {@link Command} contains a {@link Note} of {@link NoteType} type @link {@link NoteType#ERROR}.
 * 
 * @author leon
 *
 */
public final class CommandNoteTypeErrorException extends XmppException {

	public CommandNoteTypeErrorException(String message) {
		super(message);
	}
}
