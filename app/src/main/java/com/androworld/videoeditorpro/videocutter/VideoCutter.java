package com.androworld.videoeditorpro.videocutter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.androworld.videoeditorpro.R;
import com.androworld.videoeditorpro.VideoPlayer;
import com.androworld.videoeditorpro.VideoSliceSeekBar;
import com.androworld.videoeditorpro.VideoSliceSeekBar.SeekBarChangeListener;
import com.androworld.videoeditorpro.listvideoandmyvideo.ListVideoAndMyAlbumActivity;
import com.androworld.videoeditorpro.videojoiner.VideoJoinerActivity;
import com.androworld.videoeditorpro.videojoiner.model.VideoPlayerState;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SuppressLint({"WrongConstant"})
public class VideoCutter extends AppCompatActivity implements MediaScannerConnectionClient, OnClickListener {
    public static Context AppContext = null;
    static final boolean k = true;
    MediaScannerConnection a;
    int b = 0;
    int c = 0;
    TextView d;
    TextView e;
    TextView f;
    public FFmpeg fFmpeg;
    TextView g;
    ImageView h;
    VideoSliceSeekBar i;
    VideoView j;
    private String l = "";
    private String m;

    public String n;

    public VideoPlayerState o = new VideoPlayerState();
    private a p = new a();
    private InterstitialAd q;

    private class a extends Handler {
        private boolean b;
        private Runnable c;

        private a() {
            this.b = false;
            this.c = new Runnable() {
                public void run() {
                    a.this.a();
                }
            };
        }


        public void a() {
            if (!this.b) {
                this.b = VideoCutter.k;
                sendEmptyMessage(0);
            }
        }

        @Override public void handleMessage(Message message) {
            this.b = false;
            VideoCutter.this.i.videoPlayingProgress(VideoCutter.this.j.getCurrentPosition());
            if (!VideoCutter.this.j.isPlaying() || VideoCutter.this.j.getCurrentPosition() >= VideoCutter.this.i.getRightProgress()) {
                if (VideoCutter.this.j.isPlaying()) {
                    VideoCutter.this.j.pause();
                    VideoCutter.this.h.setBackgroundResource(R.drawable.play2);
                }
                VideoCutter.this.i.setSliceBlocked(false);
                VideoCutter.this.i.removeVideoStatusThumb();
                return;
            }
            postDelayed(this.c, 50);
        }
    }


    @Override public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView( R.layout.videocutteractivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText("Video Cutter");
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (k || supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(k);
            supportActionBar.setDisplayShowTitleEnabled(false);
            AppContext = this;
            this.h = (ImageView) findViewById(R.id.buttonply1);
            this.i = (VideoSliceSeekBar) findViewById(R.id.seek_bar1);
            this.f = (TextView) findViewById(R.id.Filename);
            this.d = (TextView) findViewById(R.id.left_pointer);
            this.e = (TextView) findViewById(R.id.right_pointer);
            this.g = (TextView) findViewById(R.id.dur);
            this.j = (VideoView) findViewById(R.id.videoView1);
            this.h.setOnClickListener(this);
            this.m = getIntent().getStringExtra("path");
            if (this.m == null) {
                finish();
            }
            this.fFmpeg = FFmpeg.getInstance(this);
            g();
            this.f.setText(new File(this.m).getName());
            this.j.setVideoPath(this.m);
            this.j.seekTo(100);
            e();
            this.j.setOnCompletionListener(new OnCompletionListener() {
                public void onCompletion(MediaPlayer mediaPlayer) {
                    VideoCutter.this.h.setBackgroundResource(R.drawable.play2);
                }
            });
            this.q = new InterstitialAd(this);
            this.q.setAdUnitId(getString(R.string.InterstitialAd));
            this.q.setAdListener(new AdListener() {
                @Override public void onAdClosed() {
                    VideoCutter.this.c();
                }
            });
            a();
            return;
        }
        throw new AssertionError();
    }

    private void a() {
        if (!this.q.isLoading() && !this.q.isLoaded()) {
            this.q.loadAd(new Builder().build());
        }
    }


    public void b() {
        if (this.q == null || !this.q.isLoaded()) {
            c();
        } else {
            this.q.show();
        }
    }


    public void c() {
        Intent intent = new Intent(getApplicationContext(), VideoPlayer.class);
        intent.setFlags(67108864);
        intent.putExtra("song", this.n);
        startActivity(intent);
        finish();
    }

