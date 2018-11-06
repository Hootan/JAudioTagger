/*
 * Entagged Audio Tag library
 * Copyright (c) 2003-2005 Rapha�l Slinckx <raphael@slinckx.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jaudiotagger.tag.wav;

import org.jaudiotagger.audio.generic.GenericTag;
import org.jaudiotagger.audio.iff.ChunkHeader;
import org.jaudiotagger.logging.Hex;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.TagTextField;
import org.jaudiotagger.tag.images.Artwork;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Represent wav metadata found in the LISTINFO Chunk
 *
 * An LIST INFO chunk was the original way to store metadata but simailr to ID3v1 it suffers from a limited
 * set of fields, although non-standard extra field cannot be added, notably there is no support for images.
 *
 * Any Wavc editors now instead/addtionally add data with an ID3tag
 */
public class WavInfoTag extends GenericTag
{
    //We dont use these fields but we need to read them so they can be written back if user modifies
    private List<TagTextField> unrecognisedFields = new ArrayList<>();

    private Long startLocationInFile = null;

    //End location of this chunk
    private Long endLocationInFile = null;

    static
    {
        supportedKeys = EnumSet.of(
                FieldKey.ALBUM,
                FieldKey.ARTIST,
                FieldKey.ALBUM_ARTIST,
                FieldKey.TITLE,
                FieldKey.TRACK,
                FieldKey.GENRE,
                FieldKey.COMMENT,
                FieldKey.YEAR,
                FieldKey.RECORD_LABEL,
                FieldKey.ISRC,
                FieldKey.COMPOSER,
                FieldKey.LYRICIST,
                FieldKey.ENCODER,
                FieldKey.CONDUCTOR,
                FieldKey.RATING,
                //Modifiedd
                FieldKey.COPYRIGHT,
                FieldKey.DISC_NO,
                FieldKey.LYRICS
        );
    }
    public String toString()
    {
        StringBuilder  output = new StringBuilder("Wav Info Tag:\n");
        if(getStartLocationInFile()!=null)
        {
            output.append("\tstartLocation:" + Hex.asDecAndHex(getStartLocationInFile()) + "\n");
        }
        if(getEndLocationInFile()!=null)
        {
            output.append("\tendLocation:" + Hex.asDecAndHex(getEndLocationInFile()) + "\n");
        }
        output.append(super.toString());
        if(unrecognisedFields.size()>0)
        {
            output.append("\nUnrecognized Tags:\n");
            for(TagTextField next:unrecognisedFields)
            {
                output.append("\t"+next.getId()+":"+next.getContent()+"\n");
            }
        }
        return output.toString();
    }

    public TagField createCompilationField(boolean value) throws KeyNotFoundException, FieldDataInvalidException
    {
        return createField(FieldKey.IS_COMPILATION,String.valueOf(value));
    }

    public Long getStartLocationInFile()
    {
        return startLocationInFile;
    }

    public void setStartLocationInFile(long startLocationInFile)
    {
        this.startLocationInFile = startLocationInFile;
    }

    public Long getEndLocationInFile()
    {
        return endLocationInFile;
    }

    public void setEndLocationInFile(long endLocationInFile)
    {
        this.endLocationInFile = endLocationInFile;
    }

    public long getSizeOfTag()
    {
        if(endLocationInFile==null || startLocationInFile==null)
        {
            return 0;
        }
        return (endLocationInFile - startLocationInFile) - ChunkHeader.CHUNK_HEADER_SIZE;
    }

    public void addUnRecognizedField(String code, String contents)
    {
        unrecognisedFields.add(new GenericTagTextField(code, contents));
    }

    public List<TagTextField> getUnrecognisedFields()
    {
        return unrecognisedFields;
    }

    /**
     * Read
     */
    @Override
    public String getTitle2() {
        return getFirst(FieldKey.TITLE);
    }

    @Override
    public String getComment2() {
        return getFirst(FieldKey.COMMENT);
    }

    @Override
    public String getArtist2() {
        return getFirst(FieldKey.ARTIST);
    }

