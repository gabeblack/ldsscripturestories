package gabe.android.oldtestament;

import gabe.android.scriptures.ChapterListingActivity;
import android.os.Bundle;

public class OldTestamentActivity extends gabe.android.scriptures.ChapterListingActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	ChapterListingActivity.ROOT_FOLDER = "/scripture_stories_OT";
    	ChapterListingActivity.MP4_PREFIX="http://media2.ldscdn.org/assets/scripture-stories/old-testament-stories/";
    	ChapterListingActivity.WMV_PREFIX="http://broadcast2.lds.org/scripture-stories/old-testament/";
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public String getDefaultLocale()
    {
    	return "eng";
    }
}