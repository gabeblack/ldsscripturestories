/*  
    Copyright 2012 Gabriel Black <gabenbecca@gmail.com>

    This file is part of LDS Scripture Stories

    LDS Scripture Stories is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    LDS Scripture Stories is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with LDS Scripture Stories.  If not, see <http://www.gnu.org/licenses/>.
*/

package gabe.android.scriptures;

import java.io.File;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
//import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class ChapterListingActivity extends ListActivity {
	public static final String LOG_TAG = "SCRIPTURES";
    //initialize our progress dialog/bar
	private Menu mMenu;
	private boolean mCanDownload = true;
	private MyArrayAdapter m_adapter;
	public static ChapterListingActivity context;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    
    //initialize root directory
    public static String ROOT_FOLDER = "/scripture_stories";
    public static String MP4_PREFIX="http://media2.ldscdn.org/assets/scripture-stories/book-of-mormon-stories/";
	public static String WMV_PREFIX="http://broadcast2.lds.org/scripture-stories/book-of-mormon/";

    public static File MOVIE_FOLDER= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
    public static String SELECTED_LANGUAGE="eng";
    public static String SELECTED_QUALITY="-360p-";
    public static String SELECTED_MEDIA_TYPE="mp4";

    public static Chapter[] chapters;
	private final static int DOWNLOAD_ID = 1;
	private final static int SETTINGS_ID = 2;
	private final static int CLEAR_CACHE_ID = 3;
	private final static int CANCEL_DOWNLOAD_ID = 4;
	static final String BOOKMARK_ID = "BOOKMARK";
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
		//Log.d(LOG_TAG, "onCreate");
		ChapterListingActivity.context=this;        
        checkAndCreateDirectory(MOVIE_FOLDER+ROOT_FOLDER);
        this.getPrefs();
		refreshListView(getResources());
		
    }

    private void refreshListView(Resources resources)
    {
        int titles_id = resources.getIdentifier(SELECTED_LANGUAGE+"Titles", "array", getPackageName());
        int descriptions_id = resources.getIdentifier(SELECTED_LANGUAGE+"Descriptions", "array", getPackageName());
        int chapterFiles_id = resources.getIdentifier("chapterFiles","array",getPackageName());
        
        String[] titles = resources.getStringArray(titles_id);
        String[] descriptions = resources.getStringArray(descriptions_id);
        chapters = new Chapter[titles.length];
        String[] chapterFiles = resources.getStringArray(chapterFiles_id);
        for(int i=0; i<titles.length; ++i) {
        	chapters[i]=new Chapter(chapterFiles[i],i+1,titles[i],descriptions[i],resources.getIdentifier("chapter"+(i+1), "drawable",getPackageName()));
        }
        m_adapter = new MyArrayAdapter(this, chapters);
        this.setListAdapter(m_adapter);
        ListView listView = getListView();
		listView.setTextFilterEnabled(true);
		//Log.d(LOG_TAG, "Listview refreshed!");
    }
    /*@Override
    public void onResume() 
    {
    	super.onResume();
    	Log.d(LOG_TAG, "onResume");
    }
    
    @Override
    public void onPause()
    {
    	super.onPause();
    	Log.d(LOG_TAG, "onPause");
    }*/
    @Override
    public void onStart()
    {
    	super.onStart();
		//Log.d(LOG_TAG, "onStart");
    	if(!this.getPrefs()) {
    		// then something changed so we need to refresh the lists...
    		synchronized(this) {
    			if(DownloadFileAsync.currentDownload != null)
    				DownloadFileAsync.currentDownload.cancel(true);
    		}	
    		refreshListView(getResources());
    	}
    }
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        super.onCreateOptionsMenu(menu);
        
        menu.add(0, SETTINGS_ID, 0, R.string.menu_settings);
        menu.add(0, DOWNLOAD_ID, 0, R.string.menu_download).setEnabled(mCanDownload);
        menu.add(0, CANCEL_DOWNLOAD_ID, 0, R.string.menu_cancel_download).setEnabled(!mCanDownload);
        menu.add(0, CLEAR_CACHE_ID, 0, R.string.menu_clear_cache);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        mMenu = menu;
        return true;
    }

    public void EnableCancelDownload(Boolean value)
    {
    	if(mMenu != null) {
    		mMenu.findItem(CANCEL_DOWNLOAD_ID).setEnabled(value); 
    		mMenu.findItem(DOWNLOAD_ID).setEnabled(!value);
    	}
        this.mCanDownload=!value;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	switch (item.getItemId()) {
    		case CANCEL_DOWNLOAD_ID:
    			synchronized(this) {
    				if(DownloadFileAsync.currentDownload != null)
    					DownloadFileAsync.currentDownload.cancel(true);
    			}
    			return true;
    		case DOWNLOAD_ID:	
    			for(int i=0; i<chapters.length; ++i) {
    				if( !chapters[i].file_exists()) {
    					askDownload(chapters[i],false);
    					return true;
    				}
    			}
    			Toast.makeText(this, "Chapters already downloaded", Toast.LENGTH_LONG).show();
    			return true;
    		case SETTINGS_ID:	
                Intent intent = new Intent(getApplicationContext(),Preferences.class);
                startActivity(intent);
    			return true;
    		case CLEAR_CACHE_ID:
    			File root_dir = new File(ChapterListingActivity.MOVIE_FOLDER + ChapterListingActivity.ROOT_FOLDER);
    			if( root_dir.exists() && root_dir.isDirectory()) {
    				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    				alertDialogBuilder.setTitle("Confirm Operation");
    				alertDialogBuilder
						.setMessage("You are about to delete all files in "+root_dir.getAbsolutePath() + ".  Proceed?")
						.setCancelable(true)
						.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
				    			File root_dir = new File(ChapterListingActivity.MOVIE_FOLDER + ChapterListingActivity.ROOT_FOLDER);
								String[] children = root_dir.list();
		    		        	for (int i = 0; i < children.length; i++) {
		    		        		new File(root_dir, children[i]).delete();
	    		        		}	
		    		        	ChapterListingActivity.context.reloadList();
							}})
						.setNegativeButton("No",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								dialog.cancel();
							}
						});
    				AlertDialog alertDialog = alertDialogBuilder.create();
    				alertDialog.show();
    				//reloadList();
    				return true;
    			}

    	}
    	return false;
    }

    private boolean getPrefs() 
    {
    	boolean unchanged = false;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        unchanged = SELECTED_LANGUAGE.equals(prefs.getString("languagePref", getDefaultLocale())) &&
        		SELECTED_QUALITY.equals(prefs.getString("qualityPref","360p")) &&
        		SELECTED_MEDIA_TYPE.equals(prefs.getString("mediaTypePref","mp4"));
        SELECTED_LANGUAGE = prefs.getString("languagePref", getDefaultLocale());
        SELECTED_QUALITY = prefs.getString("qualityPref","360p");
        SELECTED_MEDIA_TYPE = prefs.getString("mediaTypePref","mp4");
        return unchanged;
    }
    
    public String getDefaultLocale() {
		// TODO Auto-generated method stub
    	if(Locale.getDefault().equals(Locale.CANADA) ||
    	   Locale.getDefault().equals(Locale.ENGLISH) ||
    	   Locale.getDefault().equals(Locale.UK) ||
    	   Locale.getDefault().equals(Locale.US))
    		return "eng";
    	if(Locale.getDefault().equals(Locale.CANADA_FRENCH) ||
    	   Locale.getDefault().equals(Locale.FRANCE) ||
    	   Locale.getDefault().equals(Locale.FRENCH))
    		return "fra";
    	if(Locale.getDefault().equals(Locale.GERMAN) ||
    	   Locale.getDefault().equals(Locale.GERMANY))
    		return "deu";
    	if(Locale.getDefault().equals(Locale.CHINA) ||
    	   Locale.getDefault().equals(Locale.CHINESE) ||
    	   Locale.getDefault().equals(Locale.TRADITIONAL_CHINESE))
    		return "yue";
    	if(Locale.getDefault().equals(Locale.ITALIAN) ||
    	   Locale.getDefault().equals(Locale.ITALIAN))
    		return "ita";
    	if(Locale.getDefault().equals(Locale.JAPAN) ||
    	   Locale.getDefault().equals(Locale.JAPANESE))
    		return "jpn";
    	if(Locale.getDefault().equals(Locale.KOREA) ||
    	   Locale.getDefault().equals(Locale.KOREAN))
    		return "kor";
		return "eng";
	}

	public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
    
    class DownloadChapters implements DialogInterface.OnClickListener {
		private Chapter chapter;

		public DownloadChapters(Chapter chapter) {
			this.chapter = chapter;
		}

		public void onClick(DialogInterface dialog, int id)
    	{
			synchronized(ChapterListingActivity.context) {
				if(ChapterListingActivity.context.mCanDownload) {
					if(chapter != null) 
						new DownloadFileAsync(chapter, false).execute(chapter);
					else
						ChapterListingActivity.context.downloadAllChapters();
		    	}
				else {
					Toast.makeText(ChapterListingActivity.context, "Download already in progress!", Toast.LENGTH_LONG).show();
					//context.mProgressDialog.show();
				}
			}
    	}
    }
    
    public void downloadAllChapters()
    {
    	for(int i=0; i<chapters.length; ++i) {
			if( !chapters[i].file_exists()) {
				new DownloadFileAsync(chapters[i],true).execute(chapters[i]);
				return;
			}
		}
    }

    protected void askDownload(Chapter chapter, Boolean requiredDownload)
    {
    	if( !isNetworkAvailable() ) {
			Toast.makeText(this, "Download requires an internet connection!", Toast.LENGTH_LONG).show();
			return;
    	}
    		
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    	if(requiredDownload) {
    		alertDialogBuilder.setTitle("Download required to play...");
			alertDialogBuilder
				.setMessage("Download all chapters?")
				.setCancelable(true)
				.setPositiveButton("Yes, all chapters!",new DownloadChapters(null))
				.setNegativeButton("No, just this chapter",new DownloadChapters(chapter));
    	}
    	else {
    		alertDialogBuilder.setTitle("Download Chapters for offline viewing?");
    		alertDialogBuilder
				.setMessage("Click yes to Download!")					
				.setCancelable(true)
				.setPositiveButton("Yes",new DownloadChapters(null))
				.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});
    	}

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
    }
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
		Chapter item = (Chapter) getListAdapter().getItem(position);
 		//Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
		Intent intent = new Intent(getApplicationContext(),VideoDisplay.class);
		if(item.file_exists()) {
			intent.putExtra("video_path",item.fname);
			//Log.d(LOG_TAG, item.fname);
			//intent.setDataAndType(Uri.fromFile(new File(item.fname)),"video/*");
			startActivity(intent);
			this.setBookmark(item);
			
		}
		else if(this.isNetworkAvailable()) {
			if(SELECTED_MEDIA_TYPE.equals("wmv")) {
				this.askDownload(item, true);
			}
			else {
				intent.putExtra("video_path",item.url);
				//Toast.makeText(this, item.url, Toast.LENGTH_LONG).show();
				//intent = new Intent();
				//intent.setDataAndType(Uri.parse(item.url),"video/*");
				
				//Log.d(LOG_TAG, item.url);
				startActivity(intent);
				setBookmark(item);
			}
		}
		else {
			Toast.makeText(this, "Download requires an internet connection!", Toast.LENGTH_LONG).show();
		}
	}
    //this is our download file asynctask
    
    private void setBookmark(Chapter item) {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    	SharedPreferences.Editor editor = prefs.edit();
    	editor.putString(BOOKMARK_ID, item.title);
    	editor.commit();
    	reloadList();
    	
	}

	//function to verify if directory exists
    public void checkAndCreateDirectory(String dirName){
        File new_dir = new File( dirName );
        if( !new_dir.exists() ){
            new_dir.mkdirs();
            //new_dir=null;
            //new_dir.canRead();
        }
        
    }
    

	public void reloadList() {
		m_adapter.notifyDataSetChanged();
	}
}
