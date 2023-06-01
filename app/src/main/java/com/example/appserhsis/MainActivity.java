package com.example.appserhsis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnLogin= findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etUsername = findViewById(R.id.edtEmail);
                EditText etPassword = findViewById(R.id.edtPass);


                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                String credentials = username + ":" + password;
                String base64Credentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                String basicAuth = "Basic " + base64Credentials;

                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), "");

                Request request = new Request.Builder()
                        .url("http://serhsi-rh-env.eba-jqr5fnb9.us-east-1.elasticbeanstalk.com/auth")
                        .post(requestBody)
                        .header("Authorization", basicAuth)
                        .header("Content-Type", "application/json")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        // Manejar error de conexión
                        Log.d("OkHttp", "onFailure: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseData = response.body().string();
                            Log.d("OkHttp", "Solicitud exitosa. Datos de respuesta: " + responseData);

                            try {
                                JSONArray jsonArray = new JSONArray(responseData);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);

                                String idUsuario = jsonObject.getString("idUsuario");

                                Intent intent = new Intent(MainActivity.this, Lista.class);
                                intent.putExtra("idUsuario", idUsuario);
                                startActivity(intent);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        } else {
                            Log.d("OkHttp", "La solicitud falló. Código de respuesta: " + response.code());
                        }
                    }
                });
            }
        });
    }
}