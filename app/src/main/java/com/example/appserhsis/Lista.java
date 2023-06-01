package com.example.appserhsis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.appserhsis.databinding.ActivityListaBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Lista extends AppCompatActivity {
    ActivityListaBinding binding;

    ListAdapter listAdapter;
    ArrayList<ListData> dataArrayList = new ArrayList<>();
    ListData listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        actualizarLista();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {

            if (dataArrayList.size() == 1) {

                dataArrayList.clear();
                listAdapter.notifyDataSetChanged();
            } else {

                actualizarLista();
            }
        }
    }
    private void actualizarLista() {
        OkHttpClient client = new OkHttpClient();
        String idUsuario = getIntent().getStringExtra("idUsuario");
        String apiUrl = "http://serhsi-rh-env.eba-jqr5fnb9.us-east-1.elasticbeanstalk.com/clientes/" + idUsuario + "/cotizaciones/realizadas";
        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        ResponseBody responseBody = response.body();
                        if (responseBody != null) {
                            String responseData = responseBody.string();
                            JSONArray jsonArray = new JSONArray(responseData);
                            int image = R.drawable.cotizacionlogi;


                            dataArrayList.clear();


                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String idCotizacion = jsonObject.getString("idCotizacion");
                                String nombre = jsonObject.getString("marca") + ' ' + jsonObject.getString("modelo") + ' ' + jsonObject.getString("numeroSerie");
                                double costo = jsonObject.getDouble("costo");
                                String descripcion=jsonObject.getString("descripcion");
                                double iva = costo * 0.16;
                                double costoConIVA = costo + iva;
                                String costototal = String.format("%.2f", costoConIVA);


                                ListData listData = new ListData(idCotizacion, nombre,descripcion, costototal, null, image);
                                dataArrayList.add(listData);


                                String refaccionesUrl = "http://serhsi-rh-env.eba-jqr5fnb9.us-east-1.elasticbeanstalk.com/refacciones/" + idCotizacion;
                                Request refaccionesRequest = new Request.Builder()
                                        .url(refaccionesUrl)
                                        .build();

                                client.newCall(refaccionesRequest).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        e.printStackTrace();

                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        if (response.isSuccessful()) {
                                            try {
                                                ResponseBody refaccionesResponseBody = response.body();
                                                if (refaccionesResponseBody != null) {
                                                    String refaccionesResponseData = refaccionesResponseBody.string();
                                                    JSONArray refaccionesJsonArray = new JSONArray(refaccionesResponseData);


                                                    StringBuilder descripcionConcatenada = new StringBuilder();
                                                    for (int j = 0; j < refaccionesJsonArray.length(); j++) {
                                                        JSONObject refaccionJsonObject = refaccionesJsonArray.getJSONObject(j);
                                                        String cantidad = refaccionJsonObject.getString("cantidad");
                                                        String descripcionRefaccion = refaccionJsonObject.getString("descripcion");
                                                        String costoUnitario = '$' + refaccionJsonObject.getString("costoUnitario")+ " C/U" ;


                                                        descripcionConcatenada.append(cantidad);
                                                        descripcionConcatenada.append("  "); // Espacio adicional
                                                        descripcionConcatenada.append(descripcionRefaccion);
                                                        descripcionConcatenada.append("  "); // Espacio adicional
                                                        descripcionConcatenada.append(costoUnitario);
                                                        descripcionConcatenada.append("\n");
                                                    }


                                                    if (descripcionConcatenada.length() > 0) {

                                                        descripcionConcatenada.setLength(descripcionConcatenada.length() - 1);
                                                    }


                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            listData.setRefacciones(descripcionConcatenada.toString());


                                                            listAdapter.notifyDataSetChanged();
                                                        }
                                                    });
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();

                                            }
                                        } else {

                                        }
                                    }
                                });
                            }


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                } else {

                }
            }
        });

        listAdapter = new ListAdapter(Lista.this, dataArrayList);
        binding.listview.setAdapter(listAdapter);
        binding.listview.setClickable(true);
        binding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Manejar el evento de clic en un elemento de la lista
                Intent intent = new Intent(Lista.this, DetailedActivity.class);
                ListData selectedData = dataArrayList.get(i);
                intent.putExtra("id", selectedData.getId());
                intent.putExtra("nombre", selectedData.getNombre());
                intent.putExtra("costo", selectedData.getCosto());
                intent.putExtra("refacciones", selectedData.getRefacciones());
                intent.putExtra("descripcion",selectedData.getDescripcion());
                intent.putExtra("image", selectedData.getImage());
                startActivityForResult(intent, 1);
            }
        });
    }
}