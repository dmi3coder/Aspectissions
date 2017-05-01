package io.github.dmi3coder.aspectissions;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import java.io.File;
import java.io.IOException;

/*
  Danger! No MVP zone :(
 */
@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {

  private Button recordButton;
  private MediaRecorder recorder;
  private MediaPlayer player;
  private boolean recording = false;
  private Uri fileUri;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    fileUri = Uri.fromFile(new File(getFilesDir().getAbsoluteFile(), "record"));
    recordButton = (Button) findViewById(R.id.record_button);
    recordButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        recordAudio();
        Log.d("SomeWeirdTag", "onClick: after recordAudio");
      }
    });
    player = new MediaPlayer();
  }

  @DangerousPermission(permission.RECORD_AUDIO)
  private void recordAudio() {
    if (!recording) {
      player.stop();
      player.release();
      player = null;
      recorder = prepareRecorder();
      recorder.start();
      recordButton.setText("STOP");
    } else {
      recorder.stop();
      recorder.release();
      player = MediaPlayer.create(this, fileUri);
      player.start();
      recordButton.setText("RECORD AGAIN");
    }
    recording = !recording;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    recordAudio();
  }


  private MediaRecorder prepareRecorder() {
    MediaRecorder mediaRecorder = new MediaRecorder();
    try {
      mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
      mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
      mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
      mediaRecorder.setOutputFile(fileUri.getPath());
      mediaRecorder.prepare();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return mediaRecorder;
  }


}
