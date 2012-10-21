package gabe.android.scriptures;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class DownloadFileAsync extends AsyncTask<Chapter, String, Chapter> {
    public static DownloadFileAsync currentDownload;
	private Boolean downloadAll;
	private Chapter chapter;
    public DownloadFileAsync(Chapter chapter, Boolean downloadAll)
    {
    	super();
    	this.chapter = chapter;
    	this.downloadAll = downloadAll;
		synchronized(ChapterListingActivity.context) {
			DownloadFileAsync.currentDownload = this;
			ChapterListingActivity.context.EnableCancelDownload(true);
		}
    	initDialog(); 	
    }
    @Override
    protected void onCancelled()
    {
    	synchronized(ChapterListingActivity.context) {
			DownloadFileAsync.currentDownload = null;
			ChapterListingActivity.context.EnableCancelDownload(false);
		}
    	chapter.setProgress(0);
    	chapter.getProgressbar().setVisibility(View.INVISIBLE);
    }
    
    private void initDialog()
    {
    	chapter.setProgress(1);
    	chapter.getProgressbar().setVisibility(View.VISIBLE);
    }
	@Override
    protected void onPreExecute() {
        super.onPreExecute();
        initDialog();
    }
    
    @Override
    protected Chapter doInBackground(Chapter... chapters) {

        try {
            //connecting to url
        	URL u = new URL(chapters[0].url);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.connect();
            
            //lenghtOfFile is used for calculating download progress
            int lengthOfFile = c.getContentLength();
            
            //this is where the file will be seen after the download
            //File temp_file = File.createTempFile("downloading_chapter",null);
            File temp_file = new File(ChapterListingActivity.MOVIE_FOLDER,"downloading_chapter.tmp");
            FileOutputStream f = new FileOutputStream(temp_file);
            //file input is from the url
            InputStream in = c.getInputStream();
            //here's the download code
            byte[] buffer = new byte[1024];
            int len1 = 0;
            long total = 0;
            
            while ((len1 = in.read(buffer)) > 0) {
            	if(this.isCancelled()) {
                	synchronized(ChapterListingActivity.context) {
            			DownloadFileAsync.currentDownload = null;
            			ChapterListingActivity.context.EnableCancelDownload(false);
            		}
                	f.close();
                	temp_file.delete();
                	return chapters[0];
            	}
                total += len1; //total = total + len1
                int progress = (int)((total*100)/lengthOfFile);
                publishProgress("" + progress);
                f.write(buffer, 0, len1);
            }
            f.close();
            File chapter_file = new File(chapters[0].fname);
            //chapter_file.delete();
            //chapter_file.
            temp_file.renameTo(chapter_file);
            return chapters[0];
            
        } catch (Exception e) {
            Log.d(ChapterListingActivity.LOG_TAG, e.getMessage());
        }
        
        return null;
    }
    
    protected void onProgressUpdate(String... progress) {
    	chapter.setProgress(Integer.parseInt(progress[0]));
    	//chapter.getProgressbar().setProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(Chapter fname) {
        //dismiss the dialog after the file was downloaded
		synchronized(ChapterListingActivity.context) {
			DownloadFileAsync.currentDownload = null;
			ChapterListingActivity.context.EnableCancelDownload(false);
		}
		chapter.getProgressbar().setVisibility(View.INVISIBLE);
    	if( fname == null)
    		return;
    	if( !fname.file_exists()) {
    		Toast.makeText(ChapterListingActivity.context, "Download "+fname.fname+" failed", Toast.LENGTH_LONG).show();
    		return;
    	}
    	else
    		ChapterListingActivity.context.reloadList();
    	try {
    		
    		if(downloadAll) {
    			ChapterListingActivity.context.downloadAllChapters();
    		}
    	}
    	catch (Exception e)
    	{
    		return;
    	}
    }
}
