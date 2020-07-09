package com.hyphenate.chatuidemo.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.cache.UserCacheInfo;
import com.hyphenate.chatuidemo.cache.UserCacheManager;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MyFragment  extends Fragment implements View.OnClickListener {

    private ImageView headAvatar;
    private TextView tvNickName;
    private TextView tvUsername;
	private Button logoutBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.em_fragment_conversation_my, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        headAvatar = (ImageView) getView().findViewById(R.id.user_head_avatar);
        tvUsername = (TextView) getView().findViewById(R.id.user_username);
        tvNickName = (TextView) getView().findViewById(R.id.user_nickname);
        logoutBtn= (Button) getView().findViewById(R.id.btn_logout);
        logoutBtn.setOnClickListener(this);

        String currentUserName=EMClient.getInstance().getCurrentUser();
        tvUsername.setText(currentUserName);

        UserCacheInfo  userCacheInfo= UserCacheManager.get(currentUserName);
        if(userCacheInfo!=null){
            tvNickName.setText(userCacheInfo.getNickName());

            Picasso.with(this.getContext()).load(userCacheInfo.getAvatar())
                    .placeholder(com.hyphenate.easeui.R.drawable.fanju_default_image).fit().centerInside()
                    .into(headAvatar, new Callback() {
                        @Override
                        public void onSuccess() {
                            headAvatar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            headAvatar.setBackgroundResource(com.hyphenate.easeui.R.drawable.fanju_default_image);
                        }
                    });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logout:
                logout();
                break;
        }
    }

    void logout() {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        String st = getResources().getString(R.string.Are_logged_out);
        pd.setMessage(st);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        DemoHelper.getInstance().logout(true,new EMCallBack() {

            @Override
            public void onSuccess() {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        // show login screen
                        ((MainActivity) getActivity()).finish();
                        startActivity(new Intent(getActivity(), LoginActivity.class));

                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        pd.dismiss();
                        Toast.makeText(getActivity(), "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
