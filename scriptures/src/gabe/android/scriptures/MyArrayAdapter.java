package gabe.android.scriptures;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MyArrayAdapter extends ArrayAdapter<Chapter>{
		private final Context context;
		private final Chapter[] values;

		public MyArrayAdapter(Context context, Chapter[] values) {
			super(context, R.layout.rowlayout, R.id.label, values);
			this.context = context;
			this.values = values;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//Log.d(BookOfMormonActivity.LOG_TAG, "Adapter refreshed!");
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
			TextView textView = (TextView) rowView.findViewById(R.id.label);
			ImageView imageView = (ImageView) rowView.findViewById(R.id.chapter_icon);
			textView.setText(values[position].toString());
			ImageView downloaded = (ImageView) rowView.findViewById(R.id.downloaded_id);
			ImageView bookmark = (ImageView) rowView.findViewById(R.id.bookmark_id);
			ProgressBar progress = (ProgressBar) rowView.findViewById(R.id.pb);
			//Log.d(BookOfMormonActivity.LOG_TAG, values[position].title + "progress: " +values[position].download_progress);
			values[position].setProgressbar(progress);
			if(values[position].is_downloading()) {
				progress.setVisibility(View.VISIBLE);
				progress.setProgress(values[position].download_progress);
			}
			else 
				progress.setVisibility(View.INVISIBLE);
			
			if(values[position].file_exists()) 
				downloaded.setVisibility(View.VISIBLE);
			else 
				downloaded.setVisibility(View.INVISIBLE);
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			if(values[position].title.equals(prefs.getString(ChapterListingActivity.BOOKMARK_ID,"none"))) 
				bookmark.setVisibility(View.VISIBLE);
			else
				bookmark.setVisibility(View.INVISIBLE);
			
					
			imageView.setImageResource(values[position].icon);
			return rowView;
		}
}	
