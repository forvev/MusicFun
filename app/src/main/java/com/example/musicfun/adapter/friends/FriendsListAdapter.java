package com.example.musicfun.adapter.friends;

import static android.content.Context.MODE_PRIVATE;
import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.example.musicfun.R;
import com.example.musicfun.databinding.FragmentFriendsBinding;
import com.example.musicfun.datatype.User;
import com.example.musicfun.interfaces.FriendFragmentInterface;
import com.example.musicfun.repository.Database;
import com.example.musicfun.ui.friends.FriendsViewModel;
import com.example.musicfun.ui.friends.Friends_DBAccess;

import java.util.ArrayList;
import java.util.List;

public class FriendsListAdapter extends BaseAdapter {

    private static final int VIEW_TYPE_REQUEST_SENT = 0;
    private static final int VIEW_TYPE_REQUEST_RECEIVED = 1;
    private static final int VIEW_TYPE_FRIEND = 2;

    Context mContext;
    LayoutInflater inflater;
    private List<User> userList = null;
    private ArrayList<User> arrayList;
    Friends_DBAccess dbAccess;
    SharedPreferences sp;
    FriendFragmentInterface fi;


    public FriendsListAdapter(Context context, List<User> userList, FriendFragmentInterface fi){
        mContext = context;
        this.userList = userList;
        inflater = LayoutInflater.from(mContext);
        this.arrayList = new ArrayList<>();
        this.arrayList.addAll(userList);
        dbAccess = new Friends_DBAccess(mContext);
        this.fi = fi;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int i) {
        return userList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemViewType(int position) {

        User user = (User) userList.get(position);

        if(!user.isAccepted() && user.isRequestSend()) {
            return VIEW_TYPE_REQUEST_SENT;
        }else if(!user.isAccepted() && !user.isRequestSend()){
            return VIEW_TYPE_REQUEST_RECEIVED;
        }else{
            return VIEW_TYPE_FRIEND;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        /*
        view = inflater.inflate(R.layout.friends_custom_view, null);
        TextView username = (TextView) view.findViewById(R.id.friends_custom_view_username);
        ImageView delete = (ImageView) view.findViewById(R.id.friends_custom_view_delete);

        RelativeLayout clickArea = (RelativeLayout) view.findViewById(R.id.user_and_id);

        clickArea.setOnClickListener(click -> fi.startChat(retrieveUserName(i)));
             profile.setOnClickListener(click -> getProfile(i));
        delete.setOnClickListener(click -> fi.deleteFriend(i));

        username.setText(userList.get(i).getUserName());
        */
        View v = view;
        int type = getItemViewType(i);
        if(v == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            User user = userList.get(i);
            if (type == 0){
                v = inflater.inflate(R.layout.friends_custom_request_sent, viewGroup, false);
                //v = inflater.inflate(R.layout.friends_custom_request_sent, null);
                TextView username = (TextView) v.findViewById(R.id.friend_request_sent_friends_custom_view_username);
                ImageView delete = (ImageView) v.findViewById(R.id.friend_request_sent_friends_custom_view_delete);

                username.setText(user.getUserName());
                delete.setOnClickListener(click -> fi.deleteFriend(i,retrieveUserName(i)));
            }else if(type == 1){
                v = inflater.inflate(R.layout.friends_custom_request_received, viewGroup, false);
                //v = inflater.inflate(R.layout.friends_custom_request_received, null);
                TextView username = (TextView) v.findViewById(R.id.friend_request_received_friends_custom_view_username);
                ImageView accept = (ImageView) v.findViewById(R.id.friend_request_received_accept);
                ImageView decline = (ImageView) v.findViewById(R.id.friend_request_received_decline);

                username.setText(user.getUserName());
                decline.setOnClickListener(click -> fi.deleteFriend(i,retrieveUserName(i)));
                accept.setOnClickListener(click -> fi.addFriend(user.getUserName(), i));
            }else{
                v = inflater.inflate(R.layout.friends_custom_view, viewGroup, false);
                //v = inflater.inflate(R.layout.friends_custom_view, null);
                TextView username = (TextView) v.findViewById(R.id.friends_custom_view_username);
                ImageView delete = (ImageView) v.findViewById(R.id.friends_custom_view_delete);
                RelativeLayout clickArea = (RelativeLayout) v.findViewById(R.id.user_and_id);

                username.setText(user.getUserName());
                delete.setOnClickListener(click -> fi.deleteFriend(i,retrieveUserName(i)));
                clickArea.setOnClickListener(click -> fi.startChat(retrieveUserName(i)));
            }
        }


        return v;
    }

    private String retrieveUserName(int i){
        return userList.get(i).getUserName();
    }

}