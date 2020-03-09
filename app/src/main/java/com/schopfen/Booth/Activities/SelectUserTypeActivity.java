package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.R;

import in.codeshuffle.typewriterview.TypeWriterView;

public class SelectUserTypeActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView reg_buyer,reg_booth;
    Button next;
    Animation fadeIn;
    TextView tv_user,tv_booth,instruction_title,instructions_body;
    String buyer_title,buyer_body,booth_title,booth_body;
    LinearLayout as_buyer,as_booth,main_linear;
    ImageView buyer_outer,buyer_inner,booth_outer,booth_inner;
    int id;
    TextView instruction_body;

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
            setContentView(R.layout.activity_select_user_type);

            buyer_title = getResources().getString(R.string.buyer);
            booth_title = getResources().getString(R.string.booth);
            buyer_body = getResources().getString(R.string.registerbuyer);
            booth_body = getResources().getString(R.string.registeruser);

            initilizeViews();
            regUserFunction();


            fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
            fadeIn.setDuration(1000);
        }else {
            setContentView(R.layout.no_internet_screen);
        }

    }

    // initilized widgets
    private void initilizeViews() {
        //initilization
        next = findViewById(R.id.next);
        as_buyer = findViewById(R.id.as_buyer);
        as_booth = findViewById(R.id.as_booth);
        reg_buyer = findViewById(R.id.reg_user_image);
        reg_booth = findViewById(R.id.reg_booth_image);
        main_linear = findViewById(R.id.main_linear);
        buyer_outer = findViewById(R.id.buyer_outer_circlr);
        buyer_inner = findViewById(R.id.buyer_inner_circlr);
        booth_outer = findViewById(R.id.booth_outer_circlr);
        booth_inner = findViewById(R.id.booth_inner_circlr);
        tv_user = findViewById(R.id.tv_reg_user);
        tv_booth = findViewById(R.id.tv_reg_booth);
        instruction_title = findViewById(R.id.ins_title);
        instruction_body = findViewById(R.id.ins_body);



        //passing context
        as_booth.setOnClickListener(this);
        as_buyer.setOnClickListener(this);
        next.setOnClickListener(this);
    }


    // Click Listeners
    @Override
    public void onClick(View view) {
        if (view == as_buyer){
            int cx = (main_linear.getLeft() + main_linear.getRight()) / 2;
            int cy = main_linear.getTop();
            int finalRadius = Math.max(main_linear.getWidth(), main_linear.getHeight());

            Animator anim = ViewAnimationUtils.createCircularReveal(main_linear, cx, cy, 0, finalRadius);
            anim.setDuration(1000);
            main_linear.setBackgroundResource(R.drawable.buyer_background);
            anim.start();
            regUserFunction();
        }else if (view == as_booth){
            int cx = (main_linear.getLeft() + main_linear.getRight()) / 2;
            int cy = main_linear.getTop();
            int finalRadius = Math.max(main_linear.getWidth(), main_linear.getHeight());

            Animator anim = ViewAnimationUtils.createCircularReveal(main_linear, cx, cy, 0, finalRadius);
            anim.setDuration(1000);
            main_linear.setBackgroundResource(R.drawable.booth_background);
            anim.start();

            regBoothFunction();
        }else if (view == next){
            nextButtonFunction();
        }
    }

    public void regUserFunction(){
//        instruction_body.setDelay(0);
//        instruction_body.setWithMusic(false);
//        instruction_body.removeAnimation();
        main_linear.setBackgroundResource(R.drawable.buyer_background);
        buyer_outer.setBackgroundResource(R.drawable.round_corner_white_shape);
        buyer_inner.setBackgroundResource(R.drawable.rounded_corners_orange_btn);
        booth_inner.setBackgroundResource(R.drawable.round_corner_white_shape);
        booth_outer.setBackgroundResource(R.drawable.round_corner_white_shape);
        as_buyer.setBackgroundResource(R.drawable.rounded_corners_orange_btn);
        reg_buyer.setImageResource(R.drawable.ic_shopping_bag_white);
        reg_booth.setImageResource(R.drawable.ic_shop);
        as_booth.setBackgroundResource(R.drawable.round_corner_white_shape);
        tv_user.setTextColor(getResources().getColor(R.color.white));
        tv_booth.setTextColor(getResources().getColor(R.color.black));
        instruction_title.setText(buyer_title);
        instruction_body.setText(buyer_body);
        id = 0;
    }

    public void regBoothFunction(){
//        instruction_body.setDelay(0);
//        instruction_body.setWithMusic(false);
//        instruction_body.removeAnimation();
        main_linear.setBackgroundResource(R.drawable.booth_background);
        buyer_outer.setBackgroundResource(R.drawable.round_corner_white_btn);
        buyer_inner.setBackgroundResource(R.drawable.round_corner_white_btn);
        booth_inner.setBackgroundResource(R.drawable.rounded_corners_orange_btn);
        booth_outer.setBackgroundResource(R.drawable.round_corner_white_btn);
        as_booth.setBackgroundResource(R.drawable.rounded_corners_orange_btn);
        reg_booth.setImageResource(R.drawable.ic_shop_white);
        as_buyer.setBackgroundResource(R.drawable.round_corner_white_shape);
        reg_buyer.setImageResource(R.drawable.ic_shopping_bag);
        tv_user.setTextColor(getResources().getColor(R.color.black));
        tv_booth.setTextColor(getResources().getColor(R.color.white));
        instruction_title.setText(booth_title);
        instruction_body.setText(booth_body);
        id = 1;
    }

    public void nextButtonFunction(){
        if (id == 0){
            Intent intent = new Intent(SelectUserTypeActivity.this,RegisterBayerActivity.class);
            intent.putExtra("backgroundkey", id);
            startActivity(intent);
            finish();
        }else if (id == 1){
            Intent intent = new Intent(SelectUserTypeActivity.this,RegisterBoothActivity.class);
            intent.putExtra("backgroundkey", id);
            startActivity(intent);
            finish();
        }
    }
}
