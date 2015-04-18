package com.vsdeni.ejru.helpers;

import android.widget.Toast;

import com.vk.sdk.api.VKParameters;
import com.vsdeni.ejru.App;
import com.vsdeni.ejru.R;

/**
 * Created by Denis on 18.04.2015.
 */
public class ShareHelper {
    public static VKParameters unpublishedContent;

    public static void notifySharingSuccess() {
        Toast.makeText(App.getContext(), App.getContext().getString(R.string.success_sharing_message), Toast.LENGTH_SHORT).show();
    }
}
