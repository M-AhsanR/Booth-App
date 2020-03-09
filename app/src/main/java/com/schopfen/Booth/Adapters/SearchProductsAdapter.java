package com.schopfen.Booth.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.schopfen.Booth.Activities.ProductDetailsActivity;
import com.schopfen.Booth.Activities.ShoppingCartActivity;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.SearchProductsArrayData;
import com.schopfen.Booth.Models.ProductsData;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchProductsAdapter extends RecyclerView.Adapter<SearchProductsAdapter.MyViewHolder> {

    Context context;
    ArrayList<ProductsData> arrayList;
    private ItemClick itemClick;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    String IsInWishlist;

    public SearchProductsAdapter(Context context, ArrayList<ProductsData> arrayList,ItemClick itemClick) {
        this.context = context;
        this.arrayList = arrayList;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_products_adapter_items, parent, false);

        final SearchProductsAdapter.MyViewHolder viewHolder = new SearchProductsAdapter.MyViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick.onItemClick(view, viewHolder.getPosition());
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        Picasso.get().load(Constants.URL.IMG_URL + arrayList.get(position).getProductImagesData().get(0).getProductCompressedImage()).into(holder.product_image);
        holder.product_title.setText(arrayList.get(position).getTitle());
        holder.product_brand_name.setText("@"+arrayList.get(position).getBoothUserName());
        holder.product_price.setText(arrayList.get(position).getProductPrice());

        holder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                mEditor.putString("ProductID", arrayList.get(position).getProductID()).apply();
                context.startActivity(intent);
            }
        });

        holder.add_to_wishlist.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                AddtoWishList(position, holder);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                RemoveFromWishList(position, holder);
            }
        });

        holder.add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddToCartApi(position);
            }
        });

    }

    private void AddtoWishList(int position, MyViewHolder holder) {
        Map<String, String> body = new HashMap<String, String>();
        body.put("Type", "add");
        body.put("ProductID", arrayList.get(position).getProductID());
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.addToWishlist, context, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {

                if (ERROR.isEmpty()) {
                    Log.e("AddWishListResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        if (status == 200) {
                            IsInWishlist = "1";
                            holder.add_to_wishlist.setLiked(Boolean.TRUE);
                        } else {
                            IsInWishlist = "0";
                            holder.add_to_wishlist.setLiked(Boolean.FALSE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(context, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void RemoveFromWishList(int position, MyViewHolder holder) {
        Map<String, String> body = new HashMap<String, String>();
        body.put("Type", "remove");
        body.put("ProductID", arrayList.get(position).getProductID());
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.addToWishlist, context, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {

                if (ERROR.isEmpty()) {
                    Log.e("RemoveWishListResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        if (status == 200) {
                            IsInWishlist = "0";
                            holder.add_to_wishlist.setLiked(Boolean.FALSE);
                        } else {
                            IsInWishlist = "1";
                            holder.add_to_wishlist.setLiked(Boolean.TRUE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(context, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void AddToCartApi(int position) {

        CustomLoader.showDialog((Activity) context);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("BoothID", arrayList.get(position).getUserID());
        body.put("ProductID", arrayList.get(position).getProductID());
        body.put("ProductQuantity", "1");
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        Log.e("AddtoCartBody", body.toString());

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.AddToCart, context, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("AddToCartResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            String message = jsonObject.getString("message");
//                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();

                            CustomLoader.dialog.dismiss();

                            final Dialog alertdialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                            alertdialog.setContentView(R.layout.continue_or_checkout_dialog);
                            LinearLayout continueBtn = alertdialog.findViewById(R.id.continue_shop);
                            LinearLayout proceedBtn = alertdialog.findViewById(R.id.proceed_checkout);

                            alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            alertdialog.setCancelable(false);
                            alertdialog.show();

                            continueBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alertdialog.dismiss();
                                }
                            });

                            proceedBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    context.startActivity(new Intent(context, ShoppingCartActivity.class));
                                    alertdialog.dismiss();
                                }
                            });

                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            CustomLoader.dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CustomLoader.dialog.dismiss();
                    }
                } else {
                    Toast.makeText(context, ERROR, Toast.LENGTH_SHORT).show();
                    CustomLoader.dialog.dismiss();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView product_image, add_to_cart;
        LikeButton add_to_wishlist;
        TextView product_title, product_brand_name, product_price;
        LinearLayout main_layout;

        MyViewHolder(View itemView) {
            super(itemView);

            product_image = itemView.findViewById(R.id.product_image);
            product_title = itemView.findViewById(R.id.product_title);
            product_brand_name = itemView.findViewById(R.id.product_brand_name);
            product_price = itemView.findViewById(R.id.product_price);
            main_layout = itemView.findViewById(R.id.main_layout);
            add_to_cart = itemView.findViewById(R.id.add_to_cart);
            add_to_wishlist = itemView.findViewById(R.id.wishListBtn);
        }
    }
    public interface ItemClick {
        void onItemClick(View v, int position);
    }
}
