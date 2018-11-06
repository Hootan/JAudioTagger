package org.jaudiotagger.audio;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import org.extra.FileProvider;
import org.extra.FileSystemProvider;
import org.extra.RandomAccessFileProvider;
import org.extra.Utils;
import org.jaudiotagger.audio.dsf.Dsf;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.NoReadPermissionsException;
import org.jaudiotagger.audio.exceptions.NoWritePermissionsException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.flac.metadatablock.MetadataBlockDataPicture;
import org.jaudiotagger.audio.generic.Permissions;
import org.jaudiotagger.audio.real.RealTag;
import org.jaudiotagger.logging.ErrorMessage;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagOptionSingleton;
import org.jaudiotagger.tag.aiff.AiffTag;
import org.jaudiotagger.tag.asf.AsfTag;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v22Tag;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.mp4.Mp4Tag;
import org.jaudiotagger.tag.reference.ID3V2Version;
import org.jaudiotagger.tag.vorbiscomment.VorbisCommentTag;
import org.jaudiotagger.tag.wav.WavTag;
import org.jcodec.common.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * <p>This is the main object manipulated by the user representing an audiofile, its properties and its tag.
 * <p>The preferred way to obtain an <code>AudioFile</code> is to use the <code>AudioFileIO.read(File)</code> method.
 * <p>The <code>AudioHeader</code> contains every properties associated with the file itself (no meta-data), like the bitrate, the sampling rate, the encoding audioHeaders, etc.
 * <p>To get the meta-data contained in this file you have to get the <code>Tag</code> of this <code>AudioFile</code>
 *
 * @author Raphael Slinckx
 * @version $Id$
 * @see AudioFileIO
 * @see Tag
 * @since v0.01
 */
public class AudioFile
{
    //Logger
    public static Logger logger = Logger.getLogger("org.jaudiotagger.audio");

    /**
     *
     * The physical file that this instance represents.
     */
    protected FileSystemProvider provider;

    /**
     * The Audio header info
     */
    protected AudioHeader audioHeader;

    /**
     * The tag
     */
    protected Tag tag;
    
    /**
     * The tag
     */
    protected String extension;
    public final boolean isAudio;

    public AudioFile()
    {
        isAudio = true;
    }

    /**
     * <p>These constructors are used by the different readers, users should not use them, but use the <code>AudioFileIO.read(File)</code> method instead !.
     * <p>Create the AudioFile representing file denoted by pathnames, the encoding audio Headers and containing the tag
     *
     * @param provider           The audio file
     * @param audioHeader the encoding audioHeaders over this file
     * @param tag         the tag contained in this file
     */
    public AudioFile(FileSystemProvider provider, AudioHeader audioHeader, Tag tag)
    {
        this.provider = provider;
        this.audioHeader = audioHeader;
        this.tag = tag;

        //Modifiedd
        this.isAudio = !"wmv".equalsIgnoreCase(provider.extension());
    }

    /**
     * <p>Write the tag contained in this AudioFile in the actual file on the disk, this is the same as calling the <code>AudioFileIO.write(this)</code> method.
     *
     * @throws NoWritePermissionsException if the file could not be written to due to file permissions
     * @throws CannotWriteException If the file could not be written/accessed, the extension wasn't recognized, or other IO error occured.
     * @see AudioFileIO
     */
    public void commit() throws CannotWriteException
    {
        AudioFileIO.write(this);
    }

    /**
     * <p>Delete any tags that exist in the fie , this is the same as calling the <code>AudioFileIO.delete(this)</code> method.
     *
     * @throws CannotWriteException If the file could not be written/accessed, the extension wasn't recognized, or other IO error occured.
     * @see AudioFileIO
     */
    public void delete() throws CannotReadException, CannotWriteException
    {
        AudioFileIO.delete(this);
    }

    /**
     * Set the file to store the info in
     *
     * @param provider
     */
    public void setProvider(FileSystemProvider provider)
    {
        this.provider = provider;
    }
    public void setFile(java.io.File file)
    {
        this.provider = new FileProvider(file);
    }

    /**
     * Retrieve the physical file
     *
     * @return
     */
    public FileSystemProvider getProvider()
    {
        return provider;
    }

    /**
     * Set the file extension
     *
     * @param ext
     */
    public void setExt(String ext)
    {
        this.extension = ext;
    }

