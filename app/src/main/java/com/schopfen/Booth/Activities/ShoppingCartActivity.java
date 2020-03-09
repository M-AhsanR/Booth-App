package com.schopfen.Booth.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.makeramen.roundedimageview.RoundedImageView;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.CartBoothItemsData;
import com.schopfen.Booth.Models.CartBoothsData;
import com.schopfen.Booth.Models.ProductImagesData;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCartActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    RecyclerView shoppingCartRecycler;
    LinearLayout checkoutmain_linear,data_linear,empty_linear;
    static LinearLayout checkout;

    ArrayList<ProductImagesData> productImagesData;
    ArrayList<CartBoothsData> cartBoothsData;
    ArrayList<CartBoothItemsData> cartBoothItemsData;

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
            setContentView(R.layout.activity_shopping_cart);

            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();

            GetCartItems();

            shoppingCartRecycler = findViewById(R.id.shoppingCartRecycler);
            data_linear = findViewById(R.id.data_linear);
            empty_linear = findViewById(R.id.empty_linear);
            checkout = findViewById(R.id.checkout);

            checkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ShoppingCartActivity.this, CheckOutActivity.class);
                    startActivity(i);
                    finish();
                }
            });
        }else {
            setContentView(R.layout.no_internet_screen);
        }

    }

    @Override
    public void onClick(View v) {

    }

    private void GetCartItems(){

        Map<String, String> postParams = new HashMap<>();
        HashMap<String, String> headerParams;

        CustomLoader.showDialog(ShoppingCartActivity.this);

        headerParams = new HashMap<>();
        headerParams.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.GetCartItems + sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE, ShoppingCartActivity.this, postParams, headerParams, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("GetCartItemsResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();

                            cartBoothsData = new ArrayList<>();
                            JSONArray jsonArray = jsonObject.getJSONArray("cart_items");
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                String BoothID = jsonObject1.getString("BoothID");
                                String BoothName = jsonObject1.getString("BoothName");
                                String ProductID = jsonObject1.getString("ProductID");
                                String ProductQuantity = jsonObject1.getString("ProductQuantity");
                                String TempOrderID = jsonObject1.getString("TempOrderID");
                                String UserID = jsonObject1.getString("UserID");
                                String VatPercentage = jsonObject1.getString("VatPercentage");

                                cartBoothItemsData = new ArrayList<>();
                                JSONArray CartItems = jsonObject1.getJSONArray("CartItems");
                                for(int j=0; j<CartItems.length(); j++){
                                    JSONObject jsonObject2 = CartItems.getJSONObject(j);
                                    String BoothID1 = jsonObject2.getString("BoothID");
                                    String Currency = jsonObject2.getString("Currency");
                                    String CurrencySymbol = jsonObject2.getString("CurrencySymbol");
                                    String ProductID1 = jsonObject2.getString("ProductID");
                                    String ProductPrice = jsonObject2.getString("ProductPrice");
                                    String ProductQuantity1 = jsonObject2.getString("ProductQuantity");
                                    String ProductTitle = jsonObject2.getString("ProductTitle");
                                    String TempOrderID1 = jsonObject2.getString("TempOrderID");
                                    String UserID1 = jsonObject2.getString("UserID");
                                    String DeliveryCharges = jsonObject2.getString("DeliveryCharges");

                                    productImagesData = new ArrayList<>();
                                    JSONArray ProductImages = jsonObject2.getJSONArray("ProductImages");
                                    for(int k=0; k<ProductImages.length(); k++){
                                        JSONObject jsonObject3 = ProductImages.getJSONObject(k);
                                        String ProductCompressedImage = jsonObject3.getString("ProductCompressedImage");
                                        String ProductImage = jsonObject3.getString("ProductImage");

                                        productImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                    }

                                    cartBoothItemsData.add(new CartBoothItemsData(DeliveryCharges, BoothID1, Currency, CurrencySymbol, ProductID1, ProductPrice, ProductQuantity1, ProductTitle, TempOrderID1, UserID1, productImagesData));
                                }

                                cartBoothsData.add(new CartBoothsData(VatPercentage, BoothID, BoothName, ProductID, ProductQuantity, TempOrderID, UserID, cartBoothItemsData));
                            }

                            Log.e("CartBoothSize", cartBoothsData.size() + " ");

                            if (cartBoothsData.isEmpty()){
                                checkout.setVisibility(View.GONE);
                                data_linear.setVisibility(View.GONE);
                                empty_linear.setVisibility(View.VISIBLE);
                            }

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ShoppingCartActivity.this, RecyclerView.VERTICAL, false);
                            shoppingCartRecycler.setLayoutManager(layoutManager);

                            ShoppingCartRecycAdapter shoppingCartRecycAdapter = new ShoppingCartRecycAdapter(ShoppingCartActivity.this, cartBoothsData, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
                                    Log.e("position", position + " ");

                                }
                            });

