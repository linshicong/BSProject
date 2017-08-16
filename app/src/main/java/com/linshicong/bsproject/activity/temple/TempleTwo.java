package com.linshicong.bsproject.activity.temple;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.linshicong.bsproject.R;
import com.linshicong.bsproject.activity.SlideBackActivity;
import com.linshicong.bsproject.activity.display.ShowTwoActivity;
import com.linshicong.bsproject.bean.User;
import com.linshicong.bsproject.util.BitmapUtil;
import com.linshicong.bsproject.util.BmobUtil;
import com.linshicong.bsproject.util.FileUtil;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

import static com.linshicong.bsproject.util.BitmapUtil.photoURI;
import static com.linshicong.bsproject.util.BitmapUtil.showDialogCustom;
import static com.linshicong.bsproject.util.Constant.ACTION_REQUEST_EDIT_IMAGE;
import static com.linshicong.bsproject.util.Constant.SELECT_GALLERY_IMAGE_CODE;
import static com.linshicong.bsproject.util.Constant.TAKE_PHOTO_CODE;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.fragment.temple
 * @Description: 模板二逻辑处理
 * @date 2016/9/20 19:50
 */
public class TempleTwo extends SlideBackActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.photo_img)
    ImageView photoImg;
    @Bind(R.id.one_text)
    TextView oneText;
    @Bind(R.id.two_text)
    TextView twoText;
    @Bind(R.id.three_text)
    TextView threeText;
    @Bind(R.id.month_text)
    TextView monthText;
    @Bind(R.id.year_text)
    TextView yearText;
    @Bind(R.id.name_text)
    TextView nameText;
    @Bind(R.id.photo_content_text)
    EditText photoContentText;
    @Bind(R.id.commit_btn)
    ImageView commitBtn;
    private String newFilePath;
    private Bitmap bitmap;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_GALLERY_IMAGE_CODE:
                    /**从选择照片后返回，获得路径path,然后跳转到图片编辑界面*/
                    String filepath = data.getStringExtra("imgPath");
                    String path = filepath;
                    Intent intent = new Intent(TempleTwo.this, EditImageActivity.class);
                    intent.putExtra(EditImageActivity.FILE_PATH, path);
                    File outputFile = FileUtil.genEditFile();
                    intent.putExtra(EditImageActivity.EXTRA_OUTPUT,
                            outputFile.getAbsolutePath());
                    TempleTwo.this.startActivityForResult(intent,
                            ACTION_REQUEST_EDIT_IMAGE);
                    break;
                case TAKE_PHOTO_CODE:
                    /**拍照后返回，获得路径，然后跳转到图片编辑界面*/
                    path = photoURI.getPath();
                    intent = new Intent(TempleTwo.this, EditImageActivity.class);
                    intent.putExtra(EditImageActivity.FILE_PATH, path);
                    outputFile = FileUtil.genEditFile();
                    intent.putExtra(EditImageActivity.EXTRA_OUTPUT,
                            outputFile.getAbsolutePath());
                    TempleTwo.this.startActivityForResult(intent,
                            ACTION_REQUEST_EDIT_IMAGE);
                    break;
                case ACTION_REQUEST_EDIT_IMAGE:
                    /**进入图片编辑界面*/
                    newFilePath = data.getStringExtra(EditImageActivity.SAVE_FILE_PATH);
                    bitmap = BitmapFactory.decodeFile(newFilePath);
                    bitmap = BitmapUtil.decode(getResources().getDisplayMetrics().widthPixels, bitmap);
                    photoImg.setImageBitmap(bitmap);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temple_two_activity);
        ButterKnife.bind(this);
        initView();
    }

    /**
     * 初始化控件，并把当前日期设置到日期栏中
     */
    public void initView() {
        toolbar.setNavigationIcon(R.mipmap.ic_black_ui_back_arrow);
        toolbar.setTitle("編輯卡片");
        toolbar.inflateMenu(R.menu.menu_temple);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        nameText.setText(BmobUser.getCurrentUser(User.class).getName().toString());
        int m = Calendar.getInstance().get(Calendar.MONTH) + 1;
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        try {
            Date date = sdf.parse(m + "");
            sdf = new SimpleDateFormat("MMM", Locale.US);
            monthText.setText(sdf.format(date) + "." + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        yearText.setText(Calendar.getInstance().get(Calendar.YEAR) + "");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_display:
                    /**首先判断照片和文字是否为空，如果不为空则进入卡片预览界面*/
                    if (bitmap == null) {
                        Toast.makeText(TempleTwo.this, "請先添加卡片", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    String ed_1 = oneText.getText().toString();
                    String ed_2 = twoText.getText().toString();
                    String ed_3 = threeText.getText().toString();
                    if (TextUtils.isEmpty(ed_1) && TextUtils.isEmpty(ed_2) && TextUtils.isEmpty(ed_3)) {
                        Toast.makeText(TempleTwo.this, "請先編輯卡片", Toast.LENGTH_SHORT).show();
                        break;
                    } else {
                        Intent intent = new Intent(TempleTwo.this, ShowTwoActivity.class);
                        intent.putExtra("ed1", ed_1);
                        intent.putExtra("ed2", ed_2);
                        intent.putExtra("ed3", ed_3);
                        intent.putExtra("month", monthText.getText().toString());
                        intent.putExtra("year", yearText.getText().toString());
                        intent.putExtra("path", newFilePath);
                        startActivity(intent);
                    }
                    break;
            }
            return true;
        }
    };


    /**
     * 按钮点击事件处理
     */

    @OnClick({R.id.photo_img, R.id.commit_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.photo_img:
                showDialogCustom(TempleTwo.this);
                break;
            case R.id.commit_btn:
                /**确定按钮，把文字显示在卡片中*/
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        View v = getWindow().peekDecorView();
                        if (v != null) {
                            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputmanger.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                        AssetManager mgr = getAssets();
                        Typeface tf = Typeface.createFromAsset(mgr, "fonts/HY.ttf");
                        oneText.setTypeface(tf);
                        twoText.setTypeface(tf);
                        threeText.setTypeface(tf);
                        oneText.setText("");
                        twoText.setText("");
                        threeText.setText("");
                        String[] ss = photoContentText.getText().toString().split("\n");
                        oneText.setText(BmobUtil.change(ss[0]));
                        if (ss.length > 1) {
                            twoText.setText(BmobUtil.change(ss[1]));
                        }
                        if (ss.length > 2) {
                            threeText.setText(BmobUtil.change(ss[2]));
                        }
                    }
                });
                break;
        }
    }
}