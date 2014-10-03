package yh.musicplayer.Models;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Magnus on 2014-10-01.
 */
public class TrackListHelper {

    //Constants
    public final static int PLAY = 1;
    public final static int PAUSE = 2;
    public final static int PLAY_SONG = 3;

    public final static String TRACK = "track";
    public final static String ACTION = "action";

    //class variables
    private Context context;
    public static int numberOfTracks;
    public TrackListHelper(Context context){
        this.context = context;
    }

    public boolean checkIfStorageAvailable(){

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){

            return true;
        }

        return false;
    }

    public ArrayList<Track> getPlayList(){
        Log.d("MusicService: ", "getPlayList called!");

        Cursor musicResult = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.DATA },
                MediaStore.Audio.Media.IS_MUSIC + " > 0 ",
                null,
                null
        );

        ArrayList<Track> trackList = new ArrayList<Track>();

        numberOfTracks = musicResult.getCount();

        if(numberOfTracks > 0){
            musicResult.moveToFirst();
            Track previous = null;

            do{
                Track track = new Track(
                        musicResult.getString(musicResult.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)),
                        musicResult.getString(musicResult.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                        musicResult.getString(musicResult.getColumnIndex(MediaStore.Audio.Media.DATA))
                );


                if (previous != null){
                    previous.setNextTrack(track);
                }

                previous = track;
                trackList.add(track);

            }
            while (musicResult.moveToNext());

            previous.setNextTrack(trackList.get(0));

        }

        Log.d("MusicService: ", "Number of songs " + numberOfTracks);

        musicResult.close();

        return trackList;
    }

}
