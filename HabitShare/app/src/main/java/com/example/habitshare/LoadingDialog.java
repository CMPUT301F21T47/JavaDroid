package com.example.habitshare;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;

public class LoadingDialog {
    private AlertDialog alertdialog;
    private Dialog dialog;

    LoadingDialog(Context context){
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.loading_animation);
    }

//    LoadingDialog(Activity activity){
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        LayoutInflater inflater = activity.getLayoutInflater();
//
//        builder.setView(inflater.inflate(R.layout.loading_animation,null));
//        builder.setCancelable(true);
//
//        dialog = builder.create();
//    }

    public void startLoadingDialog(){
        dialog.show();
    }

    public void dismissLoadingDialog(){
        dialog.dismiss();
    }
}
