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

//import android.util.Log;
import android.widget.ProgressBar;

public class Chapter {
    	public String url;
    	public String fname;
    	public String title;
    	public String description;
    	public int icon;
    	private String prefix;
    	private String quality;
		public int download_progress=0;
		private ProgressBar progressbar;
		public static String LANGUAGE_QUALITY_PREFIX="-1000k-";
    	public Chapter(String url, int i, String title, String description, int drawable)
    	{
    		//Log.d(BookOfMormonActivity.LOG_TAG, "New Chapter!");
    		prefix = ChapterListingActivity.MP4_PREFIX;
    		quality = "-"+ChapterListingActivity.SELECTED_QUALITY+"-";
    		String extension="."+ChapterListingActivity.SELECTED_MEDIA_TYPE+"?download=true";
    		if(ChapterListingActivity.SELECTED_MEDIA_TYPE.equals("wmv")) {
    			prefix = ChapterListingActivity.WMV_PREFIX;
    			quality = "-300k-";
    			extension = ".wmv";
    		}
    		else if(!ChapterListingActivity.SELECTED_LANGUAGE.equals("eng")) {
    			prefix = ChapterListingActivity.WMV_PREFIX;
    			quality = Chapter.LANGUAGE_QUALITY_PREFIX;
    			extension = ".mp4";    			
    		}
    			
    		
    		this.url=prefix+url+quality+ChapterListingActivity.SELECTED_LANGUAGE+extension;
    		this.fname=ChapterListingActivity.MOVIE_FOLDER + ChapterListingActivity.ROOT_FOLDER + "/" + "chapter"+ i + quality + ChapterListingActivity.SELECTED_LANGUAGE+"."+ChapterListingActivity.SELECTED_MEDIA_TYPE;
            this.title=title;
            this.description=description;
            this.icon=drawable;
    	}
    	public Boolean file_exists()
    	{
    		File file = new File(fname);
    		return file.length() > 0;
    	}
    	public String toString() 
    	{
    		return title + " - " + description;
    	}
		public boolean is_downloading() {
			return this.download_progress > 0 && this.download_progress < 100;
		}
		public ProgressBar getProgressbar() {
			return progressbar;
		}
		public void setProgressbar(ProgressBar progressbar) {
			this.progressbar = progressbar;
		}
		public void setProgress(int progress) {
			this.download_progress=progress;
			if(this.progressbar != null)
				this.progressbar.setProgress(progress);
			
		}
    	
}
