package ru.ereke.appsalem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity {
    private EditText editTextCodeC1;
    private Button buttonLogin;
    private int success = 0;
    private String message = "";
    private TextView tvErrCode1C;
    private String URL = "http://mybento.kz/inc/ajax/mobil_polis_test.php";
    private SharedPreferences sharedPreferences;
    private String SAVED_TEXT = "saved user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        editTextCodeC1 = (EditText) findViewById(R.id.etCodeC1);
        buttonLogin = (Button) findViewById(R.id.btnLogin);
        tvErrCode1C = (TextView) findViewById(R.id.tvErrCode1C);
        // проверяем выполнен ли вход до этого
        String CODE1C = getCode1C();
        if (CODE1C.length() > 0) {
            String name = getUserName();
            goToWellcomeAct(name);
        }
        // когда какойто кнопка нажато
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Обработка...",Toast.LENGTH_LONG ).show();
                UrlMaker urlMaker = new UrlMaker();
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("code1C", editTextCodeC1.getText().toString());
                String url = urlMaker.getUrl(URL, params);
                new JSONTask().execute(url);
            }
        });

    }
    // Парсер Get запрос
    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
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
                if (success == 1) {
                    String userName = dataJsonObj.getString("name");
                    saveData(userName);
                    goToWellcomeAct(userName);
                }
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
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            tvErrCode1C.setText("Проверте код 1С!");
        }
    }

    // Сохраняет данные на Preference
    private void saveData(String userName) {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SAVED_TEXT, editTextCodeC1.getText().toString());
        editor.putString("user name", userName);
        editor.commit();
    }
    // Получает Код 1С от Preference
    private String getCode1C() {
        String savedCode1C = "";
        sharedPreferences = getPreferences(MODE_PRIVATE);
        savedCode1C = sharedPreferences.getString(SAVED_TEXT, "");
        return savedCode1C;
    }
    // Получает Имя от Preference
    private String getUserName() {
        String name = "";
        sharedPreferences = getPreferences(MODE_PRIVATE);
        name = sharedPreferences.getString("user name", "");
        return name;
    }
    // отправляет на WellcomeActivity
    private void goToWellcomeAct(String name) {
        Intent intent;
        intent = new Intent(MainActivity.this , WellcomeActivity.class);
        intent.putExtra("userName", name);
        intent.putExtra("userCode1C", getCode1C());
        startActivity(intent);
        finish();
    }
}
