package com.weather.information.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.weather.information.R;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_about);
    }

    @Override
    public void onResume() {
        super.onResume();

        final ActionBar actionBar = this.getActionBar();
        actionBar.setTitle(this.getString(R.string.weather_about_action));
    }

    public void onClickLegalInformation(final View view) {
        final Intent intent = new Intent("com.weather.information.WEATHERINFO")
                .setComponent(new ComponentName("com.weather.information",
                        "com.weather.information.activity.LicensesActivity"));
        this.startActivity(intent);
    }

    public void onClickSourceCode(final View view) {
        final String url = this.getString(R.string.application_source_code_url);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    public void onClickRemoteData(final View view) {
        final String url = this.getString(R.string.openweahtermap_url);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    public void onClickMyWeb(final View view) {
        final String url = this.getString(R.string.my_url);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}
