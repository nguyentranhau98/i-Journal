package com.i_journal;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class EntryFragment extends Fragment {
    ListView lv_post;

    public ListView getLv_post() {
        return lv_post;
    }

    public void setLv_post(ListView lv_post) {
        this.lv_post = lv_post;
    }

    public EntryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry, container, false);
        setLv_post((ListView) view.findViewById(R.id.lv_post));
        Log.d("EntryLVPOST", "onCreateView: "+lv_post);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
