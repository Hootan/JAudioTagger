package org.jaudiotagger.audio.generic;

import org.extra.FileSystemProvider;
import org.extra.RandomAccessFileProvider;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.NoReadPermissionsException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.logging.ErrorMessage;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Replacement for AudioFileReader class
 */
public abstract class AudioFileReader2 extends AudioFileReader
{
    /*
   * Reads the given file, and return an AudioFile object containing the Tag
   * and the encoding infos present in the file. If the file has no tag, an
   * empty one is returned. If the encodinginfo is not valid , an exception is thrown.
   *
   * @param f The file to read
   * @exception NoReadPermissionsException if permissions prevent reading of file
   * @exception CannotReadException If anything went bad during the read of this file
   */
    public AudioFile read(FileSystemProvider provider) throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        if(logger.isLoggable(Level.CONFIG))
        {
            logger.config(ErrorMessage.GENERAL_READ.getMsg(provider));
        }

        if (!provider.canRead())
        {
            logger.warning(Permissions.displayPermissions(provider));
            throw new NoReadPermissionsException(ErrorMessage.GENERAL_READ_FAILED_DO_NOT_HAVE_PERMISSION_TO_READ_FILE.getMsg(provider));
        }

        if (provider.length() <= MINIMUM_SIZE_FOR_VALID_AUDIO_FILE)
        {
            throw new CannotReadException(ErrorMessage.GENERAL_READ_FAILED_FILE_TOO_SMALL.getMsg(provider));
        }

        GenericAudioHeader info = getEncodingInfo(provider);
        Tag tag = getTag(provider);
        return new AudioFile(provider, info, tag);
    }

    /**
     *
     * Read Encoding Information
     *
     * @param provider
     * @return
     * @throws CannotReadException
     * @throws IOException
     */
    protected abstract GenericAudioHeader getEncodingInfo(FileSystemProvider provider) throws CannotReadException, IOException;

    protected GenericAudioHeader getEncodingInfo(RandomAccessFileProvider raf) throws CannotReadException, IOException
    {
        throw new UnsupportedOperationException("Old method not used in version 2");
    }

    /**
     * Read tag Information
     *
     * @param provider
     * @return
     * @throws CannotReadException
     * @throws IOException
     */
    protected abstract Tag getTag(FileSystemProvider provider) throws CannotReadException, IOException;

    protected Tag getTag(RandomAccessFileProvider file) throws CannotReadException, IOException
    {
        throw new UnsupportedOperationException("Old method not used in version 2");
    }
}
