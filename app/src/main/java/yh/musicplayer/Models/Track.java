package yh.musicplayer.Models;

/**
 * Created by Magnus on 2014-10-01.
 */
public class Track {

    //Class variables
    private String name;
    private String artist;
    private String path;

    private Track nextTrack = null;

    public Track(String name, String artist, String path){

        this.name = name;
        this.artist = artist;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getPath() {
        return path;
    }

    public Track getNextTrack() {
        return nextTrack;
    }

    public void setNextTrack(Track track) {
        this.nextTrack = track;
    }

    public String toString(){
        return name.split(".mp3")[0];
    }
}