    /**
     * Retrieve the file extension
     *
     * @return
     */
    public String getExt()
    {
        return extension;
    }

    /**
     *  Assign a tag to this audio file
     *  
     *  @param tag   Tag to be assigned
     */
    public void setTag(Tag tag)
    {
        this.tag = tag;
    }

    /**
     * Return audio header information
     * @return
     */
    public AudioHeader getAudioHeader()
    {
        return audioHeader;
    }

    /**
     * <p>Returns the tag contained in this AudioFile, the <code>Tag</code> contains any useful meta-data, like
     * artist, album, title, etc. If the file does not contain any tag the null is returned. Some audio formats do
     * not allow there to be no tag so in this case the reader would return an empty tag whereas for others such
     * as mp3 it is purely optional.
     *
     * @return Returns the tag contained in this AudioFile, or null if no tag exists.
     */
    public Tag getTag()
    {
        return tag;
    }

    /**
     * <p>Returns a multi-line string with the file path, the encoding audioHeader, and the tag contents.
     *
     * @return A multi-line string with the file path, the encoding audioHeader, and the tag contents.
     *         TODO Maybe this can be changed ?
     */
    public String toString()
    {
        return "AudioFile " + getProvider().getAbsolutePath()
                + "  --------\n" + audioHeader.toString() + "\n" + ((tag == null) ? "" : tag.toString()) + "\n-------------------";
    }

    /**
     * Check does file exist
     *
     * @param provider
     * @throws FileNotFoundException  if file not found
     */
    public void checkFileExists(FileSystemProvider provider)throws FileNotFoundException
    {
        logger.config("Reading file:" + "getPath" + provider.getPath() + ":abs:" + provider.getAbsolutePath());
        if (!provider.exists())
        {
            logger.severe("Unable to find:" + provider.getPath());
            throw new FileNotFoundException(ErrorMessage.UNABLE_TO_FIND_FILE.getMsg(provider.getPath()));
        }
    }

    /**
     * Checks the file is accessible with the correct permissions, otherwise exception occurs
     *
     * @param provider
     * @param readOnly
     * @throws ReadOnlyFileException
     * @throws FileNotFoundException
     * @return
     */
    protected RandomAccessFileProvider checkFilePermissions(FileSystemProvider provider, boolean readOnly) throws ReadOnlyFileException, FileNotFoundException, CannotReadException
    {
        RandomAccessFileProvider newFile;
        checkFileExists(provider);

        // Unless opened as readonly the file must be writable
        if (readOnly)
        {
            //May not even be readable
            if(!provider.canRead())
            {
                logger.severe("Unable to read file:" + provider.getPath());
                logger.severe(Permissions.displayPermissions(provider));
                throw new NoReadPermissionsException(ErrorMessage.GENERAL_READ_FAILED_DO_NOT_HAVE_PERMISSION_TO_READ_FILE.getMsg(provider.getPath()));
            }
            newFile = provider.getRandomAccessFile("r");
        }
        else
        {
            if (TagOptionSingleton.getInstance().isCheckIsWritable() && !provider.canWrite())
            {
                logger.severe(Permissions.displayPermissions(provider));
                throw new ReadOnlyFileException(ErrorMessage.NO_PERMISSIONS_TO_WRITE_TO_FILE.getMsg(provider.getPath()));
            }
            newFile = provider.getRandomAccessFile("rw");
        }
        return newFile;
    }

    /**
     * Optional debugging method. Must override to do anything interesting.
     *
     * @return  Empty string. 
     */
    public String displayStructureAsXML()
    {
        return "";
    }

    /**
     * Optional debugging method. Must override to do anything interesting.
     *
     * @return
     */
    public String displayStructureAsPlainText()
    {
        return "";
    }


