package com.boden.lingvist;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class HelpActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);

		InputStream iFile;
		String s;
		try {
			iFile = getResources().openRawResource(R.raw.help);
			InputStreamReader tmp = new InputStreamReader(iFile, "UTF8");
			BufferedReader dataIO = new BufferedReader(tmp);
			StringBuffer sBuffer = new StringBuffer();
			String strLine = null;
			while ((strLine = dataIO.readLine()) != null) {
				sBuffer.append(strLine);
			}
			dataIO.close();
			iFile.close();
			s = sBuffer.toString();
		} catch (Exception e) {
			s =getResources().getString(R.string.coud_not_open_help);
		}
		TextView tv = (TextView) findViewById(R.id.textView1);
		// String s=getText(R.string.help_text);
		tv.setText(Html.fromHtml(s));
	}

}
