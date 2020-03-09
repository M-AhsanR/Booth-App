package com.schopfen.Booth.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.schopfen.Booth.Activities.LoginActivity;
import com.schopfen.Booth.Activities.ProductDetailsActivity;
import com.schopfen.Booth.Adapters.BoothProfile_Product_Adapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.Models.BoothProfile_Product_Data;
import com.schopfen.Booth.Models.ProductImagesModel;
import com.schopfen.Booth.R;
import com.schopfen.Booth.interfaces.CustomItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BoothProfile_Products_Fragment extends Fragment {

    BoothProfile_Product_Adapter adapter;
    BoothProfile_Product_Data data;
    ArrayList<BoothProfile_Product_Data> list = new ArrayList<>();
    ArrayList<ProductImagesModel> imagesList;
    RecyclerView recyclerView;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    BoothProfile_Product_Data boothProfile_product_data;
    public static final String MyPREFERENCES = "MyPrefs";

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser){
//            ProductApi();
//        }
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_boothproduct,container,false);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();
        recyclerView = view.findViewById(R.id.recyclerView);
//        ProductApi();

        return view;
    }

    private void ProductApi(){
        Map<String, String> postparams = new HashMap<String, String>();
        postparams.put("UserID", sharedpreferences.getString("UserID", " "));
        postparams.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        if(sharedpreferences.getString("Booth", "").equals("Other")){
            postparams.put("OtherUserID", getActivity().getIntent().getStringExtra("OtherUserID"));
        }

//        Log.e("postparams", postparams.toString());

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.products, getActivity(), postparams, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()){
                    Log.e("productResult", result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        if (status == 200){

                            JSONArray products = jsonObject.getJSONArray("products");

                            for (int i = 0; i < products.length(); i++){
                                JSONObject product_items = products.getJSONObject(i);
                                boothProfile_product_data = new BoothProfile_Product_Data();

                                String ProductID = product_items.getString("ProductID");
                                String title = product_items.getString("Title");
                                JSONArray product_images = product_items.getJSONArray("ProductImages");
                                imagesList = new ArrayList<>();
                                for (int j = 0; j < product_images.length(); j++){
                                    JSONObject image_items = product_images.getJSONObject(j);
                                    ProductImagesModel productImagesModel = new ProductImagesModel();

                                    String image = image_items.getString("ProductImage");
                                    String compressedImage = image_items.getString("ProductCompressedImage");

                                    productImagesModel.setImage(image);
                                    productImagesModel.setCompressedImage(compressedImage);

                                    imagesList.add(productImagesModel);
                                }
                                boothProfile_product_data.setProduct_id(ProductID);
                                boothProfile_product_data.setProduct_name(title);
                                boothProfile_product_data.setProduct_image(imagesList);

                                list.add(boothProfile_product_data);
                            }

                            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));

                            adapter = new BoothProfile_Product_Adapter(getActivity(), list, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
//                                    Toast.makeText(getActivity(), "Item "+position, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
                                    mEditor.putString("ProductID", list.get(position).getProduct_id()).commit();
                                    startActivity(intent);
                                }
                            });
                            recyclerView.setAdapter(adapter);



                        }else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            if (status == 401){
                                mEditor.clear().apply();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
