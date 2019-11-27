package com.i_journal;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class UpdatePostActivity extends AppCompatActivity implements View.OnClickListener {
    EditText edt_title, edt_content;
    ImageView img_weary, img_disappointed, img_expressionless, img_slightlysmiling, img_smiling;
    Button btn_ok, btn_cancel;
    static int chosenId = 0;
    Post oldPost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        oldPost = (Post) getIntent().getSerializableExtra("UPDATEPOST");

        edt_title = findViewById(R.id.edt_title);
        edt_content = findViewById(R.id.edt_content);
        img_weary = findViewById(R.id.img_weary);
        img_disappointed = findViewById(R.id.img_disappointed);
        img_expressionless = findViewById(R.id.img_expressionless);
        img_slightlysmiling = findViewById(R.id.img_slightlysmiling);
        img_smiling = findViewById(R.id.img_smiling);
        btn_ok = findViewById(R.id.btn_ok);
        btn_cancel = findViewById(R.id.btn_cancel);

        img_weary.setOnClickListener(this);
        img_disappointed.setOnClickListener(this);
        img_expressionless.setOnClickListener(this);
        img_slightlysmiling.setOnClickListener(this);
        img_smiling.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        setSelectedData(oldPost);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_weary: {
                handleImageClick(R.id.img_weary);
                break;
            }
            case R.id.img_disappointed: {
                handleImageClick(R.id.img_disappointed);
                break;
            }
            case R.id.img_expressionless: {
                handleImageClick(R.id.img_expressionless);
                break;
            }
            case R.id.img_slightlysmiling: {
                handleImageClick(R.id.img_slightlysmiling);
                break;
            }
            case R.id.img_smiling: {
                handleImageClick(R.id.img_smiling);
                break;
            }
            case R.id.btn_ok: {
                if (chosenId == 0 || edt_title.getText().toString().trim().equals("") || edt_content.getText().toString().trim().equals("")) {
                    Toast.makeText(getBaseContext(), "Please choose the expression face, fill all the title and content", Toast.LENGTH_SHORT).show();
                } else {
                    String title = edt_title.getText().toString().trim();
                    String content = edt_content.getText().toString().trim();
                    int rating = getRating(chosenId);
                    Post newPost = new Post(oldPost.getKey(), title, content, oldPost.getTime(), rating);
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("NEWPOST", newPost);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            }
            case R.id.btn_cancel: {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
                break;
            }
        }
    }

    void handleImageClick(int id) {
        if (chosenId == 0) {
            chosenId = id;
            findViewById(id).setPadding(5, 5, 5, 5);
            findViewById(id).setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.background_circle));
            return;
        }
        if (chosenId == id) {
            chosenId = 0;
            findViewById(id).setPadding(0, 0, 0, 0);
            findViewById(id).setBackgroundResource(0);
            return;
        }
        if (chosenId != id || chosenId != 0) {
            findViewById(chosenId).setPadding(0, 0, 0, 0);
            findViewById(chosenId).setBackgroundResource(0);
            chosenId = id;
            findViewById(id).setPadding(5, 5, 5, 5);
            findViewById(id).setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.background_circle));
        }
    }

    int getRating(int id) {
        switch (id) {
            case R.id.img_weary: {
                return 1;
            }
            case R.id.img_disappointed: {
                return 2;
            }
            case R.id.img_expressionless: {
                return 3;
            }
            case R.id.img_slightlysmiling: {
                return 4;
            }
            case R.id.img_smiling: {
                return 5;
            }
        }
        return 1;
    }

    void setSelectedData(Post post) {
        edt_title.setText(post.getTitle());
        edt_content.setText(post.getContent());
        switch (post.getRating()) {
            case 1: {
                handleImageClick(R.id.img_weary);
                break;
            }
            case 2: {
                handleImageClick(R.id.img_disappointed);
                break;
            }
            case 3: {
                handleImageClick(R.id.img_expressionless);
                break;
            }
            case 4: {
                handleImageClick(R.id.img_slightlysmiling);
                break;
            }
            case 5: {
                handleImageClick(R.id.img_smiling);
                break;
            }
        }
    }
}
