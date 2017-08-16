package com.linshicong.bsproject.util;

import android.os.CountDownTimer;
import android.widget.Button;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.Util
 * @Description: 短信验证计时器
 * @date 2016/9/24 14:37
 */
public class MyCountTimer extends CountDownTimer {
    private Button view;

    public MyCountTimer(long millisInFuture, long countDownInterval, Button view) {
        super(millisInFuture, countDownInterval);
        this.view = view;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        view.setText((millisUntilFinished / 1000) + "秒后重发");
    }

    @Override
    public void onFinish() {
        view.setText("重新發送驗證碼");
    }

}
