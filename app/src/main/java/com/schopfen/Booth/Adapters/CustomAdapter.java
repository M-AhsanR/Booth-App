package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.schopfen.Booth.R;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    Context context;
    int flags[];
    ArrayList<String> countryNames;
    LayoutInflater inflter;

    public CustomAdapter(Context applicationContext, ArrayList<String> countryNames) {
        this.context = applicationContext;
        this.countryNames = countryNames;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return countryNames.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
//        view = inflter.inflate(R.layout.main_cat_item, null);
        TextView names = (TextView) view.findViewById(R.id.textView);
        names.setText(countryNames.get(i));
        return view;
    }
}