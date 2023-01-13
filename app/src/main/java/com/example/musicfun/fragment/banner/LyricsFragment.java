package com.example.musicfun.fragment.banner;

import static com.example.musicfun.activity.MusicbannerService.COPA_RESULT;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.musicfun.R;
import com.example.musicfun.activity.LyricsActivity;
import com.example.musicfun.activity.MusicbannerService;
import com.example.musicfun.databinding.FragmentLyricsBinding;
import com.example.musicfun.datatype.Lyrics;
import com.example.musicfun.datatype.RelativeSizeColorSpan;
import com.example.musicfun.datatype.Songs;
import com.example.musicfun.viewmodel.MainActivityViewModel;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LyricsFragment extends Fragment {

    private FragmentLyricsBinding binding;
    private MusicbannerService service;
    protected @Nullable ExoPlayer player;
    private BroadcastReceiver broadcastReceiver;
    private MainActivityViewModel viewModel;

    private StyledPlayerControlView controlView;
    private TextView tv_title;
    private String title = "";
    private TextView tv_artist;
    private String artist = "";
    private ImageView coverView;
    private ImageView btn_currentPlaylist;
    private ImageView btn_active_guests;

    // views and variables for the canvas
    private List<Lyrics> lyricsList;
    private int currentLine = -1;   // current singing row , should be highlighted.
    private TextView tv_lyrics;
    private boolean isVisible;

    private final int POLL_INTERVAL_MS_PLAYING = 1000;
    private final int POLL_INTERVAL_MS_PAUSED = 3000;
    int spanColorHighlight = Color.parseColor("#311B92");
    private RelativeSizeColorSpan highlight = new RelativeSizeColorSpan (1.3f, spanColorHighlight);
    private Spannable spannableText;
    private ScrollingMovementMethod scrolltext = new ScrollingMovementMethod();

    private boolean isBound;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentLyricsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isVisible = true;
        controlView = binding.playerView;
        controlView.setShowTimeoutMs(0);

//        Initialize the views
//        update title and artist
        tv_title = getView().findViewById(R.id.styled_player_song_name);
        tv_artist = getView().findViewById(R.id.styled_player_artist);
        coverView = getView().findViewById(R.id.imageView2);
        if(title != ""){
            tv_title.setText(title);
            tv_artist.setText(artist);
        }

//        lyrics relevant
        tv_lyrics = binding.lyrics;
        tv_lyrics.setMovementMethod(scrolltext);

        btn_currentPlaylist = getView().findViewById(R.id.current_playlist);
        btn_currentPlaylist.setOnClickListener(showCurrentPlaylist);
//        TODO: check who called this fragment
//        TODO: if it is from friends, then show active_guests button. Otherwise, hide this button
        btn_active_guests = getView().findViewById(R.id.active_participants);
        btn_active_guests.setOnClickListener(showActiveGuests);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                title = intent.getStringExtra("title");
                artist = intent.getStringExtra("artist");
                tv_title.setText(title);
                tv_artist.setText(artist);
                String coverUrl = intent.getStringExtra("coverUrl");
                changeCover(coverUrl);
            }
        };

    }