//                            shoppingCartRecycler.addItemDecoration(new DividerItemDecoration(ShoppingCartActivity.this, DividerItemDecoration.VERTICAL));
                            shoppingCartRecycler.setAdapter(shoppingCartRecycAdapter);

                            shoppingCartRecycAdapter.notifyDataSetChanged();


                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(ShoppingCartActivity.this, message, Toast.LENGTH_LONG).show();
                            CustomLoader.dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(ShoppingCartActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public static class ShoppingCartRecycAdapter extends RecyclerView.Adapter<ShoppingCartRecycAdapter.MyViewHolder> {

        Context mContext;
        public static TextView total_price_button;
        public static ArrayList<CartBoothsData> cartBoothsData;
        CustomItemClickListener listener;
        SharedPreferences sharedpreferences;
        SharedPreferences.Editor mEditor;
        public static final String MyPREFERENCES = "MyPrefs";

        ShoppingCartRecycAdapter(Context mContext, ArrayList<CartBoothsData> cartBoothsData, CustomItemClickListener listener) {
            this.mContext = mContext;
            this.cartBoothsData = cartBoothsData;
            this.listener = listener;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view;
            LayoutInflater inflater = LayoutInflater.from(mContext);

            sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();

            view = inflater.inflate(R.layout.shop_cart_recyc_item, parent, false);

            final MyViewHolder viewHolder = new MyViewHolder(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v, viewHolder.getPosition());
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

            MyViewHolder.itemsCount.setText("(" + cartBoothsData.get(position).getCartBoothItemsData().size() + ")");
            MyViewHolder.boothName.setText(cartBoothsData.get(position).getBoothName());

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
            holder.shoppingCartSubRecyc.setLayoutManager(layoutManager);

            ShopCartSubRecycAdapter shopCartSubRecycAdapter = new ShopCartSubRecycAdapter(position, mContext, cartBoothsData.get(position).getCartBoothItemsData(), new CustomItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.e("position", position + " ");

                }
            });
            holder.shoppingCartSubRecyc.setAdapter(shopCartSubRecycAdapter);
            //     shopCartSubRecycAdapter.notifyItemChanged(position);
//        shopCartSubRecycAdapter.notifyDataSetChanged();

        }

        @Override
        public int getItemCount() {
            return cartBoothsData.size();
        }

        public static class MyViewHolder extends RecyclerView.ViewHolder {

            RecyclerView shoppingCartSubRecyc;
            public static TextView boothName, itemsCount;

            public MyViewHolder(View itemView) {
                super(itemView);

                total_price_button = itemView.findViewById(R.id.total_price);
                shoppingCartSubRecyc = itemView.findViewById(R.id.shop_cart_subRcyc);
                boothName = itemView.findViewById(R.id.boothName);
                itemsCount = itemView.findViewById(R.id.itemsCount);
            }
        }

        public void refresh(){
            notifyDataSetChanged();
        }

    }

    public static class ShopCartSubRecycAdapter extends RecyclerView.Adapter<ShopCartSubRecycAdapter.MyViewHolder> {

        Context mContext;
        ArrayList<CartBoothItemsData> cartBoothItemsData;
        CustomItemClickListener listener;
        SharedPreferences sharedpreferences;
        SharedPreferences.Editor mEditor;
        ArrayList<String> quantitylist = new ArrayList<>();
        public static final String MyPREFERENCES = "MyPrefs";
        int mainposition;


        public ShopCartSubRecycAdapter(int position, Context mContext, ArrayList<CartBoothItemsData> cartBoothItemsData, CustomItemClickListener listener) {
            this.mContext = mContext;
            this.cartBoothItemsData = cartBoothItemsData;
            this.listener = listener;
            this.mainposition = position;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view;
            LayoutInflater inflater = LayoutInflater.from(mContext);

            sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();

            view = inflater.inflate(R.layout.shop_cart_subrecyc_item, parent, false);

            final MyViewHolder viewHolder = new MyViewHolder(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v, viewHolder.getPosition());
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ShopCartSubRecycAdapter.MyViewHolder holder, final int position) {

            float proprice = Float.parseFloat(cartBoothItemsData.get(position).getProductPrice()) * Integer.parseInt(cartBoothItemsData.get(position).getProductQuantity1());
            Picasso.get().load(Constants.URL.IMG_URL + cartBoothItemsData.get(position).getProductImagesData().get(0).getProductCompressedImage()).into(holder.productImg);
            holder.productTitle.setText(cartBoothItemsData.get(position).getProductTitle());
            holder.productQuantity.setText(cartBoothItemsData.get(position).getProductQuantity1());
            holder.productPrice.setText(proprice + " " + cartBoothItemsData.get(position).getCurrency());
            quantitylist.add(cartBoothItemsData.get(position).getProductQuantity1());

            float beforeprice = 0;
            for (int i = 0; i < cartBoothItemsData.size(); i++) {
                float propricefortotal = Float.parseFloat(cartBoothItemsData.get(i).getProductPrice()) * Integer.parseInt(cartBoothItemsData.get(i).getProductQuantity1());
                beforeprice = beforeprice + propricefortotal;
                Log.e("Priceis", String.valueOf(beforeprice));
            }
            Log.e("Price is", String.valueOf(beforeprice));
            ShoppingCartRecycAdapter.total_price_button.setText(mContext.getResources().getString(R.string.total)+ " " + cartBoothItemsData.get(position).getCurrency() + " " + String.valueOf(beforeprice));
            beforeprice = 0;


            holder.productQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("Clicked", "Click");
                    final Dialog pricedialog = new Dialog(mContext, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                    pricedialog.setContentView(R.layout.select_quantity_dialog);
                    EditText getprice = pricedialog.findViewById(R.id.getPrice);
                    Button cancelBtn = pricedialog.findViewById(R.id.btn_cancel);
                    Button addBtn = pricedialog.findViewById(R.id.btn_add);

                    getprice.setText(cartBoothItemsData.get(position).getProductQuantity1());
                    addBtn.setText(mContext.getResources().getString(R.string.update));

                    pricedialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    pricedialog.show();

                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            pricedialog.dismiss();
                        }
                    });

                    addBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
                            if (getprice.getText().toString().isEmpty() || Integer.parseInt(getprice.getText().toString()) < 1) {
                                getprice.startAnimation(shake);
                                Toast.makeText(mContext, mContext.getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
                            } else {
                               quantityCalculationCall(getprice, holder.productPrice, holder.productQuantity, position);                            pricedialog.dismiss();
                            }
                        }
                    });

                }
            });

            holder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final PopupMenu projMangMore = new PopupMenu(mContext, view);
                    projMangMore.getMenuInflater().inflate(R.menu.cart_menu, projMangMore.getMenu());

                    projMangMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.wishlist:

                                    final Dialog wishlistDialog = new Dialog(mContext, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                    wishlistDialog.setContentView(R.layout.delete_cart_item_dialog);
                                    Button yesBtn1 = wishlistDialog.findViewById(R.id.btn_yes);
                                    Button noBtn1 = wishlistDialog.findViewById(R.id.btn_no);
                                    TextView alert_message = wishlistDialog.findViewById(R.id.alert_message);

                                    alert_message.setText(mContext.getResources().getString(R.string.areyousureyoutwanttoaddtowishlist));

                                    wishlistDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    wishlistDialog.show();

                                    noBtn1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            wishlistDialog.dismiss();
                                        }
                                    });

                                    yesBtn1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            AddtoWishList(cartBoothItemsData.get(position).getProductID1(), position);
                                            deleteFromCartApiCall(position);
                                            wishlistDialog.dismiss();
                                        }
                                    });



                                    break;
                                case R.id.delete:
                                    final Dialog deletedialog = new Dialog(mContext, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                    deletedialog.setContentView(R.layout.delete_cart_item_dialog);
                                    Button yesBtn = deletedialog.findViewById(R.id.btn_yes);
                                    Button noBtn = deletedialog.findViewById(R.id.btn_no);

                                    deletedialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    deletedialog.show();

                                    noBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            deletedialog.dismiss();
                                        }
                                    });

                                    yesBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            deleteFromCartApiCall(position);
                                            deletedialog.dismiss();
                                        }
                                    });

                                    break;
                            }
                            return false;
                        }
                    });
                    projMangMore.show();
                }
            });

            Log.e("ProductPrice", cartBoothItemsData.get(position).getProductPrice());

        }

        private void AddtoWishList(String ProductID, int position) {
            CustomLoader.showDialog((Activity) mContext);
            Map<String, String> body = new HashMap<String, String>();
            body.put("Type", "add");
            body.put("ProductID", ProductID);
            body.put("UserID", sharedpreferences.getString("UserID", " "));
            body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

            HashMap<String, String> header = new HashMap<String, String>();
            header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

            ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.addToWishlist, mContext, body, header, new ServerCallback() {
                @Override
                public void onSuccess(String result, String ERROR) {

                    if (ERROR.isEmpty()) {
                        Log.e("AddWishListResp", result);
                        try {
                            JSONObject jsonObject = new JSONObject(String.valueOf(result));
                            int status = jsonObject.getInt("status");
                            String message = jsonObject.getString("message");
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                            if (status == 200) {
                                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                                CustomLoader.dialog.dismiss();

                            } else {
                                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                                CustomLoader.dialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            CustomLoader.dialog.dismiss();
                        }
                    } else {
                        CustomLoader.dialog.dismiss();
                        Toast.makeText(mContext, ERROR, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return cartBoothItemsData.size();
        }

        public static class MyViewHolder extends RecyclerView.ViewHolder {

            RoundedImageView productImg;
            TextView productTitle, productQuantity, productPrice;
            ImageView more;

            public MyViewHolder(View itemView) {
                super(itemView);

                productImg = itemView.findViewById(R.id.productImg);
                productTitle = itemView.findViewById(R.id.productTitle);
                productQuantity = itemView.findViewById(R.id.productQuantity);
                productPrice = itemView.findViewById(R.id.productPrice);
                more = itemView.findViewById(R.id.more);
            }
        }

        private void deleteFromCartApiCall(int pos) {

            CustomLoader.showDialog((Activity) mContext);

            quantitylist.remove(pos);
            String tempId = cartBoothItemsData.get(pos).getTempOrderID1();
            Map<String, String> body = new HashMap<String, String>();

            HashMap<String, String> header = new HashMap<String, String>();
            header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

            ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.deleteFromCart + sharedpreferences.getString("UserID", " ") + "&TempOrderID=" + tempId + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE, mContext, body, header, new ServerCallback() {
                @Override
                public void onSuccess(String result, String ERROR) {

                    if (ERROR.isEmpty()) {

                        try {
                            JSONObject jsonObject = new JSONObject(String.valueOf(result));
                            int status = jsonObject.getInt("status");
                            if (status == 200) {
                                CustomLoader.dialog.dismiss();
                                String message = jsonObject.getString("message");
//                                cartBoothItemsData.remove(pos);
//                                notifyItemRemoved(pos);
//                                notifyDataSetChanged();
//                                ShoppingCartRecycAdapter.MyViewHolder.itemsCount.setText("(" + String.valueOf(Integer.valueOf(ShoppingCartRecycAdapter.MyViewHolder.itemsCount.getText().toString().substring(1, ShoppingCartRecycAdapter.MyViewHolder.itemsCount.getText().toString().length()-1))-1) + ")");
                                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();

                                mContext.startActivity(new Intent(mContext, ShoppingCartActivity.class));
                                ((Activity) mContext).overridePendingTransition( 0, 0);
                                ((Activity) mContext).finish();
                                ((Activity) mContext).overridePendingTransition( 0, 0);


//                                if (cartBoothItemsData.isEmpty()) {
//                                    ShoppingCartRecycAdapter.total_price_button.setText("");
//                                    ShoppingCartRecycAdapter.total_price_button.setVisibility(View.GONE);
//                                    ShoppingCartRecycAdapter.MyViewHolder.boothName.setVisibility(View.GONE);
//                                    ShoppingCartRecycAdapter.MyViewHolder.itemsCount.setVisibility(View.GONE);
//                                    ShoppingCartRecycAdapter.cartBoothsData.remove(mainposition);
//                                }
//                                if (ShoppingCartRecycAdapter.cartBoothsData.isEmpty()){
//                                    checkout.setVisibility(View.GONE);
//                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(mContext, ERROR, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        private void quantityCalculationCall(EditText editText, TextView proprice, TextView proquantity, int pos) {

            CustomLoader.showDialog((Activity) mContext);

            String updatedquantity = editText.getText().toString();
            String tempId = cartBoothItemsData.get(pos).getTempOrderID1();

            quantitylist.set(pos, updatedquantity);

            Map<String, String> body = new HashMap<String, String>();
            body.put("TempOrderID", tempId);
            body.put("ProductQuantity", updatedquantity);
            body.put("UserID", sharedpreferences.getString("UserID", " "));
            body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

            HashMap<String, String> header = new HashMap<String, String>();
            header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));


            ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.updateCartQuantity, mContext, body, header, new ServerCallback() {
                @Override
                public void onSuccess(String result, String ERROR) {
                    if (ERROR.isEmpty()) {
                        Log.e("GetResp", result);
                        try {
                            JSONObject jsonObject = new JSONObject(String.valueOf(result));
                            int status = jsonObject.getInt("status");
                            if (status == 200) {
                                CustomLoader.dialog.dismiss();

                                float totalprice = Integer.parseInt(updatedquantity) * Float.parseFloat(cartBoothItemsData.get(pos).getProductPrice());
                                proquantity.setText(String.valueOf(updatedquantity));
                                proprice.setText(String.valueOf(totalprice) + " " + cartBoothItemsData.get(pos).getCurrency());

                                float price = 0;

                                for (int i = 0; i < cartBoothItemsData.size(); i++) {
                                    if (!cartBoothItemsData.get(pos).getProductTitle().equals(cartBoothItemsData.get(i).getProductTitle())){
                                        float propricefortotal = Float.parseFloat(cartBoothItemsData.get(i).getProductPrice()) * Integer.parseInt(cartBoothItemsData.get(i).getProductQuantity1());
                                        price = price + propricefortotal;
                                        Log.e("Priceis", String.valueOf(price));
                                    }
                                }

                                price = price + totalprice;

                                Log.e("Price is", String.valueOf(price));
                                ShoppingCartRecycAdapter.total_price_button.setText("Total" + " " + cartBoothItemsData.get(pos).getCurrency() + " " + String.valueOf(price));

                                String message = jsonObject.getString("message");
                                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            CustomLoader.dialog.dismiss();
                            e.printStackTrace();
                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, ERROR, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}