    private void d() {
        String valueOf = String.valueOf(this.c);
        String.valueOf(this.b);
        String valueOf2 = String.valueOf(this.b - this.c);
        String format = new SimpleDateFormat("_HHmmss", Locale.US).format(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsoluteFile());
        sb.append("/");
        sb.append(getResources().getString(R.string.MainFolderName));
        sb.append("/");
        sb.append(getResources().getString(R.string.VideoCutter));
        File file = new File(sb.toString());
        if (!file.exists()) {
            file.mkdirs();
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(Environment.getExternalStorageDirectory().getAbsoluteFile());
        sb2.append("/");
        sb2.append(getResources().getString(R.string.MainFolderName));
        sb2.append("/");
        sb2.append(getResources().getString(R.string.VideoCutter));
        sb2.append("/videocutter");
        sb2.append(format);
        sb2.append(".mp4");
        this.n = sb2.toString();
        a(new String[]{"-ss", valueOf, "-y", "-i", this.m, "-t", valueOf2, "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", this.n}, this.n);
    }

    private void a(String[] strArr, final String str) {
        try {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.show();
            this.fFmpeg.execute(strArr, new ExecuteBinaryResponseHandler() {
                @Override public void onFailure(String str) {
                    Log.d("ffmpegfailure", str);
                    try {
                        new File(str).delete();
                        VideoCutter.this.deleteFromGallery(str);
                        Toast.makeText(VideoCutter.this, "Error Creating Video", 0).show();
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }

                @Override public void onSuccess(String str) {
                    progressDialog.dismiss();
                    Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                    intent.setData(Uri.fromFile(new File(VideoCutter.this.n)));
                    VideoCutter.this.sendBroadcast(intent);
                    VideoCutter.this.b();
                }

                @Override public void onProgress(String str) {
                    Log.d("ffmpegResponse", str);
                    StringBuilder sb = new StringBuilder();
                    sb.append("progress : ");
                    sb.append(str);
                    progressDialog.setMessage(sb.toString());
                }

                @Override public void onStart() {
                    progressDialog.setMessage("Processing...");
                }

                @Override public void onFinish() {
                    progressDialog.dismiss();
                    VideoCutter.this.refreshGallery(str);
                }
            });
            getWindow().clearFlags(16);
        } catch (FFmpegCommandAlreadyRunningException unused) {
        }
    }

    private void e() {
        this.j.setOnPreparedListener(new OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
                VideoCutter.this.i.setSeekBarChangeListener(new SeekBarChangeListener() {
                    public void SeekBarValueChanged(int i, int i2) {
                        if (VideoCutter.this.i.getSelectedThumb() == 1) {
                            VideoCutter.this.j.seekTo(VideoCutter.this.i.getLeftProgress());
                        }
                        VideoCutter.this.d.setText(VideoJoinerActivity.formatTimeUnit((long) i));
                        VideoCutter.this.e.setText(VideoJoinerActivity.formatTimeUnit((long) i2));
                        VideoCutter.this.o.setStart(i);
                        VideoCutter.this.o.setStop(i2);
                        VideoCutter.this.c = i / 1000;
                        VideoCutter.this.b = i2 / 1000;
                        TextView textView = VideoCutter.this.g;
                        StringBuilder sb = new StringBuilder();
                        sb.append("duration : ");
                        sb.append(String.format("%02d:%02d:%02d", new Object[]{Integer.valueOf((VideoCutter.this.b - VideoCutter.this.c) / 3600), Integer.valueOf(((VideoCutter.this.b - VideoCutter.this.c) % 3600) / 60), Integer.valueOf((VideoCutter.this.b - VideoCutter.this.c) % 60)}));
                        textView.setText(sb.toString());
                    }
                });
                VideoCutter.this.i.setMaxValue(mediaPlayer.getDuration());
                VideoCutter.this.i.setLeftProgress(0);
                VideoCutter.this.i.setRightProgress(mediaPlayer.getDuration());
                VideoCutter.this.i.setProgressMinDiff(0);
            }
        });
    }

    private void f() {
        if (this.j.isPlaying()) {
            this.j.pause();
            this.i.setSliceBlocked(false);
            this.h.setBackgroundResource(R.drawable.play2);
            this.i.removeVideoStatusThumb();
            return;
        }
        this.j.seekTo(this.i.getLeftProgress());
        this.j.start();
        this.i.videoPlayingProgress(this.i.getLeftProgress());
        this.h.setBackgroundResource(R.drawable.pause2);
        this.p.a();
    }

    @Override public void onClick(View view) {
        if (view == this.h) {
            f();
        }
    }

    public void onMediaScannerConnected() {
        this.a.scanFile(this.l, "video/*");
    }

    public void onScanCompleted(String str, Uri uri) {
        this.a.disconnect();
    }


    @Override public void onResume() {
        super.onResume();
    }

    private void g() {
        try {
            this.fFmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override public void onFailure() {
                    VideoCutter.this.h();
                    Log.d("ffmpeg loading failed! ", "");
                }

                @Override public void onFinish() {
                    Log.d("ffmpeg loading finish! ", "");
                }

                @Override public void onStart() {
                    Log.d("ffmpeg loading started!", "");
                }

                @Override public void onSuccess() {
                    Log.d("ffmpeg loading success!", "");
                }
            });
        } catch (FFmpegNotSupportedException unused) {
            h();
        }
    }


    public void h() {
        new AlertDialog.Builder(this).setIcon(17301543).setTitle("Device not supported").setMessage("FFmpeg is not supported on your device").setCancelable(false).setPositiveButton(17039370, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) {
                VideoCutter.this.finish();
            }
        }).create().show();
    }

    public void deleteFromGallery(String str) {
        String[] strArr = {"_id"};
        String[] strArr2 = {str};
        Uri uri = Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = getContentResolver();
        Cursor query = contentResolver.query(uri, strArr, "_data = ?", strArr2, null);
        if (query.moveToFirst()) {
            try {
                contentResolver.delete(ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, query.getLong(query.getColumnIndexOrThrow("_id"))), null, null);
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
            }
        } else {
            try {
                new File(str).delete();
                refreshGallery(str);
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
        query.close();
    }

    public void refreshGallery(String str) {
        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        intent.setData(Uri.fromFile(new File(str)));
        sendBroadcast(intent);
    }

    @Override public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, ListVideoAndMyAlbumActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
        finish();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_picker, menu);
        return k;
    }

   @Override public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            onBackPressed();
            return k;
        }
        if (menuItem.getItemId() == R.id.Done) {
            if (this.j.isPlaying()) {
                this.j.pause();
                this.h.setBackgroundResource(R.drawable.play2);
            }
            d();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