    /** Create Default Tag
     *
     * @return
     */
    public Tag createDefaultTag()
    {
        String extension = getExt();
        if(extension == null)
        {
            String fileName = provider.getName();
            extension = fileName.substring(fileName.lastIndexOf('.') + 1);
            setExt(extension);
        }
        if(SupportedFileFormat.FLAC.getFilesuffix().equals(extension))
        {
            return new FlacTag(VorbisCommentTag.createNewTag(), new ArrayList< MetadataBlockDataPicture >());
        }
        else if(SupportedFileFormat.OGG.getFilesuffix().equals(extension))
        {
            return VorbisCommentTag.createNewTag();
        }
        else if(SupportedFileFormat.MP4.getFilesuffix().equals(extension))
        {
            return new Mp4Tag();
        }
        else if(SupportedFileFormat.M4A.getFilesuffix().equals(extension))
        {
            return new Mp4Tag();
        }
        else if(SupportedFileFormat.M4P.getFilesuffix().equals(extension))
        {
            return new Mp4Tag();
        }
        else if(SupportedFileFormat.WMA.getFilesuffix().equals(extension))
        {
            return new AsfTag();
        }
        else if(SupportedFileFormat.WAV.getFilesuffix().equals(extension))
        {
            return new WavTag(TagOptionSingleton.getInstance().getWavOptions());
        }
        else if(SupportedFileFormat.RA.getFilesuffix().equals(extension))
        {
            return new RealTag();
        }
        else if(SupportedFileFormat.RM.getFilesuffix().equals(extension))
        {
            return new RealTag();
        }
        else if(SupportedFileFormat.AIF.getFilesuffix().equals(extension))
        {
            return new AiffTag();
        }
        else if(SupportedFileFormat.AIFC.getFilesuffix().equals(extension))
        {
            return new AiffTag();
        }
        else if(SupportedFileFormat.AIFF.getFilesuffix().equals(extension))
        {
            return new AiffTag();
        }
        else if(SupportedFileFormat.DSF.getFilesuffix().equals(extension))
        {
            return Dsf.createDefaultTag();
        }
        else
        {
            throw new RuntimeException("Unable to create default tag for this file format");
        }

    }

    /**
     * Get the tag or if the file doesn't have one at all, create a default tag  and return
     *
     * @return
     */
    public Tag getTagOrCreateDefault()
    {
        Tag tag = getTag();
        if(tag==null)
        {
            return createDefaultTag();
        }
        return tag;
    }

     /**
     * Get the tag or if the file doesn't have one at all, create a default tag and set it
     * as the tag of this file
     *
     * @return
     */
    public Tag getTagOrCreateAndSetDefault()
    {
        Tag tag = getTagOrCreateDefault();
        setTag(tag);
        return tag;
    }

    /**
     * Get the tag and convert to the default tag version or if the file doesn't have one at all, create a default tag
     * set as tag for this file
     *
     * Conversions are currently only necessary/available for formats that support ID3
     *
     * @return
     */
    public Tag getTagAndConvertOrCreateAndSetDefault()
    {
        Tag tag = getTagOrCreateDefault();

        /* TODO Currently only works for Dsf We need additional check here for Wav and Aif because they wrap the ID3 tag so never return
         * null for getTag() and the wrapper stores the location of the existing tag, would that be broken if tag set to something else
         */
        if(tag instanceof AbstractID3v2Tag)
        {
            Tag convertedTag = convertID3Tag((AbstractID3v2Tag)tag, TagOptionSingleton.getInstance().getID3V2Version());
            if(convertedTag!=null)
            {
                setTag(convertedTag);
            }
            else
            {
                setTag(tag);
            }
        }
        else
        {
            setTag(tag);
        }
        return getTag();
    }

    /**
     *
     * @param provider
     * @return filename with audioFormat separator stripped off.
     */
    public static String getBaseFilename(FileSystemProvider provider)
    {
        int index= provider.getName().toLowerCase().lastIndexOf(".");
        if(index>0)
        {
            return provider.getName().substring(0,index);
        }
        return provider.getName();
    }

    /**
     * If using ID3 format convert tag from current version to another as specified by id3V2Version,
     *
     * @return null if no conversion necessary
     */
    public AbstractID3v2Tag convertID3Tag(AbstractID3v2Tag tag, ID3V2Version id3V2Version)
    {
        if(tag instanceof ID3v24Tag)
        {
            switch(id3V2Version)
            {
                case ID3_V22:
                    return new ID3v22Tag((ID3v24Tag)tag);
                case ID3_V23:
                    return new ID3v23Tag((ID3v24Tag)tag);
                case ID3_V24:
                    return null;
            }
        }
        else if(tag instanceof ID3v23Tag)
        {
            switch(id3V2Version)
            {
                case ID3_V22:
                    return new ID3v22Tag((ID3v23Tag)tag);
                case ID3_V23:
                    return null;
                case ID3_V24:
                    return new ID3v24Tag((ID3v23Tag)tag);
            }
        }
        else if(tag instanceof ID3v22Tag)
        {
            switch(id3V2Version)
            {
                case ID3_V22:
                    return null;
                case ID3_V23:
                    return new ID3v23Tag((ID3v22Tag)tag);
                case ID3_V24:
                    return new ID3v24Tag((ID3v22Tag)tag);
            }
        }
        return null;
    }

