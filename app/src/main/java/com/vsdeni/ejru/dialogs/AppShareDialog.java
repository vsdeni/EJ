package com.vsdeni.ejru.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.vsdeni.ejru.R;

/**
 * Created by Denis on 15.04.2015.
 */
public class AppShareDialog extends DialogFragment {
    String mTitle;
    String mBody;
    String mImage;
    String mLink;

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
}
