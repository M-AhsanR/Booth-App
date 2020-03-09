package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.schopfen.Booth.Adapters.MainCategories_Adapter;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.DataClasses.MainCategories_Model;
import com.schopfen.Booth.R;

import java.util.ArrayList;
import java.util.List;

public class MainCategoriesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MainCategories_Adapter listAdapter;
    ArrayList<MainCategories_Model> parentlist = new ArrayList<>();
    Button confirmButton;
    NestedScrollView nestedScrollView;

    @Override
    protected void onStop() {
        super.onStop();
        if (CustomLoader.dialog != null){
            CustomLoader.dialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (CustomLoader.dialog != null){
            CustomLoader.dialog.dismiss();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isNetworkAvailable()){
            setContentView(R.layout.activity_main_categories);


            recyclerView = findViewById(R.id.rv_catlist_cla);
            confirmButton = findViewById(R.id.confirmBtn);
            nestedScrollView = findViewById(R.id.nestedsv);

            nestedScrollView.setFocusableInTouchMode(true);
            nestedScrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            List<String> listtoadditems = new ArrayList<String>();

            MainCategories_Model parentItems;
            String parent[] = {"parent one","parent two","parent three","parent four","parent five"};

            for (int i=0; i<parent.length; i++){
                parentItems = new MainCategories_Model("http://",parent[i],"6 sub items");
                parentlist.add(parentItems);
            }

            listAdapter = new MainCategories_Adapter(MainCategoriesActivity.this, parentlist, new CustomItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {

                }
            });
            recyclerView.setAdapter(listAdapter);

            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainCategoriesActivity.this, CategoriesListActivity.class);
                    startActivity(i);
                }
            });
        }else {
            setContentView(R.layout.no_internet_screen);
        }



    }
}
