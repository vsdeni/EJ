package com.vsdeni.ejru.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import com.facebook.share.model.ShareLinkContent;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vsdeni.ejru.App;
import com.vsdeni.ejru.R;
import com.vsdeni.ejru.listeners.ShareListener;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Denis on 15.04.2015.
 */
public class AppShareDialog extends DialogFragment implements View.OnClickListener {
    String mTitle;
    String mBody;
    String mImage;
    String mLink;

    ShareListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (ShareListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mTitle = args.getString("title");
            mBody = args.getString("body");
            mImage = args.getString("image");
            mLink = args.getString("link");
        }
    }

    public static AppShareDialog newInstance(String title, String body, String image, String link) {
        AppShareDialog dialog = new AppShareDialog();
        Bundle args = new Bundle(4);
        args.putString("title", title);
        args.putString("body", body);
        args.putString("image", image);
        args.putString("link", link);
        dialog.setArguments(args);
        return dialog;
    }

    private void shareMore() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_title, mTitle));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, mLink);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.action_share)));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.share_dialog, null);
        view.findViewById(R.id.share_fb).setOnClickListener(this);
        view.findViewById(R.id.share_vk).setOnClickListener(this);
        view.findViewById(R.id.share_tw).setOnClickListener(this);
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.action_share)
                .setPositiveButton(getActivity().getString(R.string.share_more), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shareMore();
                    }
                })
                .setNegativeButton(getActivity().getString(R.string.share_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        return dialog;
    }

    private void shareFb() {
        ShareLinkContent.Builder linkContent = new ShareLinkContent.Builder()
                .setContentTitle(mTitle)
                .setContentDescription(Html.fromHtml(mBody).toString())
                .setContentUrl(Uri.parse(mLink));

        if (!TextUtils.isEmpty(mImage)) {
            linkContent.setImageUrl(Uri.parse(mImage));
        }

        mListener.onFacebookShare(linkContent.build());
    }

    private void shareVk() {
        VKAccessToken token = VKSdk.getAccessToken();
        VKParameters content;
        content = VKParameters.from(
                VKApiConst.ATTACHMENTS,
                mLink);
        if (token != null) {
            content.put(VKApiConst.USER_ID, token.userId);
        }
        mListener.onVkShare(content);
    }

    private void shareTwitter() {
        TweetComposer.Builder builder = new TweetComposer.Builder(getActivity())
                .text(mTitle);
        try {
            builder.url(new URL(mLink));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        builder.show();
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.share_fb:
                shareFb();
                break;
            case R.id.share_vk:
                shareVk();
                break;
            case R.id.share_tw:
                shareTwitter();
                break;
        }
    }
}
