package ru.ereke.appsalem;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class DeliveredActivity extends AppCompatActivity  implements View.OnClickListener{

    private Button btnDelivered;
    private Button btnImgAdd1;
    private Button btnImgAdd2;
    private Button btnImgAdd3;
    private Button btnBack;
    private ImageView ivImage1;
    private ImageView ivImage2;
    private ImageView ivImage3;
    private static final int CAMERA_REQUEST1 = 1;
    private static final int CAMERA_REQUEST2 = 2;
    private static final int CAMERA_REQUEST3 = 3;
    private EditText etPolicyNumDelivery;
    private String locationData;
    private String userCode1C;
    private TextView tvFillPolicyN;
    private String URL = "http://mybento.kz/inc/ajax/mobil_ok_test.php";
    private String encodedImage1 = "not";
    private String encodedImage2 = "not";
    private String encodedImage3 = "not";
    private String keyCode1C = "code1C";
    private String keyBSO = "BSO";
    private String keyImg1 = "img1";
    private String keyImg2 = "img2";
    private String keyImg3 = "img3";
    private String keyLocation = "location";
    private TextView tvErrTakePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivered);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent intent = getIntent();
        userCode1C = intent.getStringExtra("userCode1C");
        locationData = intent.getStringExtra("locationData");
        btnBack = (Button) findViewById(R.id.btnBack);

        btnImgAdd1 = (Button) findViewById(R.id.btnImgAdd1);
        btnImgAdd2 = (Button) findViewById(R.id.btnImgAdd2);
        btnImgAdd3 = (Button) findViewById(R.id.btnImgAdd3);

        btnDelivered = (Button) findViewById(R.id.btnDelivered);
        etPolicyNumDelivery= (EditText) findViewById(R.id.etPolicyNumberDeliv);
        ivImage1 = (ImageView) findViewById(R.id.ivImage1);
        ivImage2 = (ImageView) findViewById(R.id.ivImage2);
        ivImage3 = (ImageView) findViewById(R.id.ivImage3);
        tvFillPolicyN = (TextView) findViewById(R.id.tvFillPolicyN);
        tvErrTakePhoto = (TextView) findViewById(R.id.tvErrTakePhoto);

        btnBack.setOnClickListener(this);
        btnDelivered.setOnClickListener(this);
        btnImgAdd1.setOnClickListener(this);
        btnImgAdd2.setOnClickListener(this);
        btnImgAdd3.setOnClickListener(this);
    }

    // когда кнопка нажато
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnImgAdd1:
                tvErrTakePhoto.setText("");
                Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent1, CAMERA_REQUEST1);
                break;
            case R.id.btnImgAdd2:
                tvErrTakePhoto.setText("");
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent2, CAMERA_REQUEST2);
                break;
            case R.id.btnImgAdd3:
                tvErrTakePhoto.setText("");
                Intent intent3 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent3, CAMERA_REQUEST3);
                break;
            case R.id.btnDelivered:
                if (etPolicyNumDelivery.getText().toString().length() == 0) {
                    Toast toast = Toast.makeText(DeliveredActivity.this, "Введите номер полиса!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                } else if ((encodedImage1.length() < 10 && encodedImage2.length() < 10 && encodedImage3.length() < 10)){
                    tvFillPolicyN.setText("");
                    Toast toast = Toast.makeText(DeliveredActivity.this, "Сфотографируйте полис!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                } else {
                    tvErrTakePhoto.setText("");
                    Toast.makeText(DeliveredActivity.this, "Обработка...", Toast.LENGTH_SHORT).show();
                    delivered();
                }
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_REQUEST1: {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Bitmap myBmp = (Bitmap) data.getExtras().get("data");
                    ivImage1.setImageBitmap(myBmp);
                    Bitmap bitmap = ((BitmapDrawable)ivImage1.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
                    byte[] image = stream.toByteArray();
                    Log.d("MyLog", "" + image.length);
                    encodedImage1 = Base64.encodeToString(image , Base64.DEFAULT);
                }
                break;
            }
            case CAMERA_REQUEST2: {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Bitmap myBmp = (Bitmap) data.getExtras().get("data");
                    ivImage2.setImageBitmap(myBmp);
                    Bitmap bitmap = ((BitmapDrawable)ivImage2.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
                    byte[] image = stream.toByteArray();
                    Log.d("MyLog", "" + image.length);
                    encodedImage2 = Base64.encodeToString(image , Base64.DEFAULT);
                }
                break;
            }
            case CAMERA_REQUEST3: {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Bitmap myBmp = (Bitmap) data.getExtras().get("data");
                    ivImage3.setImageBitmap(myBmp);
                    Bitmap bitmap = ((BitmapDrawable)ivImage3.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
                    byte[] image = stream.toByteArray();
                    Log.d("MyLog", "" + image.length);
                    encodedImage3 = Base64.encodeToString(image , Base64.DEFAULT);
                }
                break;
            }
        }
    }


    private void delivered() {
        final String userBSO = etPolicyNumDelivery.getText().toString();

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
                            AlertDialog.Builder builder = new AlertDialog.Builder(DeliveredActivity.this);
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
                        Toast.makeText(DeliveredActivity.this,error.toString(),Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override


            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put(keyCode1C, userCode1C);
                map.put(keyLocation, locationData);
                map.put(keyBSO, userBSO);
                map.put(keyImg1, encodedImage1);
                map.put(keyImg2, encodedImage2);
                map.put(keyImg3, encodedImage3);

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
