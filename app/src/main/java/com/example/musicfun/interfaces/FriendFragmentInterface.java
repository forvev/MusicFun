package com.example.musicfun.interfaces;

public interface FriendFragmentInterface {

    void deleteFriend(int i);
    void deleteFriend(int position, String user_id, String playlist_id);
    void addFriend(String name);
    void getProfile(int i);
    void startChat(String name);
}
