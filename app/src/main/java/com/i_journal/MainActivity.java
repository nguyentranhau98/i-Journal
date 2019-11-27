package com.i_journal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.annotations.PublicApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FirebaseHelperListener {


    private DatabaseReference mDatabase;
    FirebaseUser currentFirebaseUser;
    FirebaseHelper firebaseHelper;

    ListView lv_post;
    static ArrayList<Post> alPost = new ArrayList<>();
    static PostAdapter adapter;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private EntryFragment entryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        entryFragment = new EntryFragment();
        loadFragment(entryFragment);
        BottomNavigationView navigation = findViewById(R.id.nav_bar);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

//        setupItemEvent();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("FRAG", "onCreate: "+ entryFragment.getLv_post());
        lv_post = entryFragment.getLv_post();
        setupFirebase();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment fragment;
            switch (menuItem.getItemId()) {
                case R.id.entry:
                    fragment = new EntryFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.stats:
                    fragment = new StatsFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.calendar:
                    fragment = new CalendarFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.logout:
                    signOut();
                    return true;
            }
            return false;
        }
    };

    private void setupFirebase() {
        FirebaseApp.initializeApp(this);
        //mDatabase = FirebaseDatabase.getInstance().getReference();
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

    public void setupItemEvent(){
        lv_post.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Post post = (Post)alPost.get(i);
//                Intent intent = new Intent(WallActivity.this, HomeActivity.class);
//                intent.putExtra("KEY",post.getKey());
//                startActivity(intent);
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();
        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
            new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
    }

    @Override
    public void onPostsChange(List<Post> alPost) {
        adapter = new PostAdapter(getBaseContext(), R.layout.post_list_item, alPost);
        Log.d("LIST", "fetchData: " +lv_post);
        lv_post.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        System.out.println("SIZEEEEE 1 " + alPost.size());
    }
}

interface FirebaseHelperListener {
    void onPostsChange(List<Post> alPost);

}