//    Bind service from fragment to make sure the service is bound on time
    private ServiceConnection playerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            isBound = true;
            MusicbannerService.ServiceBinder binder = (MusicbannerService.ServiceBinder) iBinder;
            service = binder.getMusicbannerService();

            player = service.player;
            controlView.setPlayer(player);

            if(player != null){
                String id = Objects.requireNonNull(player.getCurrentMediaItem()).mediaMetadata.description.toString();
                String coverUrl = "http://10.0.2.2:3000/images/" + id + ".jpg";
                Picasso.get().load(coverUrl).into(coverView);
                if (title.equals("") && artist.equals("")){
                    ((LyricsActivity)getActivity()).getSongTitle().observe(getViewLifecycleOwner(), new Observer<String>() {
                        @Override
                        public void onChanged(String s) {
                            if (!s.isEmpty()){
                                tv_title.setText(s);
                            }
                        }
                    });
                    ((LyricsActivity)getActivity()).getSongArtist().observe(getViewLifecycleOwner(), new Observer<String>() {
                        @Override
                        public void onChanged(String s) {
                            if (!s.isEmpty()){
                                tv_artist.setText(s);
                            }
                        }
                    });
                }
                updateLyricsFile();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private View.OnClickListener showActiveGuests = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // TODO: inform main activity to switch fragments

        }
    };

    private View.OnClickListener showCurrentPlaylist = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int startItemIndex = player.getCurrentMediaItemIndex();
            List<Songs> songInfo = service.getSongInfo();
            List<Songs> restOfPlaylist = songInfo.subList(startItemIndex, songInfo.size());
            Gson gson = new Gson();
            String json = gson.toJson(restOfPlaylist);

            NavDirections action = LyricsFragmentDirections.actionLyricsFragmentToCurrentPlaylistFragment(json);
            Navigation.findNavController(getView()).navigate(action);
            isVisible = false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        Intent musicbannerServiceIntent = new Intent(getActivity(), MusicbannerService.class);
        getActivity().bindService(musicbannerServiceIntent, playerServiceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver((broadcastReceiver), new IntentFilter(COPA_RESULT));
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
        doUnbindService();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        doUnbindService();
        super.onDestroy();
    }

    private void doUnbindService(){
        if (isBound){
            getActivity().unbindService(playerServiceConnection);
            isBound = false;
        }
    }

    private void updateLyricsFile(){
        lyricsList = new ArrayList<>();
        viewModel.fetchLyrics(player.getCurrentMediaItem().mediaMetadata.description.toString());
        viewModel.getM_songLrc().observe(getViewLifecycleOwner(), new Observer<ArrayList<Lyrics>>() {
            @Override
            public void onChanged(ArrayList<Lyrics> lyrics) {
                if(lyrics.size() != 0){
                    lyricsList = lyrics;
                    String allText = "";
                    for (Lyrics l : lyrics){
                        allText = allText + l.getLyrics() + "\n";
                    }
                    tv_lyrics.setText(allText, TextView.BufferType.SPANNABLE);
                    spannableText = (Spannable) tv_lyrics.getText();
                    getCurrentPlayerPosition();
                }
            }
        });
    }

    // checks the current player position every 500ms
    private void getCurrentPlayerPosition() {
        if(isVisible){
            long time = player.getCurrentPosition();
            int newLine = findLine(time);
            showCurrentLines(newLine);
            int[] startEnd = currentStartPoint(newLine);
            spannableText.setSpan(highlight, startEnd[0], startEnd[1], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            currentLine = newLine;

            if (player.isPlaying()) {
                time = player.getCurrentPosition();
                newLine = findLine(time);
                // update UI if line index has been changed
                if (newLine != currentLine){
                    // remove all existing span (old span)
                    spannableText.removeSpan(highlight);
                    // set span for the new line
                    startEnd = currentStartPoint(newLine);
                    spannableText.setSpan(highlight, startEnd[0], startEnd[1], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    showCurrentLines(newLine);
                    currentLine = newLine;
                }
            }
            controlView.postDelayed(this::getCurrentPlayerPosition, POLL_INTERVAL_MS_PLAYING);
        }
        else{
            controlView.postDelayed(this::getCurrentPlayerPosition, POLL_INTERVAL_MS_PAUSED);
        }
    }

    // find the start and end point of the current line. This helps to highlight the lyrics.
    private int[] currentStartPoint(int line){
        int start = 0;
        int[] result = new int[2];
        if (line == 0){
            result[0] = 0;
            result[1] = lyricsList.get(0).getLength() + 1;
            return result;
        }
        for (int i = 0; i < line; i++){
            start = start + lyricsList.get(i).getLength() + 1;
        }
        result[0] = start;
        result[1] = start + lyricsList.get(line).getLength();
        return result;
    }

    // Move the textview (including the hidden lines) so that the current line is always in the middle of the screen
    private void showCurrentLines(int i){
        int middleOfTextviewHeight = tv_lyrics.getHeight() / 2;
        tv_lyrics.scrollTo(0, -middleOfTextviewHeight + tv_lyrics.getLayout().getLineTop(i));
    }

    // returns the index of the current line of lyrics
    private int findLine(long currentTime){
        int i = 0;
        while(i < lyricsList.size() - 1){
            Lyrics first = lyricsList.get(i);
            Lyrics second = lyricsList.get(i + 1);
            if (currentTime >= first.getStartTime() && currentTime < second.getStartTime()){
                break;
            }
            i++;
        }
        return i;
    }

    public void changeCover(String url){
        Picasso.get().load(url).into(coverView);
    }
}
