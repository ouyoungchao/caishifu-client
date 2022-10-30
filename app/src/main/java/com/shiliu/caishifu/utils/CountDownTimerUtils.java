package com.shiliu.caishifu.utils;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.shiliu.caishifu.R;

public class CountDownTimerUtils extends CountDownTimer {

    private TextView mTextView;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public CountDownTimerUtils(TextView textView, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.mTextView = textView;;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        mTextView.setClickable(false); //设置不可点击
        mTextView.setText(millisUntilFinished / 1000 + mTextView.getResources().getString(R.string.delay_obtain_verification_code));  //设置倒计时时间
        mTextView.setTextColor(mTextView.getContext().getColor(R.color.btn_text_disable));
        mTextView.setBackgroundResource(R.drawable.btn_disable); //设置按钮为灰色，这时是不能点击的
        SpannableString spannableString = new SpannableString(mTextView.getText().toString());  //获取按钮上的文字
        ForegroundColorSpan span = new ForegroundColorSpan(Color.GRAY);
        spannableString.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//将倒计时的时间设置为灰色
        mTextView.setText(spannableString);

    }

    @Override
    public void onFinish() {
        mTextView.setText(mTextView.getResources().getString(R.string.retry_obtain_verification_code));
        mTextView.setTextColor(mTextView.getContext().getColor(R.color.btn_text_enable));
        mTextView.setClickable(true);
        mTextView.setBackgroundResource(R.drawable.btn_enable);
    }
}
