package com.vsdeni.ejru.listeners;

import com.facebook.share.model.ShareLinkContent;

/**
 * Created by Denis on 16.04.2015.
 */
public interface ShareListener {
    void onFacebookShare(ShareLinkContent content);
}