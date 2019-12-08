package com.i_journal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class EntryFragment extends Fragment implements FirebaseHelperListener{
    static final int UPDATE_POST_REQUEST = 2;

    ListView lv_post;
    List<Post> alPost;
    static PostAdapter adapter;
    FirebaseUser currentFirebaseUser;
    FirebaseHelper firebaseHelper;

    public EntryFragment(FirebaseHelper firebaseHelper, FirebaseUser currentFirebaseUser) {
        this.firebaseHelper = firebaseHelper;
        this.currentFirebaseUser = currentFirebaseUser;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry, container, false);
        lv_post = view.findViewById(R.id.lv_post);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_POST_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                Post newPost = (Post) bundle.getSerializable("NEWPOST");
                firebaseHelper.updatePost(currentFirebaseUser.getUid(), newPost);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerForContextMenu(lv_post);
        setupFirebase();
        lv_post.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Post post = (Post)alPost.get(i);
                Intent intent = new Intent(getActivity(), DetailPostActivity.class);
                intent.putExtra("OBJPOST",post);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setupFirebase() {
        FirebaseApp.initializeApp(getContext());
        firebaseHelper = new FirebaseHelper(FirebaseDatabase.getInstance().getReference(), this);
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentFirebaseUser != null) {
            String email = currentFirebaseUser.getEmail();
            System.out.println("EMAIL:  " + email);
            firebaseHelper.readPost(currentFirebaseUser.getUid());
        } else {
            System.out.println("Do not get current Firebase user");
        }
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0,0,0,"Update Post");
        menu.add(0,1,1,"Delete Post");

    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem){
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)menuItem.getMenuInfo();
        final Post selectedPost=(Post) this.lv_post.getItemAtPosition(info.position);
        switch (menuItem.getItemId()) {
            case 0:
                Intent intent = new Intent(getContext(), UpdatePostActivity.class);
                intent.putExtra("UPDATEPOST", selectedPost);
                startActivityForResult(intent, UPDATE_POST_REQUEST);
                break;
            case 1:{
                new AlertDialog.Builder(getContext())
                        .setMessage("Do you want to delete this entry?")
                        .setCancelable(false)
                        .setNegativeButton("No",null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                firebaseHelper.deletePost(currentFirebaseUser.getUid(),selectedPost.getKey());
                            }
                        }).show();
                break;
            }

            default:
                break;
        }
        return true;
    }

    @Override
    public void onPostsChange(List<Post> alPost) {
        adapter = new PostAdapter(getContext(), R.layout.post_list_item, alPost);
        lv_post.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        System.out.println("SIZEEEEE 1 " + alPost.size());
        this.alPost = alPost;
    }
}
