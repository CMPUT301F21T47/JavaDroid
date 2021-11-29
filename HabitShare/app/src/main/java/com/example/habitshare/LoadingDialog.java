package com.example.habitshare;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;

/**
 * This class creates a loading animation dialog to smoothen the user's experience
 */
public class LoadingDialog {
    private Dialog dialog;

    LoadingDialog(Context context){
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.loading_animation);
    }

    /**
     * self-explanatory
     */
    public void startLoadingDialog(){
        dialog.show();
    }

    /**
     * self-explanatory
     */
    public void dismissLoadingDialog(){
        dialog.dismiss();
    }
}
