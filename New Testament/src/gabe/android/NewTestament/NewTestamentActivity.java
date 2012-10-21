package gabe.android.NewTestament;

import gabe.android.scriptures.Chapter;
import gabe.android.scriptures.ChapterListingActivity;
import android.os.Bundle;

public class NewTestamentActivity extends ChapterListingActivity {
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	ChapterListingActivity.ROOT_FOLDER = "/scripture_stories_NT";
    	ChapterListingActivity.MP4_PREFIX="http://media2.ldscdn.org/assets/scripture-stories/new-testament-stories/";
    	ChapterListingActivity.WMV_PREFIX="http://broadcast2.lds.org/scripture-stories/new-testament/";
    	Chapter.LANGUAGE_QUALITY_PREFIX="-700k-";
        super.onCreate(savedInstanceState);
		//Log.d(LOG_TAG, "onCreate");
    }
}
