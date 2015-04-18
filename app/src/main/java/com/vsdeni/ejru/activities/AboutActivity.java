package com.vsdeni.ejru.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vsdeni.ejru.App;
import com.vsdeni.ejru.R;
import com.vsdeni.ejru.helpers.UrlGenerator;
import com.vsdeni.ejru.helpers.Utils;
import com.vsdeni.ejru.model.Author;

import java.util.ArrayList;

/**
 * Created by Denis on 15.04.2015.
 */
public class AboutActivity extends BaseActivity {
    private static final ArrayList<Author> MAGAZINE_EDITORS;
    private static final ArrayList<Author> WEB_EDITORS;

    Toolbar mToolbar;

    static {
        MAGAZINE_EDITORS = new ArrayList<>(3);
        MAGAZINE_EDITORS.add(new Author(3, App.getContext().getString(R.string.editor_1), App.getContext().getString(R.string.chief_editor)));
        MAGAZINE_EDITORS.add(new Author(2, App.getContext().getString(R.string.editor_2), App.getContext().getString(R.string.chief_editor)));
        MAGAZINE_EDITORS.add(new Author(249, App.getContext().getString(R.string.editor_3), App.getContext().getString(R.string.general_director)));

        WEB_EDITORS = new ArrayList<>(3);
        WEB_EDITORS.add(new Author(-1, App.getContext().getString(R.string.web_editor_1), ""));
        WEB_EDITORS.add(new Author(-1, App.getContext().getString(R.string.web_editor_2), ""));
        WEB_EDITORS.add(new Author(23, App.getContext().getString(R.string.web_editor_3), ""));
    }

    private TextView getTextView(Author author) {
        String value;
        if (author.getId() > 0) {
            value = String.format("<html><a href=\"%s\">%s</a>%s</html>",
                    UrlGenerator.forAuthor(this, author.getId()),
                    author.getName(),
                    !TextUtils.isEmpty(author.getAdditionInfo()) ? " - " + author.getAdditionInfo() : "");
        } else {
            value = author.getName() + (!TextUtils.isEmpty(author.getAdditionInfo()) ? " - " + author.getAdditionInfo() : "");
        }

        TextView textView = new TextView(this);
        textView.setText(value);
        textView.setTextSize(16);
        textView.setText(Html.fromHtml(value));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setPadding(0, 0, 0, Utils.convertDpToPixel(10, this));

        return textView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayout editorsContainer = (LinearLayout) findViewById(R.id.editors_container);
        for (Author author : MAGAZINE_EDITORS) {
            editorsContainer.addView(getTextView(author));
        }

        LinearLayout webEditorsContainer = (LinearLayout) findViewById(R.id.web_editors_container);
        for (Author author : WEB_EDITORS) {
            webEditorsContainer.addView(getTextView(author));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
