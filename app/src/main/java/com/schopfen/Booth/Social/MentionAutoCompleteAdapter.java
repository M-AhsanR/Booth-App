package com.schopfen.Booth.Social;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.schopfen.Booth.R;

import java.util.List;

/**
 * Created by ameen on 9/1/18.
 * Happy Coding
 */

public class MentionAutoCompleteAdapter extends ArrayAdapter<MentionPerson> {
    List<MentionPerson> objects;

    public MentionAutoCompleteAdapter(@NonNull Context context, @NonNull List<MentionPerson> objects) {
        super(context, 0, objects);

        this.objects = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public MentionPerson getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(@Nullable MentionPerson item) {
        return super.getPosition(item);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        ViewHolder viewHolder;
        if (view == null || !(view.getTag() instanceof ViewHolder)) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_mention_people, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.textView.setText(objects.get(position).getFullName());



        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        ViewHolder viewHolder;
        if (view == null || !(view.getTag() instanceof ViewHolder)) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_mention_people, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.textView.setText(objects.get(position).getFullName());


        return view;
    }

    static class ViewHolder {

        TextView textView;

        ViewHolder(View rootView) {
            initView(rootView);
        }

        private void initView(View rootView) {
            textView = rootView.findViewById(R.id.textView);
        }
    }
}