package org.jaudiotagger.tag.images;

import org.extra.FileSystemProvider;
import org.jaudiotagger.audio.flac.metadatablock.MetadataBlockDataPicture;

import java.io.IOException;

/**
 * Get appropriate Artwork class
 */
public class ArtworkFactory
{


    public static Artwork getNew()
    {
        //Normal
        /*if(!TagOptionSingleton.getInstance().isAndroid())
        {
            return new StandardArtwork();
        }
        //Android
        else*/
        {
            return new AndroidArtwork();
        }
    }

    /**
     * Create Artwork instance from A Flac Metadata Block
     *
     * @param coverArt
     * @return
     */
    public static Artwork createArtworkFromMetadataBlockDataPicture(MetadataBlockDataPicture coverArt)
    {
        //Normal
        /*if(!TagOptionSingleton.getInstance().isAndroid())
        {
            return StandardArtwork.createArtworkFromMetadataBlockDataPicture(coverArt);
        }
        //Android
        else*/
        {
            return AndroidArtwork.createArtworkFromMetadataBlockDataPicture(coverArt);
        }
    }

    /**
     * Create Artwork instance from an image file
     *
     * @param provider
     * @return
     * @throws IOException
     */
    public static Artwork createArtworkFromFile(FileSystemProvider provider) throws IOException
    {
        //Normal
        /*if(!TagOptionSingleton.getInstance().isAndroid())
        {
            return StandardArtwork.createArtworkFromFile(provider);
        }
        //Android
        else*/
        {
            return AndroidArtwork.createArtworkFromFile(provider);
        }
    }

    /**
     * Create Artwork instance from an image file
     *
     * @param link
     * @return
     * @throws IOException
     */
    public static Artwork createLinkedArtworkFromURL(String link) throws IOException
    {
        //Normal
        /*if(!TagOptionSingleton.getInstance().isAndroid())
        {
            return StandardArtwork.createLinkedArtworkFromURL(link);
        }
        //Android
        else*/
        {
            return AndroidArtwork.createLinkedArtworkFromURL(link);
        }
    }
}
