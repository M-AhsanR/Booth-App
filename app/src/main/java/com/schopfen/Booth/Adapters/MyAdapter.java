package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.schopfen.Booth.R;
import com.schopfen.Booth.Social.MainCatData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<MainCatData> _categoriesListParentModels;
    //private List<CategoriesListChildModel> _categoriesListChildModels;
    //  private List<String> _listDataHeader; // header titles
    private CRLCallbacks callbacks;
    ArrayList<MainCatData> _arraylistadapterparent;
   // HashMap<String, ArrayList<CategoriesListChildModel>> _arraylistadapterchild;
    private HashMap<String, List<String>> _listDataChild;

    public MyAdapter(Context context, ArrayList<MainCatData> arraylistadapterparent,
                     HashMap<String, List<String>> arraylistadapterchild, CRLCallbacks callbacks) {
        this._context = context;
        this._categoriesListParentModels = arraylistadapterparent;
        this._listDataChild = arraylistadapterchild;
        this.callbacks = callbacks;
       // this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        Log.d("myposition", String.valueOf(childPosititon));
        return this._listDataChild.get(this._arraylistadapterparent.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.catogeries_list_item, null);
        }

        TextView txtListChild = convertView
                .findViewById(R.id.tv_childtext_cla);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._arraylistadapterparent.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._arraylistadapterparent.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._arraylistadapterparent.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.catogaries_list_group, null);
        }

        TextView lblListHeader = convertView
                .findViewById(R.id.tv_parenttext_cla);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public interface CRLCallbacks {
        void onItemClick(int position);
    }

}

