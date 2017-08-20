package ru.ereke.appsalem;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class ErrorActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnRefuse;
    private Button btnBack;
    private EditText etPoliceNumberError;
    private RadioGroup rgGravity;
    private TextView tvChooseErr;
    private TextView tvErrorEmptyET;
    private String userCode1C;
    private String message;
    private String locationData;
    private int success;
    private String URL = "http://mybento.kz/inc/ajax/mobil_err_test.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // assign values
        Intent intent = getIntent();
        userCode1C = intent.getStringExtra("userCode1C");
        locationData = intent.getStringExtra("locationData");

        btnRefuse = (Button) findViewById(R.id.btnRefuse);
        btnRefuse.setOnClickListener(this);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        etPoliceNumberError = (EditText) findViewById(R.id.etPolicyNumberError);
        rgGravity = (RadioGroup) findViewById(R.id.rgGravity);

        tvChooseErr = (TextView) findViewById(R.id.tvChooseErr);
        tvErrorEmptyET = (TextView) findViewById(R.id.tvErrorEmptyET);
    }
    // когда какойто кнопка нажато
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRefuse:
                String err = "0";
                switch (rgGravity.getCheckedRadioButtonId()) {
                    case R.id.rbErrorCourier:
                        err = "1";
                        break;
                    case R.id.rbWrongData:
                        err = "2";
                        break;
                    case R.id.rbAlreadyBought:
                        err = "3";
                        break;
                    case R.id.rbNotAvailable:
                        err = "4";
                        break;
                    case R.id.rbDoNotOrdered:
                        err = "5";
                        break;
                }
                if (etPoliceNumberError.getText().toString().length() == 0) {
                    tvErrorEmptyET.setText("Введите номер полиса!");
                }
                 else if (err.equals("0")) {
                        tvErrorEmptyET.setText("");
                        tvChooseErr.setText("Выберите один из причин!");
                } else {
                    Toast.makeText(ErrorActivity.this, "Обработка...", Toast.LENGTH_SHORT).show();
                    UrlMaker urlMaker = new UrlMaker();
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("code1C", userCode1C);
                    params.put("BSO", etPoliceNumberError.getText().toString());
                    params.put("err", err);
                    params.put("location", locationData);
                    String url = urlMaker.getUrl(URL, params);
                    new JSONTask().execute(url);
                }
                break;
            case R.id.btnBack:
                finish();
                break;
        }
    }

    // http парсер Передаем и получаем данные сайта
    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                Log.d("My-log", url.toString());
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                JSONObject dataJsonObj = new JSONObject(buffer.toString());
                success = dataJsonObj.getInt("success");
                message = dataJsonObj.getString("message");
                Log.d("My-log", locationData);
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        // После парсинга обрабатываем ответ
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String status = "Нет соединения с интернетом!";
            if (success > 1) {
                status = message;
            } else if (success == 1) {
                status = "Принято!";
            }
            tvChooseErr.setText("");
            tvErrorEmptyET.setText("");
            AlertDialog.Builder builder = new AlertDialog.Builder(ErrorActivity.this);
            builder.setTitle(status)
                    .setCancelable(false)
                    .setPositiveButton("ОК",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
