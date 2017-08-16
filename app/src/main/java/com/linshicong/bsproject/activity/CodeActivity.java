package com.linshicong.bsproject.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.linshicong.bsproject.R;
import com.linshicong.bsproject.util.BmobUtil;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.index.activity
 * @Description: 我的二维码界面
 * @date 2016/10/31 15:41
 */
public class CodeActivity extends SlideBackActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.code_img)
    ImageView codeImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.code_activity);
        ButterKnife.bind(this);
        toolbar.setNavigationIcon(R.mipmap.ic_black_ui_back_arrow);
        toolbar.setTitle("我的二維碼");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        String result = BmobUtil.GetCurrentUser().getObjectId();
        /**根据用户的id生成二维码并显示到界面上*/
        Bitmap bitmap = EncodingUtils.createQRCode(result, 300, 300, null);
        codeImg.setImageBitmap(bitmap);
    }
}
