package com.darkosancanin.naturaldateandtime;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setUpButtonClickEvents();
    }

    private void setUpButtonClickEvents(){
        Button wwwButton = (Button) findViewById(R.id.www_button);
        wwwButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.url)));
                startActivity(browserIntent);
            }
        });

        Button emailButton = (Button) findViewById(R.id.email_button);
        emailButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.email_address)});
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            }
        });
    }
}
