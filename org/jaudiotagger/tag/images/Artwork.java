package org.jaudiotagger.tag.images;

import org.extra.FileSystemProvider;
import org.jaudiotagger.audio.flac.metadatablock.MetadataBlockDataPicture;

import java.io.IOException;

/**
 * Represents artwork in a format independent  way
 */
public interface Artwork
{
    public byte[] getBinaryData();


    public void setBinaryData(byte[] binaryData);

    public String getMimeType();

    public void setMimeType(String mimeType);

    public String getDescription();

    public int getHeight();

    public int getWidth();

    public void setDescription(String description);

    /**
     * Should be called when you wish to prime the artwork for saving
     *
     * @return
     */
    public boolean setImageFromData();

    //public Object getImage() throws IOException;

    public boolean isLinked();

    public void setLinked(boolean linked);

    public String getImageUrl();

    public void setImageUrl(String imageUrl);

    public int getPictureType();

    public void setPictureType(int pictureType);

    /**
     * Create Artwork from File
     *
     * @param provider
     * @throws IOException
     */
    public void setFromFile(FileSystemProvider provider)  throws IOException;

    /**
     * Populate Artwork from MetadataBlockDataPicture as used by Flac and VorbisComment
     *
     * @param coverArt
     */
    public void setFromMetadataBlockDataPicture(MetadataBlockDataPicture coverArt);


    public void setWidth(int width);

    public void setHeight(int height);
}
