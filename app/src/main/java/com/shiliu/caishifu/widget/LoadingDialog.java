package com.shiliu.caishifu.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shiliu.caishifu.R;


public class LoadingDialog extends Dialog {

    TextView mLoadingTv;
    String mLoadingText;

    /**
     * style很关键
     */
    public LoadingDialog(Context context) {
        super(context, R.style.loading_dialog_style);
    }

    public void setMessage(String loadingText) {
        this.mLoadingText = loadingText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        mLoadingTv = findViewById(R.id.tv_loading);
        mLoadingTv.setText(mLoadingText);
        LinearLayout linearLayout = findViewById(R.id.ll_loading);
        linearLayout.getBackground().setAlpha(210);
    }

}
