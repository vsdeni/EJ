package com.ejnew.news.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ejnew.news.R;
import com.ejnew.news.enums.FontSize;
import com.ejnew.news.helpers.Utils;

public class SettingsActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {
    private SeekBar mFontSeekBar;
    int stepSize = 100 / (FontSize.values().length - 1);
    Toolbar mToolbar;
    TextView mFontValueView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mFontSeekBar = (SeekBar) findViewById(R.id.font_size_seekbar);
        mFontSeekBar.setOnSeekBarChangeListener(this);

        mFontValueView = (TextView) findViewById(R.id.font_size_value);

        int fontSize = Utils.Prefs.getInt(Utils.Prefs.FONT_SIZE, 0, this);
        onProgressChanged(mFontSeekBar, fontSize * stepSize, false);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void updateFontSize(int fontSize) {
        FontSize font = FontSize.getById(fontSize);
        mFontValueView.setText(font.getTitleResId());
        Utils.Prefs.saveInt(Utils.Prefs.FONT_SIZE, font.ordinal(), this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        progress = (Math.round(progress / stepSize)) * stepSize;
        seekBar.setProgress(progress);
        for (FontSize fontSize : FontSize.values()) {
            if ((Math.round(progress / stepSize)) == fontSize.ordinal()) {
                updateFontSize(fontSize.ordinal());
                return;
            }
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
