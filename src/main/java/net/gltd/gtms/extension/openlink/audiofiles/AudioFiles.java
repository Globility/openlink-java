package net.gltd.gtms.extension.openlink.audiofiles;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "audiofiles")
public class AudioFiles extends HashSet<AudioFile> {

	private static final long serialVersionUID = 880738737585651184L;

	@XmlElement(name = "audiofile")
	public Set<AudioFile> getAudioFiles() {
		return this;
	}

}
