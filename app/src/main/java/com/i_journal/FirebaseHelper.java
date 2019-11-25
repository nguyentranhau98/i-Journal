package com.i_journal;

import android.content.Context;
import android.util.Log;
import android.widget.ListView;

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

import static com.i_journal.MainActivity.adapter;

public class FirebaseHelper {
    DatabaseReference mDatabase;
    ListView lv_post;
    Context c;
    static ArrayList<Post> alPost = new ArrayList<>();
    static Post single_Post;

    public FirebaseHelper(DatabaseReference mDatabase, ListView lv_post, Context c) {
        this.mDatabase = mDatabase;
        this.lv_post = lv_post;
        this.c = c;
    }

    public FirebaseHelper(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    public ArrayList<Post> readPost(String key) {
        Log.d("Ref", "readPost: " + mDatabase.getDatabase().getReference().toString());
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

    public void readSinglePost(final String key, final OnGetDataListener listener) {
        listener.onStart();
        Query myPosts2 = mDatabase.child("post");
        myPosts2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equals(key)) {
                        single_Post = ds.getValue(Post.class);
                        single_Post.setKey(ds.getKey());

                        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        String date = sfd.format(new Date(single_Post.getTime()));
                        System.out.println("***onSuccess*** from Firebase Helper");
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

    private void fetchData(DataSnapshot dataSnapshot) {
        alPost.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            System.out.println("*********POST*******");
            Post post = ds.getValue(Post.class);
            post.setKey(ds.getKey());
            System.out.println("KEY: " + post.getKey());
            System.out.println("TITLE: " + post.getTitle());
            System.out.println("CONTENT: " + post.getContent());

            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String date = sfd.format(new Date(post.getTime()));
            System.out.println(date);
            alPost.add(post);
        }
        adapter = new PostAdapter(c, R.layout.post_list_item, alPost);
        lv_post.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        System.out.println("SIZEEEEE 1 " + alPost.size());
    }

    public String writePost(Post post, long timestamp) {
        try {
            HashMap<String, Object> message = new HashMap<>();
            message.put("title", post.getTitle());
            message.put("content", post.getContent());
            message.put("time", timestamp);
            String key = mDatabase.child("post").push().getKey();
            mDatabase.child("post").child(key).setValue(message);
            return key;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public boolean updatePost(Post post, long timestamp) {
        try {
            HashMap<String, Object> message = new HashMap<>();
            message.put("title", post.getTitle());
            message.put("content", post.getContent());
            message.put("time", timestamp);
            mDatabase.child("post").child(post.getKey()).setValue(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePost(String key) {
        try {
            mDatabase.child("post").child(key).removeValue();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public interface OnGetDataListener {
        public void onStart();

        public void onSuccess(Post post);

        public void onFailed(DatabaseError databaseError);
    }
}
