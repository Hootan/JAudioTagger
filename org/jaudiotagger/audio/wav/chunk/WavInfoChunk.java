package org.jaudiotagger.audio.wav.chunk;

import android.text.TextUtils;

import org.extra.StandardCharsets;
import org.extra.Utils;
import org.jaudiotagger.audio.iff.IffHeaderChunk;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.wav.WavInfoTag;
import org.jaudiotagger.tag.wav.WavTag;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stores basic only metadata but only exists as part of a LIST chunk, doesn't have its own size field
 * instead contains a number of name,size, value tuples. So for this reason we do not subclass the Chunk class
 */
public class WavInfoChunk
{
    public static Logger logger = Logger.getLogger("org.jaudiotagger.audio.wav.WavInfoChunk");

    private WavInfoTag wavInfoTag;
    private String    loggingName;

    public WavInfoChunk(WavTag tag, String loggingName)
    {
        this.loggingName = loggingName;
        wavInfoTag = new WavInfoTag();
        tag.setInfoTag(wavInfoTag);
    }

    /**
     * Read Info chunk
     * @param chunkData
     */
    public  boolean readChunks(ByteBuffer chunkData)
    {
        while(chunkData.remaining()>= IffHeaderChunk.TYPE_LENGTH)
        {
            String id       = org.jaudiotagger.audio.generic.Utils.readFourBytesAsChars(chunkData);
            //Padding
            if(TextUtils.isEmpty(id.trim()))
            {
                return true;
            }
            int    size     = chunkData.getInt();

            if(
                    (!Utils.isAlphabetic(id.charAt(0)))||
                    (!Utils.isAlphabetic(id.charAt(1)))||
                    (!Utils.isAlphabetic(id.charAt(2)))||
                    (!Utils.isAlphabetic(id.charAt(3)))
               )
            {
                logger.severe(loggingName + "LISTINFO appears corrupt, ignoring:"+id+":"+size);
                return false;
            }

            String value =null;
            try
            {
                value = org.jaudiotagger.audio.generic.Utils.getString(chunkData, 0, size, StandardCharsets.UTF_8);
            }
            catch(BufferUnderflowException bue)
            {
                logger.log(Level.SEVERE, loggingName + "LISTINFO appears corrupt, ignoring:"+bue.getMessage(), bue);
                return false;
            }

            logger.config(loggingName + "Result:" + id + ":" + size + ":" + value + ":");
            WavInfoIdentifier wii = WavInfoIdentifier.getByCode(id);
            if(wii!=null && wii.getFieldKey()!=null)
            {
                try
                {
                    wavInfoTag.setField(wii.getFieldKey(), value);
                }
                catch(FieldDataInvalidException fdie)
                {
                    logger.log(Level.SEVERE, loggingName + fdie.getMessage(), fdie);
                }
            }
            //Add unless just padding
            else if(id!=null && !id.trim().equals(""))
            {
                wavInfoTag.addUnRecognizedField(id, value);
            }

            //Each tuple aligned on even byte boundary
            if (org.jaudiotagger.audio.generic.Utils.isOddLength(size) && chunkData.hasRemaining())
            {
                chunkData.get();
            }
        }
        return true;
    }
}
