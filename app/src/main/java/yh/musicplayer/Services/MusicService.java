package yh.musicplayer.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import yh.musicplayer.Activities.MainActivity;
import yh.musicplayer.Models.Track;
import yh.musicplayer.Models.TrackListHelper;
import yh.musicplayer.R;

public class MusicService extends Service {
    public MusicService() {
    }

    //Constants
    public final static String TRACK = "track";
    public final static String ACTION = "action";

    //class variables
    private MediaPlayer mediaPlayer;
    private Track currentTrack;
    private ArrayList<Track> trackList;
    private Notification notification;

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = new MediaPlayer();

        TrackListHelper trackListHelper = new TrackListHelper(this);

        //Check if the device has a SD card
        if(trackListHelper.checkIfStorageAvailable()){
            //Gets the play list
            trackList = trackListHelper.getPlayList();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        int track = intent.getIntExtra(TRACK, 0);
        int action = intent.getIntExtra(ACTION, 0);

        switch(action){

            case TrackListHelper.PLAY_SONG:
                musicPlayer(trackList.get(track));
            break;

            case TrackListHelper.PAUSE:
                mediaPlayer.pause();
            break;

            case TrackListHelper.PLAY:
                mediaPlayer.start();
            break;

            default:
            break;
        }

        return START_STICKY;
    }


    private void musicPlayer(Track track){
        if(track == null){
            return;
        }

        try{
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop(); //Stop the song
            }

            mediaPlayer.reset(); //resets the music players resource
            mediaPlayer.setDataSource(this, Uri.parse(track.getPath()));
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mediaPlayer.prepare(); //prepare recourse

            //On completion handler
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){

                @Override
                public void onCompletion(MediaPlayer mp){
                    //When done, change get next track
                    musicPlayer(currentTrack.getNextTrack());
                }
            });

            //Starts the music player
            mediaPlayer.start();
            currentTrack = track;
            onGoingNotification(currentTrack);

        } catch(Exception exception){
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
            Log.d("musicPlayer error: ", exception.toString());
        }

    }

    private void onGoingNotification(Track currentTrack) {
        final int myID = 666;

        //The intent to launch when the user clicks the expanded notification
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //This constructor is deprecated. Use Notification.Builder instead
        notification = new Notification(R.drawable.ic_launcher, currentTrack.getName().split(".mp3")[0] + " - " + currentTrack.getArtist(), 3);
        //This method is deprecated. Use Notification.Builder instead.
        notification.setLatestEventInfo(this, currentTrack.getName(), currentTrack.getArtist(), pendIntent);

        notification.flags |= Notification.FLAG_NO_CLEAR;

        startForeground(myID, notification);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //If a song is still playing, stop it and release it
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
