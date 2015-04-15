package com.vsdeni.ejru.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vsdeni.ejru.R;

/**
 * Created by Denis on 15.04.2015.
 */
public class AppShareDialog extends DialogFragment {

    public static AppShareDialog newInstance() {
        AppShareDialog dialog = new AppShareDialog();
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.share_dialog, container, false);
        return view;
    }
}
