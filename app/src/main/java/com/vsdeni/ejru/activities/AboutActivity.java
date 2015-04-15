package com.vsdeni.ejru.activities;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        LinearLayout editorsContainer = (LinearLayout) findViewById(R.id.editors_container);
        for (Author author : MAGAZINE_EDITORS) {
            String value = "<html><a href=\"" + UrlGenerator.forAuthor(this, author.getId()) + "\">" + author.getName() + "</a>" +
                    "" + (!TextUtils.isEmpty(author.getAdditionInfo()) ? " - " + author.getAdditionInfo() : "") +
                    "</html>";
            TextView textView = new TextView(this);
            textView.setText(value);
            textView.setTextSize(16);
            textView.setText(Html.fromHtml(value));
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setPadding(0, 0, 0, Utils.convertDpToPixel(10, this));
            editorsContainer.addView(textView);
        }

        LinearLayout webEditorsContainer = (LinearLayout) findViewById(R.id.web_editors_container);
        for (Author author : WEB_EDITORS) {
            String value = "<html><a href=\"" + UrlGenerator.forAuthor(this, author.getId()) + "\">" + author.getName() + "</a>" +
                    "" + (!TextUtils.isEmpty(author.getAdditionInfo()) ? " - " + author.getAdditionInfo() : "") +
                    "</html>";
            TextView textView = new TextView(this);
            textView.setText(value);
            textView.setTextSize(16);
            textView.setText(Html.fromHtml(value));
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setPadding(0, 0, 0, Utils.convertDpToPixel(10, this));
            webEditorsContainer.addView(textView);
        }
    }
}
