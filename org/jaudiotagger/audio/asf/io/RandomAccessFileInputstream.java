package org.jaudiotagger.audio.asf.io;

import org.extra.RandomAccessFileProvider;

import java.io.IOException;
import java.io.InputStream;

/**
 * Wraps a {@link RandomAccessFileProvider} into an {@link InputStream}.<br>
 *
 * @author Christian Laireiter
 */
public final class RandomAccessFileInputstream extends InputStream
{

    /**
     * The file access to read from.<br>
     */
    private final RandomAccessFileProvider source;

    /**
     * Creates an instance that will provide {@link InputStream} functionality
     * on the given {@link RandomAccessFileProvider} by delegating calls.<br>
     *
     * @param file The file to read.
     */
    public RandomAccessFileInputstream(final RandomAccessFileProvider file)
    {
        super();
        if (file == null)
        {
            throw new IllegalArgumentException("null");
        }
        this.source = file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws IOException
    {
        return this.source.read();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(final byte[] buffer, final int off, final int len) throws IOException
    {
        return this.source.read(buffer, off, len);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long skip(final long amount) throws IOException
    {
        if (amount < 0)
        {
            throw new IllegalArgumentException("invalid negative value");
        }
        long left = amount;
        while (left > Integer.MAX_VALUE)
        {
            this.source.skipBytes(Integer.MAX_VALUE);
            left -= Integer.MAX_VALUE;
        }
        return this.source.skipBytes((int) left);
    }

}
