package gabe.android.doctrineandcovenants;

import android.os.Bundle;
import gabe.android.scriptures.ChapterListingActivity;

public class DoctrineAndCovenantsActivity extends ChapterListingActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	ChapterListingActivity.ROOT_FOLDER = "/scripture_stories_DC";
    	ChapterListingActivity.MP4_PREFIX="http://media2.ldscdn.org/assets/scripture-stories/doctrine-and-covenants-stories/";
    	ChapterListingActivity.WMV_PREFIX="http://broadcast2.lds.org/scripture-stories/doctrine-and-covenants-stories/";
        super.onCreate(savedInstanceState);
    }
    @Override
    public String getDefaultLocale()
    {
    	return "eng";
    }
}