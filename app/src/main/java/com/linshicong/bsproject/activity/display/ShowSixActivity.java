package com.linshicong.bsproject.activity.display;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.linshicong.bsproject.R;
import com.linshicong.bsproject.activity.IndexActivity;
import com.linshicong.bsproject.activity.SlideBackActivity;
import com.linshicong.bsproject.bean.Post;
import com.linshicong.bsproject.bean.User;
import com.linshicong.bsproject.util.BitmapUtil;
import com.linshicong.bsproject.util.BmobUtil;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;

import static com.linshicong.bsproject.util.Constant.isUpdate;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.tekephoto.display
 * @Description: 模板六逻辑处理
 * @date 2016/9/28 13:02
 */
public class ShowSixActivity extends SlideBackActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.three_text)
    TextView threeText;
    @Bind(R.id.two_text)
    TextView twoText;
    @Bind(R.id.one_text)
    TextView oneText;
    @Bind(R.id.month_text)
    TextView monthText;
    @Bind(R.id.year_text)
    TextView yearText;
    @Bind(R.id.name_text)
    TextView nameText;
    @Bind(R.id.photo_img)
    ImageView photoImg;
    @Bind(R.id.card_view)
    CardView cardView;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_six_activity);
        ButterKnife.bind(this);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        toolbar.setNavigationIcon(R.mipmap.ic_black_ui_back_arrow);
        Intent intent = getIntent();
        toolbar.setTitle("預覽卡片");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        AssetManager mgr = getAssets();
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/HY.ttf");
        oneText.setTypeface(tf);
        twoText.setTypeface(tf);
        threeText.setTypeface(tf);
        oneText.setText(intent.getStringExtra("ed1"));
        twoText.setText(intent.getStringExtra("ed2"));
        threeText.setText(intent.getStringExtra("ed3"));
        path = intent.getStringExtra("path");
        monthText.setText(intent.getStringExtra("month"));
        yearText.setText(intent.getStringExtra("year"));
        nameText.setText(BmobUtil.GetCurrentUser().getName());
        photoImg.setImageBitmap(BitmapFactory.decodeFile(path));
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_send:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        item.setEnabled(false);
                        Bitmap bitmap = createViewBitmap(cardView);
                        BitmapUtil.saveImage(bitmap, "detail.png");
                        final ProgressDialog progress = new ProgressDialog(ShowSixActivity.this);
                        progress.setMessage("正在上傳...");
                        progress.setCanceledOnTouchOutside(false);
                        progress.show();
                        final String card = "sdcard/Feilin/detail.png";
                        final String[] filepaths = new String[2];
                        filepaths[0] = path;
                        filepaths[1] = card;
                        BmobFile.uploadBatch(filepaths, new UploadBatchListener() {
                            @Override
                            public void onSuccess(List<BmobFile> list, List<String> list1) {
                                if (list1.size() == filepaths.length) {
                                    User user = BmobUtil.GetCurrentUser();
                                    Post post = new Post();
                                    post.setToname(BmobUtil.change(nameText.getText().toString()));
                                    post.setUser(BmobUtil.GetCurrentUser());
                                    post.setContentOne(BmobUtil.change(oneText.getText().toString()));
                                    post.setContentTwo(BmobUtil.change(twoText.getText().toString()));
                                    post.setContentThree(BmobUtil.change(threeText.getText().toString()));
                                    post.setImage(list.get(0));
                                    post.setCard(list.get(1));
                                    user.setCardnum(user.getCardnum() + 1);
                                    user.update(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                //更新图片数目成功
                                                Log.i("cardnum", "cardnum");
                                            } else {
                                                Log.i("cardnum", e.toString());
                                            }
                                        }
                                    });
                                    post.save(new SaveListener<String>() {
                                        @Override
                                        public void done(String s, BmobException e) {
                                            if (e == null) {
                                                Toast.makeText(ShowSixActivity.this, "上傳成功", Toast.LENGTH_SHORT).show();
                                                progress.dismiss();
                                                File file = new File(path);
                                                if (file.exists()) {
                                                    file.delete();
                                                }
                                                if (!BmobUtil.checkAutoSave()) {
                                                    file = new File(card);
                                                    if (file.exists()) {
                                                        file.delete();
                                                    }
                                                }
                                                //通知相冊更新
                                                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                                Uri uri = Uri.parse("file:///sdcard/Feilin/");
                                                intent.setData(uri);
                                                sendBroadcast(intent);
                                                item.setEnabled(true);
                                                isUpdate = true;
                                                startActivity(new Intent(ShowSixActivity.this, IndexActivity.class));
                                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                finish();
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onProgress(int i, int i1, int i2, int i3) {

                            }

                            @Override
                            public void onError(int i, String s) {
                                Toast.makeText(ShowSixActivity.this, "上傳失敗，請檢查網絡", Toast.LENGTH_SHORT).show();
                                Log.i("display", s.toString());
                                return;
                            }
                        });
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send, menu);
        return true;
    }

    public Bitmap createViewBitmap(CardView view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
