package com.boden.lingvist;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
//import android.content.SharedPreferences;
import android.text.Html;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SelectNewPathActivity extends Activity {
	private ListView fileList;
	private String[] items;
	private ArrayList<String> listOfFiles;
	private ArrayList<String> listOfFileNames;
	private Stack<String> stackPaths=new Stack<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_dict);

		fileList = (ListView) findViewById(R.id.listView1);

		listOfFiles = new ArrayList<String>();
		listOfFileNames = new ArrayList<String>();

		getFile(new File("/mnt/sdcard/"));
		stackPaths.push("/");
		stackPaths.push("/mnt/");
		stackPaths.push("/mnt/sdcard/");		

		items = listOfFileNames.toArray(new String[listOfFiles.size()]);
		setAdapterToList(items);

		fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View itemClicked,
					int position, long id) {
				//Toast.makeText(SelectDict.this, "Натиснуто " + position,
				//		Toast.LENGTH_LONG).show();
				File f = new File(listOfFiles.get(position));				
				if (position == 0) {
					if (stackPaths.size()>1){
					stackPaths.pop();
					getFile(new File(stackPaths.peek()));
					items = listOfFileNames.toArray(new String[listOfFiles.size()]);
					setAdapterToList(items);}
				} else if (!f.isDirectory()) {
					Intent intent = new Intent();
					intent.putExtra(LastOpendActivity.EXT_NEW_PATH, listOfFiles.get(position));
					setResult(RESULT_OK, intent);
					finish(); 					
				} else {
					stackPaths.push(f.toString());
					getFile(f);
					items = listOfFileNames.toArray(new String[listOfFiles.size()]);
					setAdapterToList(items);
				}

			}
		});
    }


	public void setAdapterToList(String[] items) {
		ArrayAdapter<String> adapt = new ArrayAdapter<String>(this,
				R.layout.file_list_item, items) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View row;

				if (null == convertView) {
					LayoutInflater mInflater = getLayoutInflater();
					row = mInflater.inflate(R.layout.file_list_item, null);
				} else {
					row = convertView;
				}

				ImageView imv = (ImageView) row.findViewById(R.id.imageView1);
				if (position==0){
					imv.setImageResource(R.drawable.up);
				}else
				if ((new File(listOfFiles.get(position))).isDirectory() ||position==0){
					imv.setImageResource(R.drawable.folder);
				}else{imv.setImageResource(R.drawable.icon);}
				
				
				TextView tv = (TextView) row.findViewById(R.id.textView1);
				tv.setText(Html.fromHtml(getItem(position)));
				// tv.setText(getItem(position));

				return row;
			}
		};
		fileList.setAdapter(adapt);
	}

	public void getFile(File dir) {
		listOfFiles.clear();
		listOfFileNames.clear();
		//Log.i("DEBUG_SELECTDICT","start");
		listOfFiles.add(dir.toString());
		listOfFileNames.add(dir.toString());
		String[] dirs = dir.list();
		if (dirs.length > 0) {
			for (int i = 0; i < dirs.length; i++) {
				File f = new File(dir, dirs[i]);
				if (f.isDirectory() && f.getName().charAt(0)!='.') {
					listOfFiles.add(f.toString());
				//	Log.i("DEBUG_SELECTDICT","added to listOfFiles:"+f.toString());
					listOfFileNames.add(f.getName());
				//	Log.i("DEBUG_SELECTDICT","added to listOfFileNames:"+f.getName());
				}
			}
			for (int i = 0; i < dirs.length; i++) {
				File f = new File(dir, dirs[i]);
				if (!f.isDirectory()) {
					if (f.getName().endsWith(".vcb")) {
						listOfFiles.add(f.toString());
					//	Log.i("DEBUG_SELECTDICT","added to listOfFiles:"+f.toString());
						listOfFileNames.add(f.getName());
					//	Log.i("DEBUG_SELECTDICT","added to listOfFileNames:"+f.getName());
					}
				}
			}

		}
	//	Log.i("DEBUG_SELECTDICT","end of load");
	}
    
  //  @Override
  //  public boolean onCreateOptionsMenu(Menu menu) {
    //    getMenuInflater().inflate(R.menu.activity_select_new_path, menu);
 //       return true;
 //   }
}
