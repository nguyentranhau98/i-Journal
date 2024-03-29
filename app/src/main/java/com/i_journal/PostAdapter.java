package com.i_journal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PostAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<Post> list;

    public PostAdapter(Context context, int layout, List<Post> list) {
        this.context = context;
        this.layout = layout;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getTime();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(layout, null);
        TextView tv_post_day = view.findViewById(R.id.tv_post_day);
        ImageView mood = view.findViewById(R.id.mood);
        TextView tv_post_time = view.findViewById(R.id.tv_post_time);
        TextView tv_post_title = view.findViewById(R.id.tv_post_title);
        TextView tv_post_content = view.findViewById(R.id.tv_post_content);

        final Post post = list.get(position);

        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm:ss");
        String time = sfd.format(new Date(post.getTime()));
        tv_post_time.setText(time);

        SimpleDateFormat sfd2 = new SimpleDateFormat("dd MM yyyy");
        String date1 = sfd2.format(new Date(post.getTime()));
        String date2 = sfd2.format(new Date());

        setExpression(post.getRating(), mood);
        try {
            Date pastDay = sfd2.parse(date1);
            Date currentDay = sfd2.parse(date2);
            long diff = currentDay.getTime() - pastDay.getTime();
            if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) == 1) {
                tv_post_day.setText("1 day ago");
            } else
                tv_post_day.setText(TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS) + " hours ago");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (post.getContent().length() > 20) {
            tv_post_content.setText(post.getContent().substring(0, 19));
        } else {
            tv_post_content.setText(post.getContent());
        }

        tv_post_title.setText(post.getTitle());
        return view;
    }

    void setExpression(int id, ImageView mood) {
        switch (id) {
            case 1:
                mood.setImageResource(R.drawable.weary);
                break;
            case 2:
                mood.setImageResource(R.drawable.disappointed);
                break;
            case 3:
                mood.setImageResource(R.drawable.expressionless);
                break;
            case 4:
                mood.setImageResource(R.drawable.slightlysmiling);
                break;
            case 5:
                mood.setImageResource(R.drawable.smiling);
                break;
        }

    }
}
