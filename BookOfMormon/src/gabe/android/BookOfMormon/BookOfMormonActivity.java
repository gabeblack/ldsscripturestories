package gabe.android.BookOfMormon;

import gabe.android.scriptures.ChapterListingActivity;
import android.os.Bundle;

public class BookOfMormonActivity extends ChapterListingActivity {
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	ChapterListingActivity.ROOT_FOLDER = "/scripture_stories_BOM";
    	ChapterListingActivity.MP4_PREFIX="http://media2.ldscdn.org/assets/scripture-stories/book-of-mormon-stories/";
    	ChapterListingActivity.WMV_PREFIX="http://broadcast2.lds.org/scripture-stories/book-of-mormon/";
        super.onCreate(savedInstanceState);
		//Log.d(LOG_TAG, "onCreate");
    }
}
