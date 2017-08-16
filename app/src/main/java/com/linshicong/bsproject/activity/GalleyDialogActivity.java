package com.linshicong.bsproject.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.linshicong.bsproject.R;
import com.linshicong.bsproject.bean.PhotoTable;
import com.linshicong.bsproject.db.GalleyCache;
import com.linshicong.bsproject.util.BmobUtil;
import com.linshicong.bsproject.util.HttpUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.activity
 * @Description: 云相册窗口逻辑
 * @date 2017/2/28 8:11
 */
public class GalleyDialogActivity extends AppCompatActivity {
    @Bind(R.id.dialog_text)
    TextView dialogText;
    @Bind(R.id.galley_name_text)
    EditText galleyNameText;
    @Bind(R.id.back_btn)
    Button backBtn;
    @Bind(R.id.commit_btn)
    Button commitBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galley_dialog_activity);
        ButterKnife.bind(this);
        dialogText.setText("新建相冊");
        galleyNameText.setText("新相冊");
    }

    @OnClick({R.id.back_btn, R.id.commit_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.commit_btn:
                if (!HttpUtil.isConnected(GalleyDialogActivity.this)) {
                    Toast.makeText(this, "網絡錯誤，請檢查網絡", Toast.LENGTH_SHORT).show();
                    return;
                }
                commitBtn.setEnabled(false);
                final String tableName = galleyNameText.getText().toString();
                if (tableName.isEmpty()) {
                    commitBtn.setEnabled(true);
                    Toast.makeText(this, "相册名字不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                View v = getWindow().peekDecorView();
                if (v != null) {
                    InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                PhotoTable photoTable = new PhotoTable();
                photoTable.setUser(BmobUtil.GetCurrentUser());
                photoTable.setName(tableName);
                photoTable.setNum(0);
                photoTable.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            commitBtn.setEnabled(true);
                            GalleyCache galleyCache = new GalleyCache();
                            galleyCache.setName(tableName);
                            galleyCache.setNum(0);
                            galleyCache.setObjectId(s);
                            galleyCache.save();
                            Toast.makeText(GalleyDialogActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
                break;
        }
    }
}
