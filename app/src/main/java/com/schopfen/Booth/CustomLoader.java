package com.schopfen.Booth;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class CustomLoader {
   public static Dialog dialog;
    public static void showDialog(Activity activity) {
        dialog = new Dialog(activity, android.R.style.ThemeOverlay_Material_Dialog_Alert);
        dialog.setContentView(R.layout.customloader);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        ImageView imageView = dialog.findViewById(R.id.loader);
        Animation pulse = AnimationUtils.loadAnimation(activity, R.anim.blow_animation);
        imageView.startAnimation(pulse);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 7000);
    }
}