    @Override
    public String getAlbumArtist2() {
        return getFirst(FieldKey.ALBUM_ARTIST);
    }

    @Override
    public String getAlbum2() {
        return getFirst(FieldKey.ALBUM);
    }

    @Override
    public String getGenre2() {
        return getFirst(FieldKey.GENRE);
    }

    @Override
    public String getYear2() {
        return getFirst(FieldKey.YEAR);
    }

    @Override
    public String getComposer2() {
        return getFirst(FieldKey.COMPOSER);
    }

    @Override
    public String getPublisher2() {
        return getFirst(FieldKey.RECORD_LABEL);
    }

    @Override
    public String getEncoder2() {
        return getFirst(FieldKey.ENCODER);
    }

    @Override
    public String getCopyright2() {
        return null;//getFirst(FieldKey.COPYRIGHT);
    }

    @Override
    public String getTrack2() {
        return getFirst(FieldKey.TRACK);
    }

    @Override
    public String getDisc2() {
        //Not supported
        return null;//getFirst(FieldKey.DISC_NO);
    }

    @Override
    public String getLyrics2() {
        //Not supported
        return null;//getFirst(FieldKey.LYRICS);
    }

    @Override
    public String getItunesNorm() {
        return null;
    }

    @Override
    public String getItunesSMPB() {
        return null;
    }

    @Override
    public String getCustomTag() {
        return null;
    }

    @Override
    public Object[] getArtwork2() {
        try {
            Artwork artwork = getFirstArtwork();
            byte[] bytes = artwork != null ? artwork.getBinaryData() : null;
            return bytes != null ? new Object[]{artwork.getMimeType(), bytes} : null;
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * Write
     */
    @Override
    public void setTitle2(String data) throws Throwable {
        setField(FieldKey.TITLE, data);
    }

    @Override
    public void setComment2(String data) throws Throwable {
        setField(FieldKey.COMMENT, data);
    }

    @Override
    public void setArtist2(String data) throws Throwable {
        setField(FieldKey.ARTIST, data);
    }

    @Override
    public void setAlbumArtist2(String data) throws Throwable {
        setField(FieldKey.ALBUM_ARTIST, data);
    }

    @Override
    public void setAlbum2(String data) throws Throwable {
        setField(FieldKey.ALBUM, data);
    }

    @Override
    public void setGenre2(String data) throws Throwable {
        setField(FieldKey.GENRE, data);
    }

    @Override
    public void deleteGenre2() throws Throwable {
        deleteField(FieldKey.GENRE);
    }

    @Override
    public void deleteTrack2() throws Throwable {
        deleteField(FieldKey.TRACK);
    }

    @Override
    public void deleteDisc2() throws Throwable {
        //Not supported
        //deleteField(FieldKey.DISC_NO);
    }

    @Override
    public void deleteYear2() throws Throwable {
        deleteField(FieldKey.YEAR);
    }

    @Override
    public void setYear2(String data) throws Throwable {
        setField(FieldKey.YEAR, data);
    }

    @Override
    public void setComposer2(String data) throws Throwable {
        setField(FieldKey.COMPOSER, data);
    }

    @Override
    public void setPublisher2(String data) throws Throwable {
        setField(FieldKey.RECORD_LABEL, data);
    }

    @Override
    public void setEncoder2(String data) throws Throwable {
        setField(FieldKey.ENCODER, data);
    }

    @Override
    public void setCopyright2(String data) throws Throwable {
        //setField(FieldKey.COPYRIGHT, data);
    }

    @Override
    public void setTrack2(String data) throws Throwable {
        setField(FieldKey.TRACK, data);
    }

    @Override
    public void setDisc2(String data) throws Throwable {
        //Not supported
        //setField(FieldKey.DISC_NO, data);
    }

    @Override
    public void setLyrics2(String data) throws Throwable {
        //Not supported
        //setField(FieldKey.LYRICS, data);
    }

    @Override
    public void setItunesNORM(String comments) throws Throwable {

    }

    @Override
    public void setItunesSMPB(String comments) throws Throwable {

    }

    @Override
    public void setCustomTag(String custom) throws Throwable {

    }

}