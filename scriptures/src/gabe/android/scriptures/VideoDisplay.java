package gabe.android.scriptures;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

class OkClass implements DialogInterface.OnClickListener 
{
	private VideoDisplay context;

	public OkClass(VideoDisplay videoDisplay) {
		this.context = videoDisplay;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		context.finish();
	}
	
}
public class VideoDisplay extends Activity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    public VideoView videoView;
	@Override
	public void onCompletion(MediaPlayer mp) {
		this.finish();
    }

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video);
        videoView = (VideoView) findViewById(R.id.video_id);
        MediaController mc = new MediaController(this);
        mc.setAnchorView(videoView);
        mc.setMediaPlayer(videoView);
        videoView.setOnErrorListener(this);
        videoView.setMediaController(mc);
        videoView.setOnCompletionListener(this);
        Bundle extras = getIntent().getExtras();
        Uri video = Uri.parse(extras.getString("video_path"));
        //Bundle returnData = (Bundle) getLastNonConfigurationInstance();
        //if (returnData == null) {
        videoView.setVideoURI(video);
         
        /*} else {
        	// apply properties to new object
        	videoView.setVideoURI(video);
        	videoView.seekTo(returnData.getInt(VIDEO_POSITION));
        }*/
        //videoView.setVideoPath(extras.getString("video_path"));
        //Toast.makeText(this, extras.getString("video_path"), Toast.LENGTH_LONG).show();
        videoView.start();
	}
/*
	@Override
	public Object onRetainNonConfigurationInstance() 
	{
		// Device configuration changed
		// Save current video playback state
		Bundle data = new Bundle();
		data.putInt(VIDEO_POSITION,videoView.getCurrentPosition());
		return data;
	}
*/

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Playback Failed");
		alertDialogBuilder
			.setMessage("It looks like your device does not support this media codec.  Try going in to Menu->Settings and selecting a different media type and try again. Sorry for the inconvenience.")
			.setCancelable(false)
			.setPositiveButton("OK",new OkClass(this));
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
		return true;
	}
}
