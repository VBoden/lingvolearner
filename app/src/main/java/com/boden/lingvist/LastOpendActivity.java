package com.boden.lingvist;

import java.util.ArrayList;
//import java.util.prefs.Preferences;

import android.os.Bundle;
//import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
//import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class LastOpendActivity extends GeneralMainActivity {
	private ListView dictsList;
	private String[] items;
	private ArrayList<String> listOfDictsNames;
	private ArrayList<Dict> listOfDicts;
	private int positionInList;
	private ArrayAdapter<String> adapter;
	
	
	public static final String EXT_NEW_PATH = "dictNewPath";
	
	public static final int IDD_RENAME = 101;
	public static final int IDD_CHANGE_PATH = 103;
	public static final int IDD_DELETE = 102;
	protected static final int REQUEST_CODE_SELECT_PATH = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_last_opend);

		dictsList = (ListView) findViewById(R.id.listView1);
		items = getListFormSettings();
	//	if (items != null) {
			setAdapter();
		//	adapter=new ArrayAdapter<String>(this,
		//			android.R.layout.simple_list_item_1, items);
		//	dictsList.setAdapter(adapter);
	//	}
		//dictsList.setOnCreateContextMenuListener(this);
		registerForContextMenu(dictsList);
		dictsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View itemClicked,
					int position, long id) {
				SharedPreferences settings=getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
				StringBuffer dictionaries=new StringBuffer();
				dictionaries.append(listOfDicts.get(position).toString());
				for (int i=0;i<listOfDicts.size();i++){
					if (i!=position){
						dictionaries.append(listOfDicts.get(i).toString());	
					}
				}
				
                SharedPreferences.Editor prefEditor=settings.edit();                
                prefEditor.putString(DICTIONARIES, dictionaries.toString());
               // Log.i("DEBUG_LastOpend","saved dictionary: "+dictionaries.toString());
                prefEditor.commit();
				
				Intent intent = new Intent();
				intent.putExtra(MainFormActivity.EXT_NAME_VOC, listOfDicts.get(position).getPath());
				setResult(RESULT_OK, intent);
				finish();
			}
			});			
	}

	public String[] getListFormSettings() {
		String[] items = null;
		listOfDictsNames = new ArrayList<String>();
		listOfDicts = new ArrayList<Dict>();
	//	Log.i("DEBUG_MY_INFO","staring gettin settings");
		SharedPreferences settings = getSharedPreferences(APP_PREFERENCES,
				MODE_PRIVATE);
		//Log.i("DEBUG_LastOpend","getted settings");
		if (settings.contains(DICTIONARIES) == true) {			
			String s = settings.getString(DICTIONARIES, "");
			StringBuffer sb = new StringBuffer(s);
			while (sb.length() > 0) {				
				Dict dict = new Dict(sb.substring(sb.indexOf("<dict>"),
						sb.indexOf("</dict>")+7));				
				listOfDicts.add(dict);
				listOfDictsNames.add(dict.getName());				
				sb.delete(0, sb.indexOf("</dict>") + 7);				
			}			
			items = listOfDictsNames.toArray(new String[listOfDictsNames.size()]);
		}		
		return items;
	}
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterContextMenuInfo aMenuInfo = (AdapterContextMenuInfo) menuInfo;

        // Получаем позицию элемента в списке
        final int position = aMenuInfo.position;

        // Получаем данные элемента списка, тип данных здесь вы должны указать свой!
      //  final AdapterData data = adapter.getItem(aMenuInfo.position);

        menu.setHeaderTitle(R.string.actions_with_dicts);        
        menu.add(R.string.change_dict_name).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
            	//listOfDicts.remove(position);
            	positionInList=position;
            	showDialog(IDD_RENAME);            	 
                return true;
            }
        });
        menu.add(R.string.set_new_path).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
            	//listOfDicts.remove(position);
            	positionInList=position;
            	Intent intent = new Intent();
				intent.setClass(LastOpendActivity.this, SelectNewPathActivity.class);
			//	intent.putExtra(SelectDirActivity.EXT_NAME_DIR, pathToSoundFiles);				
				startActivityForResult(intent, REQUEST_CODE_SELECT_PATH);          	 
                return true;
            }
        });
        menu.add(R.string.delete_dict).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
            	positionInList=position;
            	showDialog(IDD_DELETE);            	
                return true;
            }
        });
    }
	public void saveChangedDictsList(){
		listOfDictsNames.clear();
    	SharedPreferences settings=getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
		StringBuffer dictionaries=new StringBuffer();				
		for (int i=0;i<listOfDicts.size();i++){					
				dictionaries.append(listOfDicts.get(i).toString());
				listOfDictsNames.add(listOfDicts.get(i).getName());
			}
		
        SharedPreferences.Editor prefEditor=settings.edit();                
        prefEditor.putString(DICTIONARIES, dictionaries.toString());
   //     Log.i("DEBUG_LastOpend","saved dictionary: "+dictionaries.toString());
        prefEditor.commit();
        
        items = listOfDictsNames.toArray(new String[listOfDictsNames.size()]);
     //   LastOpendActivity.this.dictsList.setAdapter(new ArrayAdapter<String>(this,
	//			android.R.layout.simple_list_item_1, items));
        //adapter.notifyDataSetChanged();
      //  dictsList.setAdapter(adapter);
        setAdapter();
	}
	public void setAdapter(){
		adapter=new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, items);
		dictsList.setAdapter(adapter);
	}
	@Override
	protected Dialog onCreateDialog(int id){
		switch (id){
		case IDD_RENAME:
		    AlertDialog.Builder builder  =  new AlertDialog.Builder(this);
		    builder.setTitle(R.string.editing_dict);
		    builder.setMessage(R.string.set_new_name);
		    final EditText inputName = new EditText(this);  
		    inputName.setText(listOfDicts.get(positionInList).getName());
		    inputName.setSelection(inputName.getText().toString().length());
		   // inputName.setFocusable(true);
		   // inputName.requestFocus();
		    builder.setView(inputName);		    
		    builder.setPositiveButton(R.string.change, new DialogInterface.OnClickListener(){
		           public void onClick(DialogInterface dialog , int id){
		        	  String newName= inputName.getText().toString();	
		//        	   Log.i("DEBUG_lAST","Name1="+newName);
		//        	   Log.i("DEBUG_lAST","Name2="+newName);
		            	if (newName.length()>0){
		 //           		 Log.i("DEBUG_lAST","Name3="+newName);
		            		listOfDicts.get(positionInList).setName(newName);	
		            	}     	
		            	
		            	saveChangedDictsList();
		               dialog.dismiss();
		               LastOpendActivity.this.removeDialog(IDD_RENAME);
		             //   return;
		                }
		    });
		    builder.setNegativeButton (R.string.cancel,new DialogInterface.OnClickListener (){
		           public void onClick( DialogInterface dialog, int id){
		                 dialog.cancel();
		                 LastOpendActivity.this.removeDialog(IDD_RENAME);}});
		    builder.setCancelable (false);
		    return builder.create();		    
		case IDD_DELETE:
		    builder  =  new AlertDialog.Builder(this);
		    builder.setTitle(R.string.deleting_dict);
		    builder.setMessage(R.string.do_you_wish_delete);		  
		    builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener(){
		           public void onClick(DialogInterface dialog , int id){
		        	   listOfDicts.remove(positionInList);
		            	saveChangedDictsList();		            	
		               dialog.dismiss();
		               LastOpendActivity.this.removeDialog(IDD_DELETE);
		             //   return;
		                }
		    });
		    builder.setNegativeButton (R.string.cancel, new DialogInterface.OnClickListener (){
		           public void onClick( DialogInterface dialog, int id){
		                 dialog.cancel();}});
		    builder.setCancelable (false);
		    return builder.create();
		    
	//	case IDD_CHANGE_PATH:			
	//		return null;
		default :
		return null ;
		}			
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CODE_SELECT_PATH:
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				String newPath = extras.getString(EXT_NEW_PATH);
				if (newPath!=null){
					
					if (newPath.length()>0){
	            		// Log.i("DEBUG_lAST","newPath="+newPath);
	            		listOfDicts.get(positionInList).setPath(newPath);	
	            		saveChangedDictsList();
	            	} 
				}
				}
				break;
			}
		}
	
	//@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_last_opend, menu);
//		return true;
//	}
}
