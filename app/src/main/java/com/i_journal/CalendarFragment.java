package com.i_journal;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

public class CalendarFragment extends Fragment {

    CalendarView calendar;
    TextView date_view;
    Long date;
    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendar = (CalendarView) v.findViewById(R.id.calender);
        date = calendar.getDate();

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                if(calendar.getDate() != date) {
                    date = calendar.getDate();
                    Toast.makeText(view.getContext(), "Year=" + year + " Month=" + month + " Day=" + dayOfMonth, Toast.LENGTH_LONG).show();
                    //cal.setBackgroundColor(Color.RED);
                }
            }
        });

        // Inflate the layout for this fragment
        return v;
    }


}
