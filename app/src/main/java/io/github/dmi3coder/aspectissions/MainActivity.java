package io.github.dmi3coder.aspectissions;

import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import java.io.File;
import java.io.IOException;

/*
  Danger! No MVP zone :(
 */
public class MainActivity extends AppCompatActivity {

  private Button recordButton;
  private MediaRecorder recorder;
  private MediaPlayer player;
  private Uri fileUri;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    fileUri = Uri.fromFile(new File(getFilesDir().getAbsoluteFile(), "record"));
    recordButton = (Button) findViewById(R.id.record_button);
    recordButton.setOnClickListener(v -> {
      recordAudio();
      Log.d("SomeWeirdTag", "onClick: after processAudio");
    });
    player = new MediaPlayer();
  }

  @DangerousPermission(permission.RECORD_AUDIO)
  private void recordAudio(){
    if(player.isPlaying()) {
      player.stop();
      player.release();
    }
    recorder = prepareRecorder();
    recorder.start();
    recordButton.setText("RECORDING...");
    recordButton.setEnabled(false);
    Handler h = new Handler();
    h.postDelayed(this::playAudio,3000);
  }

  private void playAudio(){
    recorder.stop();
    recorder.release();
    player = MediaPlayer.create(this, fileUri);
    player.start();
    recordButton.setText("RECORD AGAIN");
    recordButton.setEnabled(true);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if(grantResults[0] == PackageManager.PERMISSION_GRANTED) recordAudio();
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
