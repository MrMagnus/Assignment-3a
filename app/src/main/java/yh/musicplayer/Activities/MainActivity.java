package yh.musicplayer.Activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import yh.musicplayer.Models.Track;
import yh.musicplayer.Models.TrackListHelper;
import yh.musicplayer.R;
import yh.musicplayer.Services.MusicService;


public class MainActivity extends ListActivity {

    //Constants
    final static int PLAY_PAUSE_BUTTON = 1;

    //Class Variables
    private Menu itemMenu;
    private TrackListHelper  trackListHelper;
    private int currentTrack;
    ArrayList<Track> trackList;

    private boolean isPlaying = false;
    private boolean isPaused = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trackListHelper = new TrackListHelper(this);

        //check if the device has an SD card
        if(trackListHelper.checkIfStorageAvailable()){
            Log.d("MusicService: ", "SD card Exists!");

            //gets the playlist
            trackList = trackListHelper.getPlayList();

            //create a new array adapter with the play list
            ArrayAdapter<Track> musicAdapter = new ArrayAdapter<Track>(this, android.R.layout.simple_list_item_1, trackList);

            setListAdapter(musicAdapter);

        } else{Log.d("MusicService: ", "SD card Not Exists!");}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.itemMenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Intent intentService = new Intent(this, MusicService.class);

        switch(item.getItemId()) {

            case R.id.actionPlayOrPause:

                if (isPlaying) {

                    //Changes the icon on the button in the action bar
                    itemMenu.getItem(PLAY_PAUSE_BUTTON).setIcon(R.drawable.ic_action_play);

                    //Pauses the song
                    intentService.putExtra(trackListHelper.ACTION, trackListHelper.PAUSE);

                    isPlaying = false;
                    isPaused = true;
                    startService(intentService);

                } else {
                    if (isPaused) {

                        //Changes the icon on the button in the action bar
                        itemMenu.getItem(PLAY_PAUSE_BUTTON).setIcon(R.drawable.ic_action_pause);

                        //Resumes paused song
                        intentService.putExtra(trackListHelper.ACTION, trackListHelper.PLAY);

                        isPlaying = true;
                        isPaused = false;
                        startService(intentService);

                    } else {
                        //Changes the icon on the button in the action bar
                        itemMenu.getItem(PLAY_PAUSE_BUTTON).setIcon(R.drawable.ic_action_pause);

                        //Resumes paused song
                        intentService.putExtra(trackListHelper.ACTION, trackListHelper.PLAY_SONG);

                        isPlaying = true;
                        isPaused = false;
                        startService(intentService);

                    }
                }
            break;

            case R.id.actionNext:

                if(currentTrack >= 0) {

                    currentTrack++;

                    //Changes the song to the next song if currentTrack is less than the length of trackList
                    if (currentTrack <= trackList.size() -1){

                        intentService.putExtra(trackListHelper.TRACK, currentTrack);
                        intentService.putExtra(trackListHelper.ACTION, trackListHelper.PLAY_SONG);

                        startService(intentService);

                    } else {
                        //If the currentTrack is bigger than the length of the trackList, change the
                        //currentTrack to 0 and play the song with that position.
                        currentTrack = 0;

                        intentService.putExtra(trackListHelper.TRACK, currentTrack);
                        intentService.putExtra(trackListHelper.ACTION, trackListHelper.PLAY_SONG);

                        startService(intentService);
                    }
                }

            break;

            case R.id.actionPrevious:
                //Changes the song to the previous song if currentTrack is higher than 0
                if(currentTrack >= 0){

                    currentTrack--;

                    if (currentTrack >= 0){

                        intentService.putExtra(trackListHelper.TRACK, currentTrack);
                        intentService.putExtra(trackListHelper.ACTION, trackListHelper.PLAY_SONG);

                        startService(intentService);
                    } else{
                        //If the currentTrack is lesser than 0, change the
                        //currentTrack to 0 and play the song with that position.
                        currentTrack = 0;

                        intentService.putExtra(trackListHelper.TRACK, currentTrack);
                        intentService.putExtra(trackListHelper.ACTION, trackListHelper.PLAY_SONG);

                        startService(intentService);
                    }
                }

            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {

        //creates a new intentService
        Intent intentService = new Intent(this, MusicService.class);

        //Gets the items position, and sends it to the service with the chosen Action.
        currentTrack = position;
        intentService.putExtra(trackListHelper.TRACK, currentTrack).putExtra(trackListHelper.ACTION, TrackListHelper.PLAY_SONG);
        startService(intentService);

        //Changes the icon on the button in the action bar
        itemMenu.getItem(PLAY_PAUSE_BUTTON).setIcon(R.drawable.ic_action_pause);
        isPlaying = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopService(new Intent(this, MusicService.class));
    }
}
