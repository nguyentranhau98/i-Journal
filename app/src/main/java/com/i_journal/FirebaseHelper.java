package com.i_journal;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class FirebaseHelper {
    DatabaseReference mDatabase;
    static ArrayList<Post> alPost = new ArrayList<>();
    static Post single_Post;
    FirebaseHelperListener valueEventListener;

    public FirebaseHelper(DatabaseReference mDatabase, FirebaseHelperListener valueEventListener) {
        this.mDatabase = mDatabase;
        this.valueEventListener = valueEventListener;
    }

    public FirebaseHelper(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    public ArrayList<Post> readPost(String key) {
        Query myPosts = mDatabase.child(key).orderByChild("time");
        myPosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return alPost;
    }

    private void fetchData(DataSnapshot dataSnapshot) {
        alPost.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            Post post = ds.getValue(Post.class);
            post.setKey(ds.getKey());
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String date = sfd.format(new Date(post.getTime()));
            alPost.add(post);
        }
        valueEventListener.onPostsChange(alPost);
    }

    public void readSinglePost(String uid, final String key, final OnGetDataListener listener) {
        listener.onStart();
        Query myPosts2 = mDatabase.child(uid);
        myPosts2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equals(key)) {
                        single_Post = ds.getValue(Post.class);
                        single_Post.setKey(ds.getKey());
                        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        String date = sfd.format(new Date(single_Post.getTime()));
                        listener.onSuccess(single_Post);
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }



    public String writePost(String uid, Post post) {
        try {
            HashMap<String, Object> message = new HashMap<>();
            message.put("title", post.getTitle());
            message.put("content", post.getContent());
            message.put("time", post.getTime());
            message.put("rating",post.getRating());
            String key = mDatabase.child(uid).push().getKey();
            mDatabase.child(uid).child(key).setValue(message);
            return key;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public boolean updatePost(String uid, Post post) {
        try {
            HashMap<String, Object> message = new HashMap<>();
            message.put("title", post.getTitle());
            message.put("content", post.getContent());
            message.put("time", post.getTime());
            message.put("rating",post.getRating());
            mDatabase.child(uid).child(post.getKey()).setValue(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePost(String uid, String key) {
        try {
            mDatabase.child(uid).child(key).removeValue();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Post> getStatList(String key) {
        Query myPosts = mDatabase.child(key).orderByChild("time").limitToLast(10);
        myPosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return alPost;
    }

    public interface OnGetDataListener {
        public void onStart();

        public void onSuccess(Post post);

        public void onFailed(DatabaseError databaseError);
    }
}
