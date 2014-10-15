package net.gltd.gtms.extension.command;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Christian Schudt
 */
@XmlRootElement(name = "command")
public class Command {

    @XmlAttribute
    private CommandAction action;

    @XmlAttribute
    private String node;

    @XmlAttribute(name = "sessionid")
    private String sessionId;

    @XmlAttribute
    private Status status;

    @XmlAnyElement(lax = true)
    private final List<Object> extensions = new ArrayList<Object>();

    private Note note;
    
    public enum CommandAction {
        @XmlEnumValue("cancel")
        CANCEL,
        @XmlEnumValue("complete")
        COMPLETE,
        @XmlEnumValue("execute")
        EXECUTE,
        @XmlEnumValue("next")
        NEXT,
        @XmlEnumValue("prev")
        PREV
    }


    public enum Status {

        @XmlEnumValue("canceled")
        CANCELED,
        @XmlEnumValue("completed")
        COMPLETED,
        @XmlEnumValue("executing")
        EXECUTING
    }


	public CommandAction getAction() {
		return action;
	}


	public void setAction(CommandAction action) {
		this.action = action;
	}


	public String getNode() {
		return node;
	}


	public void setNode(String node) {
		this.node = node;
	}


	public String getSessionId() {
		return sessionId;
	}


	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}


	public Status getStatus() {
		return status;
	}


	public void setStatus(Status status) {
		this.status = status;
	}
	
	 /**
     * Gets all extensions.
     *
     * @return The extensions.
     */
    public List<Object> getExtensions() {
        return extensions;
    }

    @SuppressWarnings("unchecked")
    public final <T> T getExtension(Class<T> type) {
        for (Object extension : extensions) {
            if (extension.getClass() == type) {
                return (T) extension;
            }
        }
        return null;
    }


	public Note getNote() {
		return note;
	}


	public void setNote(Note note) {
		this.note = note;
	}
    
}


