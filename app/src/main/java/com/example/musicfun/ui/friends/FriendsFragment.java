package com.example.musicfun.ui.friends;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.musicfun.R;
import com.example.musicfun.activity.RegisterActivity;
import com.example.musicfun.activity.SettingActivity;
import com.example.musicfun.adapter.search.SearchUserResultAdapter;
import com.example.musicfun.databinding.FragmentFriendsBinding;
import com.example.musicfun.datatype.User;
import com.example.musicfun.fragment.mymusic.MyPlaylistFragmentDirections;
import com.example.musicfun.fragment.sharedplaylist.SharedPlaylistFragmentDirections;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Objects;

public class FriendsFragment extends Fragment {

    private SharedPreferences sp;
    private FragmentFriendsBinding binding;

    //search view part
    SearchView searchView;
    ListView listView;
    FriendsViewModel friendsViewModel;
    SearchUserResultAdapter adapter;
    //----

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SimpleFriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        friendsViewModel = new ViewModelProvider(this).get(FriendsViewModel.class);
        binding = FragmentFriendsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boolean temp = isNetworkAvailable(getActivity().getApplication());
        if (!temp){
            System.out.println("network not connected!!");
            return;
        }

        sp = getContext().getSharedPreferences("login", MODE_PRIVATE);
//        int state = sp.getInt("logged", 999);
//        binding.friendsSetting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent gotoSetting = new Intent(getActivity(), SettingActivity.class);
////                System.out.println("state current = " + state);
//                if(state ==0){
//                    Intent gotoLogin = new Intent(getActivity(), RegisterActivity.class);
//                    sp.edit().putInt("logged", -1).apply();
//                    Toast.makeText(getContext(), R.string.login_required, Toast.LENGTH_SHORT).show();
////                    System.out.println("state after = " + sp.getInt("logged", 999));
//                    getActivity().startActivity(gotoLogin);
//                }
//                else{
//                    getActivity().startActivity(gotoSetting);
//                }
//            }
//        });


        //Search view part
//        searchView = binding.friendsSearchView;
//        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if(listView != null){
//                    listView.setVisibility(View.VISIBLE);
//                }
//                binding.friendsSetting.setVisibility(View.GONE);
//                binding.friendsCancel.setVisibility(View.VISIBLE);
//                // cancel the search
//                binding.friendsCancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        closeKeyboard(view);
//                        searchView.setQuery("", false);
//                        searchView.clearFocus();
//                        listView.setVisibility(View.INVISIBLE);
//                        binding.FriendsNav.setVisibility(View.VISIBLE);
//                        binding.friendsChildFragment.setVisibility((View.VISIBLE));
//                        binding.friendsSetting.setVisibility(View.VISIBLE);
//                        binding.friendsCancel.setVisibility(View.GONE);
//                    }
//                });
//                friendsViewModel.initSearch("get/allUsers?auth_token="  + sp.getString("token", ""));
//                binding.FriendsNav.setVisibility(View.INVISIBLE);
//                binding.friendsChildFragment.setVisibility((View.INVISIBLE));
//
//                listView = binding.friendsList;
//
//                friendsViewModel.getUserNames().observe(getViewLifecycleOwner(), new Observer<ArrayList<User>>() {
//                    @Override
//                    public void onChanged(@Nullable final ArrayList<User> users) {
//                        adapter = new SearchUserResultAdapter(getActivity(), users);
//                        // binds the Adapter to the ListView
//                        listView.setAdapter(adapter);
//                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
//
//                            @Override
//                            public boolean onQueryTextSubmit(String query) {
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onQueryTextChange(String newTest) {
//                                String text = newTest;
//                                friendsViewModel.filter(text, sp.getString("token", ""));
//                                return true;
//                            }
//                        });
//                    }
//                });
//
//                listView.setOnTouchListener(new View.OnTouchListener() {
//                    // hide soft keyboard if a user is scrolling the result list
//                    @Override
//                    public boolean onTouch(View view, MotionEvent motionEvent) {
//                        closeKeyboard(view);
//                        return false;
//                    }
//                });
//                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                        closeKeyboard(view);
//                        User u = (User) listView.getItemAtPosition(i);
//                        //in case if we would like to do sth with chosen user
//                        String name = u.getUserName();
//                        //Toast.makeText(getContext(),name, Toast.LENGTH_SHORT).show();
//                        friendsViewModel.sendMsgWithBodyAdd("user/addFriend?auth_token=" + sp.getString("token", ""), name);
//                        searchView.setQuery("", false);
//                        searchView.clearFocus();
//                        listView.setVisibility(View.INVISIBLE);
//                        binding.FriendsNav.setVisibility(View.VISIBLE);
//                        binding.friendsChildFragment.setVisibility(View.VISIBLE);
//                        binding.friendsSetting.setVisibility(View.VISIBLE);
//                        binding.friendsCancel.setVisibility(View.GONE);
////                        (new Handler()).postDelayed(this::doChange, 1000);
//
//                    }

//                    public void doChange(){
//                        insertNestedFragment(new Friends_friend_Fragment());
//                    }

//                });
//            }
//        });

        NavController navController = NavHostFragment.findNavController(getChildFragmentManager().findFragmentById(R.id.nav_host_friends));
        NavigationUI.setupWithNavController(binding.FriendsNav, navController);


    }

    private void insertNestedFragment(Fragment childFragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_friends, childFragment).commit();
    }

    private void closeKeyboard(View view) {
        // this will give us the view which is currently focus in this layout
        // if nothing is currently focus then this will protect the app from crash
        if (view != null) {
            // assign the system service to InputMethodManager
            InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private Boolean isNetworkAvailable(Application application) {
        ConnectivityManager connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network nw = connectivityManager.getActiveNetwork();
        if (nw == null) return false;
        NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
        return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}