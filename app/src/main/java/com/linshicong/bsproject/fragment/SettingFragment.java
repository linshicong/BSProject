package com.linshicong.bsproject.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.drawee.view.SimpleDraweeView;
import com.linshicong.bsproject.R;
import com.linshicong.bsproject.activity.AgreementActivity;
import com.linshicong.bsproject.activity.ChangePasswordActivity;
import com.linshicong.bsproject.activity.CloudGalleyActivity;
import com.linshicong.bsproject.activity.CodeActivity;
import com.linshicong.bsproject.activity.FeedBackActivity;
import com.linshicong.bsproject.activity.InfoActivity;
import com.linshicong.bsproject.bean.PhotoTable;
import com.linshicong.bsproject.bean.User;
import com.linshicong.bsproject.util.BmobUtil;
import com.linshicong.bsproject.util.HttpUtil;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.filter.entity.ImageFile;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

import static android.app.Activity.RESULT_OK;
import static com.linshicong.bsproject.util.BmobUtil.Exit;
import static com.linshicong.bsproject.util.BmobUtil.clearCache;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.fragment
 * @Description: 设置界面
 * @date 2016/9/11 22:42
 */
public class SettingFragment extends Fragment {
    private static final String TAG = SettingFragment.class.getSimpleName();
    @Bind(R.id.user_img)
    SimpleDraweeView userImg;
    @Bind(R.id.username_text)
    TextView usernameText;
    @Bind(R.id.user_desc_text)
    TextView userDescText;
    @Bind(R.id.ll_layout)
    LinearLayout llLayout;
    @Bind(R.id.erweima_img)
    ImageView erweimaImg;
    @Bind(R.id.feedback_btn)
    Button feedbackBtn;
    @Bind(R.id.private_toggle_btn)
    ToggleButton privateToggleBtn;
    @Bind(R.id.auto_save_toggle_btn)
    ToggleButton autoSaveToggleBtn;
    @Bind(R.id.cloud_folder_btn)
    Button cloudFolderBtn;
    @Bind(R.id.change_password_btn)
    Button changePasswordBtn;
    @Bind(R.id.agreement_btn)
    Button agreementBtn;
    @Bind(R.id.clear_cache_btn)
    Button clearCacheBtn;
    @Bind(R.id.exit_btn)
    Button exitBtn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment, null);
        ButterKnife.bind(this, view);
        initData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        try {
            User user = BmobUtil.GetCurrentUser();
            usernameText.setText(user.getName());
            userDescText.setText(user.getDec());
            URL url = new URL(user.getImage().getFileUrl());
            Uri uri = Uri.parse(url.toURI().toString());
            userImg.setImageURI(uri);
        } catch (Exception e) {
            userImg.setImageResource(R.mipmap.icon);
            e.printStackTrace();
        }
        if (BmobUtil.checkPrivate()) {
            privateToggleBtn.setChecked(true);
        } else {
            privateToggleBtn.setChecked(false);
        }
        privateToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (!HttpUtil.isConnected(getActivity())) {
                    Toast.makeText(getActivity(), "無網絡連接，請檢查網絡狀態", Toast.LENGTH_SHORT).show();
                    return;
                }
                // TODO Auto-generated method stub
                if (isChecked) {
                    BmobUtil.savePrivate(getActivity(),true);
                    privateToggleBtn.setChecked(true);
                } else {
                    BmobUtil.savePrivate(getActivity(),false);
                    privateToggleBtn.setChecked(false);
                }
            }
        });
        if (BmobUtil.checkAutoSave()) {
            autoSaveToggleBtn.setChecked(true);
        } else {
            autoSaveToggleBtn.setChecked(false);
        }
        autoSaveToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (!HttpUtil.isConnected(getActivity())) {
                    Toast.makeText(getActivity(), "無網絡連接，請檢查網絡狀態", Toast.LENGTH_SHORT).show();
                    return;
                }
                // TODO Auto-generated method stub
                if (isChecked) {
                    autoSaveToggleBtn.setChecked(true);
                    BmobUtil.saveAutoSave(getActivity(),true);
                } else {
                    autoSaveToggleBtn.setChecked(false);
                    BmobUtil.saveAutoSave(getActivity(),false);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constant.REQUEST_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    ArrayList<ImageFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                    int num = list.size();
                    final String[] filePaths = new String[num];
                    for (int i = 0; i < num; i++) {
                        filePaths[i] = list.get(i).getPath();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final ProgressDialog progress = new ProgressDialog(getActivity());
                            progress.setMessage("正在上傳...");
                            progress.setCanceledOnTouchOutside(false);
                            progress.show();
                            BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
                                @Override
                                public void onSuccess(List<BmobFile> list, List<String> list1) {
                                    if (list1.size() == filePaths.length) {
                                        PhotoTable pt = new PhotoTable();
                                        pt.setUser(BmobUtil.GetCurrentUser());
                                        pt.setPhoto(list1);
                                        pt.save(new SaveListener<String>() {
                                            @Override
                                            public void done(String s, BmobException e) {
                                                if (e == null) {
                                                    Toast.makeText(getActivity(), "上傳成功", Toast.LENGTH_SHORT).show();
                                                    progress.dismiss();
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

                                }
                            });
                        }
                    });
                }
                break;
        }
    }

    @OnClick({R.id.ll_layout, R.id.erweima_img, R.id.feedback_btn, R.id.cloud_folder_btn, R.id.change_password_btn, R.id.agreement_btn, R.id.clear_cache_btn, R.id.exit_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_layout:
                startActivity(new Intent(getActivity(), InfoActivity.class));
                break;
            case R.id.erweima_img:
                startActivity(new Intent(getActivity(), CodeActivity.class));
                break;
            case R.id.feedback_btn:
                startActivity(new Intent(getActivity(), FeedBackActivity.class));
                break;
            case R.id.cloud_folder_btn:
                startActivity(new Intent(getActivity(), CloudGalleyActivity.class));
                break;
            case R.id.change_password_btn:
                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
                break;
            case R.id.agreement_btn:
                startActivity(new Intent(getActivity(), AgreementActivity.class));
                break;
            case R.id.clear_cache_btn:
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clearCache(getActivity());
                        Toast.makeText(getActivity(), "清除緩存成功", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.exit_btn:
                Exit(getActivity());
                break;
        }
    }
}
