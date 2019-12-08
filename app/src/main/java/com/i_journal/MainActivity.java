package com.i_journal;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener,FirebaseHelperListener {
    static final int ADD_POST_REQUEST = 1;

    ListView lv_post;
    static List<Post> alPost = new ArrayList<>();

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private EntryFragment entryFragment;
    private DatabaseReference mDatabase;
    FirebaseUser currentFirebaseUser;
    FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.nav_bar);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), AddPostActivity.class);
                startActivityForResult(intent, ADD_POST_REQUEST);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupFirebase();
        entryFragment = new EntryFragment(firebaseHelper, currentFirebaseUser);
        loadFragment(entryFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == ADD_POST_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = intent.getExtras();
                Post newPost = (Post) bundle.getSerializable("NEWPOST");
                firebaseHelper.writePost(currentFirebaseUser.getUid(), newPost);
            }
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment fragment;
            switch (menuItem.getItemId()) {
                case R.id.entry:
                    loadFragment(entryFragment);
                    return true;
                case R.id.stats:
                    fragment = new StatsFragment(currentFirebaseUser,firebaseHelper);
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

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void signOut() {
        mAuth.signOut();
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
    public void onClick(View view) {

    }

    private void setupFirebase() {
        FirebaseApp.initializeApp(getBaseContext());
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
    public void onPostsChange(List<Post> alPost) {

    }
}

interface FirebaseHelperListener {
    void onPostsChange(List<Post> alPost);
}