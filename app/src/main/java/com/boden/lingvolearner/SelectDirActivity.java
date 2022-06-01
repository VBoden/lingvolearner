package com.boden.lingvolearner;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

import android.os.Bundle;
//import android.app.Activity;
import android.content.Intent;
//import android.content.SharedPreferences;
import android.text.Html;
//import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class SelectDirActivity extends GeneralMainActivity {
	private ListView fileList;
	private String[] items;
	private ArrayList<String> listOfDirectories;
	private ArrayList<String> listOfDirNames;
	private Stack<String> stackPaths=new Stack<String>();
	//public static final String EXT_NAME_DIR = "dirName";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_dict);
        fileList = (ListView) findViewById(R.id.listView1);
        registerForContextMenu(fileList);

		listOfDirectories = new ArrayList<String>();
		listOfDirNames = new ArrayList<String>();

		getFile(new File("/mnt/sdcard/"));
		stackPaths.push("/");
		stackPaths.push("/mnt/");
		stackPaths.push("/mnt/sdcard/");		

		items = listOfDirNames.toArray(new String[listOfDirectories.size()]);
		setAdapterToList(items);

		fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View itemClicked,
					int position, long id) {
				//Toast.makeText(SelectDict.this, "Натиснуто " + position,
				//		Toast.LENGTH_LONG).show();
				File f = new File(listOfDirectories.get(position));				
				if (position == 0) {
					if (stackPaths.size()>1){
					stackPaths.pop();
					getFile(new File(stackPaths.peek()));
					items = listOfDirNames.toArray(new String[listOfDirectories.size()]);
					setAdapterToList(items);}
			//	} else if (!f.isDirectory()) {
				/*	SharedPreferences settings=getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
					String dictionaries="";
					if (settings.contains(DICTIONARIES) == true) {
						dictionaries = settings.getString(DICTIONARIES, "");					
					}					
	                SharedPreferences.Editor prefEditor=settings.edit();
	                Dict dict=new Dict(listOfFiles.get(position), items[position].substring(0,items[position].lastIndexOf('.')));
	                prefEditor.putString(DICTIONARIES, dict.toString()+dictionaries);
	                Log.i("DEBUG_INFO_MY","saved dictionary: "+dict.toString()+dictionaries);
	                prefEditor.commit();*/
					
					
				} else {
					stackPaths.push(f.toString());
					getFile(f);
					items = listOfDirNames.toArray(new String[listOfDirectories.size()]);
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
				if ((new File(listOfDirectories.get(position))).isDirectory() ||position==0){
					imv.setImageResource(R.drawable.folder);
				};//else{imv.setImageResource(R.drawable.icon);}
				
				
				TextView tv = (TextView) row.findViewById(R.id.textView1);
				tv.setText(Html.fromHtml(getItem(position)));
				// tv.setText(getItem(position));

				return row;
			}
		};
		fileList.setAdapter(adapt);
	}

	public void getFile(File dir) {
		listOfDirectories.clear();
		listOfDirNames.clear();
	//	Log.i("DEBUG_SELECTDICT","start");
		listOfDirectories.add(dir.toString());
		listOfDirNames.add(dir.toString());
		String[] dirs = dir.list();
		if (dirs.length > 0) {
			for (int i = 0; i < dirs.length; i++) {
				File f = new File(dir, dirs[i]);
				if (f.isDirectory() && f.getName().charAt(0)!='.') {
					listOfDirectories.add(f.toString());
				//	Log.i("DEBUG_SELECTDICT","added to listOfFiles:"+f.toString());
					listOfDirNames.add(f.getName());
					//Log.i("DEBUG_SELECTDICT","added to listOfFileNames:"+f.getName());
				}
			}
		}
	//	Log.i("DEBUG_SELECTDICT","end of load");
	}


	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterContextMenuInfo aMenuInfo = (AdapterContextMenuInfo) menuInfo;
        
        final int position = aMenuInfo.position;
        
      //  final AdapterData data = adapter.getItem(aMenuInfo.position);

      //  menu.setHeaderTitle("Дії...");        
        menu.add(R.string.select).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
            	//listOfDicts.remove(position);
            	Intent intent = new Intent();
				intent.putExtra(OptionActivity.EXT_NAME_DIR, listOfDirectories.get(position));
				setResult(RESULT_OK, intent);
				finish();            	           	 
                return true;
            }
        });
       
        
    }
  //  @Override
  //  public boolean onCreateOptionsMenu(Menu menu) {
      //  getMenuInflater().inflate(R.menu.activity_select_dir, menu);
  //      return true;
  //  }
}
