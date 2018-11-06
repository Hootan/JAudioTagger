package org.jaudiotagger.audio.generic;

import org.extra.RandomAccessFileProvider;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.tag.Tag;

import java.io.IOException;

/**
 * Created by Paul on 15/09/2015.
 */
public interface TagWriter
{
    public void delete(Tag tag, RandomAccessFileProvider raf, RandomAccessFileProvider tempRaf) throws IOException, CannotWriteException;


    /**
     * Write tag to file
     *
     * @param tag
     * @param raf
     * @param rafTemp
     * @throws CannotWriteException
     * @throws IOException
     */
    public void write(AudioFile af, Tag tag, RandomAccessFileProvider raf, RandomAccessFileProvider rafTemp) throws CannotWriteException, IOException;
}
