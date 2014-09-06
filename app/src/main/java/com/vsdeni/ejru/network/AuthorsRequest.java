package com.vsdeni.ejru.network;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.vsdeni.ejru.model.Author;

/**
 * Created by Admin on 03.09.2014.
 */
public class AuthorsRequest extends RetrofitSpiceRequest<Author.List, EjApi> {

    public AuthorsRequest() {
        super(Author.List.class, EjApi.class);
    }

    @Override
    public Author.List loadDataFromNetwork() throws Exception {
        return getService().getAuthors();
    }
}
