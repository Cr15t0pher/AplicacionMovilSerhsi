package com.example.appserhsis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.appserhsis.databinding.ActivityDetailedBinding;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetailedActivity extends AppCompatActivity {

    private OkHttpClient client = new OkHttpClient();
    ActivityDetailedBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = this.getIntent();
        if (intent != null){
            String nombre = intent.getStringExtra("nombre");
            String costo = intent.getStringExtra("costo");
            String refacciones = intent.getStringExtra("refacciones");
            String descripcion = intent.getStringExtra("descripcion");
            String id = intent.getStringExtra("id");
            binding.btnAceptar.setTag(id);
            binding.btnCancelar.setTag(id);
            int image = intent.getIntExtra("image", R.drawable.cotizacionlogi);
            binding.detalleNombre.setText(nombre);
            binding.detalleCosto.setText(costo);
            binding.detalleRefacciones.setText(refacciones);
            binding.detalleDescripcion.setText(descripcion);
            binding.detalleImage.setImageResource(image);
        }
    }

    public void onAceptarClick(View view) {
        String id = (String) view.getTag();
        aceptarCotizacion(id);
    }

    // Método para manejar el clic en el botón "Cancelar"
    public void onCancelarClick(View view) {
        String id = (String) view.getTag();
        cancelarCotizacion(id);
    }

    private void aceptarCotizacion(String id) {
        String apiUrl = "http://serhsi-rh-env.eba-jqr5fnb9.us-east-1.elasticbeanstalk.com/cotizaciones/" + id + "/aceptar";
        Request request = new Request.Builder()
                .url(apiUrl)
                .put(RequestBody.create(null, new byte[0]))
                .build();

        executeRequest(request);
    }

    private void cancelarCotizacion(String id) {
        String apiUrl = "http://serhsi-rh-env.eba-jqr5fnb9.us-east-1.elasticbeanstalk.com/cotizaciones/" + id;
        Request request = new Request.Builder()
                .url(apiUrl)
                .delete()
                .build();

        executeRequest(request);
    }

    private void executeRequest(Request request) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("OkHttp", "Solicitud exitosa. Datos de respuesta: " + response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }
        });
    }
}