package ru.ereke.appsalem;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VideoActivity extends AppCompatActivity {

    private Button mRecordView;
    private Button mPlayView;
    private Button btnSendVideo;
    private VideoView mVideoView;
    private int ACTIVITY_START_CAMERA_APP = 0;
    private String URL = "http://mybento.kz/inc/ajax/mobil_ok_test.php";
    private String keyCode1C = "code1C";
    private String userCode1C;
    private String videoStr;
    private String keyVideo = "video";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        mRecordView = (Button) findViewById(R.id.btnRecord);
        mPlayView = (Button) findViewById(R.id.btnPlay);
        btnSendVideo = (Button) findViewById(R.id.btnSendVideo);
        mVideoView = (VideoView) findViewById(R.id.videoView);
        Intent intent = getIntent();
        userCode1C = intent.getStringExtra("userCode1C");

        // записать видео
        mRecordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callVideoAppIntent = new Intent();
                callVideoAppIntent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);

                startActivityForResult(callVideoAppIntent, ACTIVITY_START_CAMERA_APP);
            }
        });


        // воспроизвести видео
        mPlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoView.start();
            }
        });

        btnSendVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(VideoActivity.this, "Отправка...", Toast.LENGTH_SHORT);
                toast.show();
                sendVideo();
            }
        });
    }


    // конвертируем видео на String
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();
            mVideoView.setVideoURI(videoUri);
            String videoPath = getRealPathFromURI(videoUri);
            try {
                byte[] videoBytes = convertVideoToBytes(videoPath);
                Log.d("video size =>", videoBytes.length + "");
                videoStr = new String(videoBytes, "UTF-8");  // Best way to decode using "UTF-8"

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // берем URL сохраненного видео
    public String getRealPathFromURI (Uri contentUri) {
        String path = null;
        String[] data = { MediaStore.MediaColumns.DATA };
        Cursor cursor = getContentResolver().query(contentUri, data, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(column_index);
        }
        cursor.close();
        return path;
    }

    // конвертируем видео на byte
    public byte[] convertVideoToBytes(String path) throws IOException {
        Log.d("path->", path);
        FileInputStream fis = new FileInputStream(path);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        for (int readNum; (readNum = fis.read(b)) != -1;) {
            bos.write(b, 0, readNum);
        }
        byte[] bytes = bos.toByteArray();
        return bytes;
    }

    // http парсер используя volley
    private void sendVideo() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject dataJsonObj = new JSONObject(response);
                            String message = dataJsonObj.getString("message");
                            if (message.contains("NoConn")) {
                                message = "Нет соединения с интернетом!";
                            }
                            // Диалог успешно отправлен
                            AlertDialog.Builder builder = new AlertDialog.Builder(VideoActivity.this);
                            builder.setTitle(message)
                                    .setCancelable(false)
                                    .setPositiveButton("ОК",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                            AlertDialog alert = builder.create();
                            alert.show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(VideoActivity.this,error.toString(),Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override


            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put(keyCode1C, userCode1C);
                map.put(keyVideo, videoStr);

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
