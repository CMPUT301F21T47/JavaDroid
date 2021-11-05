package com.example.habitshare;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class LoadingDialog {
    private AlertDialog dialog;

    LoadingDialog(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.loading_animation,null));
        builder.setCancelable(true);

        dialog = builder.create();
    }

    public void startLoadingDialog(){
        dialog.show();
    }

    public void dismissLoadingDialog(){
        dialog.dismiss();
    }
}