    //Modifiedd
    public void setTagValues(String[] values, FileSystemProvider provider, boolean single,
                             boolean removeCover, boolean addCover) throws Throwable {
        //we need other tags
        Tag newTag = getTagOrCreateDefault();
        //Tag newTag = (Tag) getTagAndConvertOrCreateAndSetDefault(audio);

        if (!TextUtils.isEmpty(values[0]) || (values[0] != null && single))
            newTag.setTitle2(values[0]);
        if (!TextUtils.isEmpty(values[1]) || (values[1] != null && single))
            newTag.setComment2(values[1]);
        if (!TextUtils.isEmpty(values[2]) || (values[2] != null && single))
            newTag.setArtist2(values[2]);
        if (!TextUtils.isEmpty(values[3]) || (values[3] != null && single))
            newTag.setAlbumArtist2(values[3]);
        if (!TextUtils.isEmpty(values[4]) || (values[4] != null && single))
            newTag.setAlbum2(values[4]);
        if (!TextUtils.isEmpty(values[5]) || (values[5] != null && single)) {
            if (values[5].length() == 0)
                newTag.deleteGenre2();
            else
                newTag.setGenre2(values[5]);
        }
        if (!TextUtils.isEmpty(values[6]) || (values[6] != null && single)) {
            if (values[6].length() == 0)
                newTag.deleteYear2();
            else
                newTag.setYear2(values[6]);
        }
        if (!TextUtils.isEmpty(values[7]) || (values[7] != null && single))
            newTag.setComposer2(values[7]);
        if (!TextUtils.isEmpty(values[8]) || (values[8] != null && single))
            newTag.setPublisher2(values[8]);
        //if (!TextUtils.isEmpty(values[9]) || (values[9] != null && single))
        //    newTag.setDiskNumber(values[9]);
        if (!TextUtils.isEmpty(values[10]) || (values[10] != null && single))
            newTag.setEncoder2(values[10]);
        if (!TextUtils.isEmpty(values[11]) || (values[11] != null && single))
            newTag.setCopyright2(values[11]);
        if (!TextUtils.isEmpty(values[12]) || (values[12] != null && single)) {
            if (values[12].length() == 0)
                newTag.deleteTrack2();
            else
                newTag.setTrack2(values[12]);
        }
        if (!TextUtils.isEmpty(values[13]) || (values[13] != null && single))
            newTag.setLyrics2(values[13]);
        if (!TextUtils.isEmpty(values[14]) || (values[14] != null && single))
            newTag.setItunesNORM(values[14]);
        if (!TextUtils.isEmpty(values[15]) || (values[15] != null && single))
            newTag.setItunesSMPB(values[15]);
        if (!TextUtils.isEmpty(values[16]) || (values[16] != null && single))
            newTag.setCustomTag(values[16]);
        if (!TextUtils.isEmpty(values[17]) || (values[17] != null && single)) {
            if (values[17].length() == 0)
                newTag.deleteDisc2();
            else
                newTag.setDisc2(values[17]);
        }

        //Log.d(">>>", albumArt + ", " + canSaveArtwork + ", " + single + ", " + removeAllCovers);
        if (removeCover) {
            try {
                newTag.deleteArtworkField();
            } catch (Throwable ignore) {
            }
        } else if(addCover) {
            try {
                newTag.deleteArtworkField();
            } catch (Throwable ignore) {
            }

            try {
                byte[] albumArt = provider.getBytes(0);
                if (albumArt == null)
                    throw new Exception("Image Null!");

                newTag.setField(newTag.createField(Utils.createArtwork(albumArt, provider.mimeType())));

            } catch (Throwable e) {
                Log.e("CMD_COVER", e.toString());
            }
        }

        setTag(newTag);
    }

}
