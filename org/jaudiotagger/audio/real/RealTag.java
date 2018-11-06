package org.jaudiotagger.audio.real;

import org.jaudiotagger.audio.generic.GenericTag;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.TagField;

public class RealTag extends GenericTag
{
    public String toString()
    {
        String output = "REAL " + super.toString();
        return output;
    }

    public TagField createCompilationField(boolean value) throws KeyNotFoundException, FieldDataInvalidException
    {
        return createField(FieldKey.IS_COMPILATION,String.valueOf(value));
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
        return null;
    }

    @Override
    public String getAlbum2() {
        return null;
    }

    @Override
    public String getGenre2() {
        return null;
    }

    @Override
    public String getYear2() {
        return null;
    }

    @Override
    public String getComposer2() {
        return null;
    }

    @Override
    public String getPublisher2() {
        return null;
    }

    @Override
    public String getEncoder2() {
        return null;
    }

    @Override
    public String getCopyright2() {
        return null;
    }

    @Override
    public String getTrack2() {
        return null;
    }

    @Override
    public String getDisc2() {
        return null;
    }

    @Override
    public String getLyrics2() {
        return null;
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
        return null;
    }

    /**
     * Write
     */
    @Override
    public void setTitle2(String data) throws Throwable {
    }

    @Override
    public void setComment2(String data) throws Throwable {
    }

    @Override
    public void setArtist2(String data) throws Throwable {
    }

    @Override
    public void setAlbumArtist2(String data) throws Throwable {
    }

    @Override
    public void setAlbum2(String data) throws Throwable {
    }

    @Override
    public void setGenre2(String data) throws Throwable {
    }

    @Override
    public void deleteGenre2() throws Throwable {
    }

    @Override
    public void deleteTrack2() throws Throwable {
    }

    @Override
    public void deleteDisc2() throws Throwable {
    }

    @Override
    public void deleteYear2() throws Throwable {
    }

    @Override
    public void setYear2(String data) throws Throwable {
    }

    @Override
    public void setComposer2(String data) throws Throwable {
    }

    @Override
    public void setPublisher2(String data) throws Throwable {
    }

    @Override
    public void setEncoder2(String data) throws Throwable {
    }

    @Override
    public void setCopyright2(String data) throws Throwable {
    }

    @Override
    public void setTrack2(String data) throws Throwable {
    }

    @Override
    public void setDisc2(String data) throws Throwable {
    }

    @Override
    public void setLyrics2(String data) throws Throwable {
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
