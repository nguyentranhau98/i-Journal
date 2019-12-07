package com.i_journal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailPostActivity extends AppCompatActivity {
    Post objPost;
    ImageView img_view;
    TextView tv_title, tv_time, tv_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_post);
        objPost = (Post) getIntent().getSerializableExtra("OBJPOST");

        img_view = findViewById(R.id.img_mood);
        tv_title = findViewById(R.id.tv_title);
        tv_time = findViewById(R.id.tv_time);
        tv_content = findViewById(R.id.tv_content);


        switch (objPost.getRating()) {
            case 1: {
                img_view.setImageDrawable(getResources().getDrawable(R.drawable.weary));
                break;
            }
            case 2: {
                img_view.setImageDrawable(getResources().getDrawable(R.drawable.disappointed));
                break;
            }
            case 3: {
                img_view.setImageDrawable(getResources().getDrawable(R.drawable.expressionless));
                break;
            }
            case 4: {
                img_view.setImageDrawable(getResources().getDrawable(R.drawable.slightlysmiling));
                break;
            }
            case 5: {
                img_view.setImageDrawable(getResources().getDrawable(R.drawable.smiling));
                break;
            }
        }
        tv_title.setText(objPost.getTitle());
        tv_content.setText(objPost.getContent());
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String date = sfd.format(new Date(objPost.getTime()));
        tv_time.setText(date);
    }
}
