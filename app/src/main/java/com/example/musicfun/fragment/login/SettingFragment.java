package com.example.musicfun.fragment.login;

import static android.content.Context.MODE_PRIVATE;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.musicfun.R;
import com.example.musicfun.activity.MusicbannerService;
import com.example.musicfun.activity.RegisterActivity;
import com.example.musicfun.activity.SettingActivity;
import com.example.musicfun.adapter.login.SettingAdapter;
import com.example.musicfun.databinding.FragmentSettingBinding;
import com.example.musicfun.datatype.Songs;
import com.example.musicfun.viewmodel.login.SettingViewModel;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends Fragment {

    private FragmentSettingBinding binding;
    private SharedPreferences sp;
    private ArrayList<String> options;
    private SettingViewModel viewModel;
    private ListView listView;
    private TextView tv;
    private MusicbannerService service;
    private ExoPlayer player;
    private List<Songs> songInfo = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        sp = getContext().getSharedPreferences("login", MODE_PRIVATE);
        tv = binding.displayUsername;
        if(sp.getInt("logged", 999) != 1) {
            System.out.println("The user has not logged in, but can see the setting page!");
        }
        tv.setText("Welcome " + sp.getString("name", "") + "!");
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = binding.listSettings;
        options = new ArrayList<>();
        String option1 = getString(R.string.reset_pw);
        String option2 = getString(R.string.change_genre);
        String option3 = getString(R.string.action_logout);
        options.add(option1);
        options.add(option2);
        options.add(option3);

        listView.setAdapter(new SettingAdapter(options, getContext()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.setting_container, new ResetFragment()).commit();
                        break;
                    case 1:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.setting_container, new GenreFragment().newInstance(true)).commit();
                        break;
                    case 2:
                        logout();
                        break;
                    default:
                        System.out.println("no match!");
                }
            }
        });
    }

    // When user logs out, all data saved in SharedPreference should be cleared. The current status in player will be sent to the server.
    public void logout() {
        ((SettingActivity) requireActivity()).getService().observe(getViewLifecycleOwner(), new Observer<MusicbannerService>() {
            @Override
            public void onChanged(MusicbannerService musicbannerService) {
                if(musicbannerService != null){
                    service = musicbannerService;
                    player = service.player;
                }
            }
        });
        sp.edit().putInt("logged", -1).apply();
        sp.edit().putString("name", "").apply();

        int startItemIndex = player.getCurrentMediaItemIndex();
        long startPosition = Math.max(0, player.getContentPosition());
        songInfo = service.getSongInfo();
        List<Songs> restOfPlaylist = songInfo.subList(startItemIndex, songInfo.size());
        Gson gson = new Gson();
        String json = gson.toJson(restOfPlaylist);
        viewModel.saveDataWhenLogout(startItemIndex, startPosition, json);
        sp.edit().putInt("startItemIndex", 0).apply();
        sp.edit().putLong("startPosition", 0).apply();
        sp.edit().putString("saved_playlist", "").apply();

        getActivity().getApplicationContext().stopService(new Intent(getContext(), MusicbannerService.class));

        Intent i = new Intent(getActivity(), RegisterActivity.class);
        startActivity(i);
        getActivity().finish();
    }
}