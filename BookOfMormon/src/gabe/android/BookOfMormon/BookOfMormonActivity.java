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
