package org.jaudiotagger.audio.asf.io;

import org.extra.Utils;
import org.jaudiotagger.audio.asf.data.Chunk;
import org.jaudiotagger.audio.asf.data.ContentBranding;
import org.jaudiotagger.audio.asf.data.GUID;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * This reader is used to read the content branding object of ASF streams.<br>
 *
 * @author Christian Laireiter
 * @see org.jaudiotagger.audio.asf.data.ContainerType#CONTENT_BRANDING
 * @see ContentBranding
 */
public class ContentBrandingReader implements ChunkReader
{
    /**
     * The GUID this reader {@linkplain #getApplyingIds() applies to}
     */
    private final static GUID[] APPLYING = {GUID.GUID_CONTENT_BRANDING};

    /**
     * Should not be used for now.
     */
    protected ContentBrandingReader()
    {
        // NOTHING toDo
    }

    /**
     * {@inheritDoc}
     */
    public boolean canFail()
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public GUID[] getApplyingIds()
    {
        return APPLYING.clone();
    }

    /**
     * {@inheritDoc}
     */
    public Chunk read(final GUID guid, final InputStream stream, final long streamPosition) throws IOException
    {
        assert GUID.GUID_CONTENT_BRANDING.equals(guid);
        final BigInteger chunkSize = org.jaudiotagger.audio.asf.util.Utils.readBig64(stream);
        final long imageType = org.jaudiotagger.audio.asf.util.Utils.readUINT32(stream);
        assert imageType >= 0 && imageType <= 3 : imageType;
        final long imageDataSize = org.jaudiotagger.audio.asf.util.Utils.readUINT32(stream);
        assert imageType > 0 || imageDataSize == 0 : imageDataSize;
        assert imageDataSize < Integer.MAX_VALUE;
        final byte[] imageData = org.jaudiotagger.audio.asf.util.Utils.readBinary(stream, imageDataSize);
        final long copyRightUrlLen = org.jaudiotagger.audio.asf.util.Utils.readUINT32(stream);
        final String copyRight = Utils.getString(org.jaudiotagger.audio.asf.util.Utils.readBinary(stream, copyRightUrlLen));
        final long imageUrlLen = org.jaudiotagger.audio.asf.util.Utils.readUINT32(stream);
        final String imageUrl = Utils.getString(org.jaudiotagger.audio.asf.util.Utils.readBinary(stream, imageUrlLen));
        final ContentBranding result = new ContentBranding(streamPosition, chunkSize);
        result.setImage(imageType, imageData);
        result.setCopyRightURL(copyRight);
        result.setBannerImageURL(imageUrl);
        return result;
    }

}
