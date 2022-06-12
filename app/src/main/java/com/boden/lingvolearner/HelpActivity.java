package com.boden.lingvolearner;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class HelpActivity extends Activity {
	protected static final String CONTENT = "content";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);

		Bundle extras = getIntent().getExtras();
		String s = extras.getString(CONTENT);

		WebView webView = (WebView) findViewById(R.id.textView1);
		webView.loadDataWithBaseURL(null, s, "text/html", "utf-8", null);
		final WebSettings webSettings = webView.getSettings();
		webSettings.setDefaultFontSize(20);
	}

}
