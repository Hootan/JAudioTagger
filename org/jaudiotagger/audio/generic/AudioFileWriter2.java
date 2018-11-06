package org.jaudiotagger.audio.generic;

import org.extra.FileSystemProvider;
import org.extra.RandomAccessFileProvider;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.logging.ErrorMessage;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagOptionSingleton;

import java.io.IOException;

/**
 * Created by Paul on 28/01/2016.
 */
public abstract class AudioFileWriter2 extends AudioFileWriter
{
    /**
     * Delete the tag (if any) present in the given file
     *
     * @param af The file to process
     *
     * @throws CannotWriteException if anything went wrong
     * @throws org.jaudiotagger.audio.exceptions.CannotReadException
     */
    @Override
    public void delete(AudioFile af) throws CannotReadException, CannotWriteException
    {
        FileSystemProvider provider = af.getProvider();

        if (TagOptionSingleton.getInstance().isCheckIsWritable() && !provider.canWrite())
        {
            logger.severe(Permissions.displayPermissions(provider));
            throw new CannotWriteException(ErrorMessage.GENERAL_DELETE_FAILED
                    .getMsg(provider));
        }

        if (af.getProvider().length() <= MINIMUM_FILESIZE)
        {
            throw new CannotWriteException(ErrorMessage.GENERAL_DELETE_FAILED_BECAUSE_FILE_IS_TOO_SMALL
                    .getMsg(provider));
        }
        deleteTag(af.getTag(), provider);
    }

    /**
     * Replace with new tag
     *
     * @param af The file we want to process
     * @throws CannotWriteException
     */
    @Override
    public void write(AudioFile af) throws CannotWriteException
    {
        FileSystemProvider provider = af.getProvider();

        if (TagOptionSingleton.getInstance().isCheckIsWritable() && !provider.canWrite())
        {
            logger.severe(Permissions.displayPermissions(provider));
            logger.severe(ErrorMessage.GENERAL_WRITE_FAILED.getMsg(af.getProvider()
                    .getPath()));
            throw new CannotWriteException(ErrorMessage.GENERAL_WRITE_FAILED_TO_OPEN_FILE_FOR_EDITING
                    .getMsg(provider));
        }

        if (af.getProvider().length() <= MINIMUM_FILESIZE)
        {
            throw new CannotWriteException(ErrorMessage.GENERAL_WRITE_FAILED_BECAUSE_FILE_IS_TOO_SMALL
                    .getMsg(provider));
        }
        writeTag(af.getTag(), provider);
    }

    /**
     * Must be implemented by each audio format
     *
     * @param tag
     * @param provider
     * @throws CannotReadException
     * @throws CannotWriteException
     */
    protected abstract void deleteTag(Tag tag, FileSystemProvider provider) throws CannotReadException, CannotWriteException;


    public void deleteTag(Tag tag, RandomAccessFileProvider raf, RandomAccessFileProvider tempRaf) throws CannotReadException, CannotWriteException, IOException
    {
        throw new UnsupportedOperationException("Old method not used in version 2");
    }

    /**
     * Must be implemented by each audio format
     *
     * @param tag
     * @param provider
     * @throws CannotWriteException
     */
    protected abstract void writeTag(Tag tag, FileSystemProvider provider) throws CannotWriteException;

    protected   void writeTag(AudioFile audioFile, Tag tag, RandomAccessFileProvider raf, RandomAccessFileProvider rafTemp) throws CannotReadException, CannotWriteException, IOException
    {
        throw new UnsupportedOperationException("Old method not used in version 2");
    }
}
