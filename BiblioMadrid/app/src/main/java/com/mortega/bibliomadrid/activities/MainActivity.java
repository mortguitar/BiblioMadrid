package com.mortega.bibliomadrid.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mortega.bibliomadrid.R;
import com.mortega.bibliomadrid.adapters.CentroAdapter;
import com.mortega.bibliomadrid.base.Centro;
import com.mortega.bibliomadrid.util.Constantes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private List<Centro> centros;
    private CentroAdapter adapter;

    private FloatingActionButton configuracionFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        centros = new ArrayList<>();
        ListView lista = (ListView) findViewById(R.id.lvCentro);

        adapter = new CentroAdapter(
                this, R.layout.item_info, centros
        );

        lista.setAdapter(adapter);
        lista.setOnItemClickListener(this);

        configuracionFab = (FloatingActionButton) findViewById(R.id.configuracionFab);
        configuracionFab.setOnClickListener(this);

        DescargaDatos descarga = new DescargaDatos();
        descarga.execute(Constantes.URL);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Centro centro = centros.get(position);

        Intent intent = new Intent(this, MapaActivity.class);
        intent.putExtra("nombre", centro.getNombre());
        intent.putExtra("organization", centro.getOrganizacion());
        intent.putExtra("latitud", centro.getLatitud());
        intent.putExtra("longitud", centro.getLongitud());

        startActivity(intent);
    }

    @Override
    public void onClick(View view) {

        Intent intent = null;

        switch (view.getId()) {
            case R.id.configuracionFab:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            default: break;
        }
    }

    private class DescargaDatos extends AsyncTask<String, Void, Void> {

        private ProgressDialog dialog;
        private String resultado;

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conexion = (HttpURLConnection) url.openConnection();

                //Lee el fichero de datos y genera una cadena de texto como resultado
                BufferedReader lector = new BufferedReader(
                        new InputStreamReader(conexion.getInputStream())
                );
                StringBuilder SB = new StringBuilder();
                String linea = null;

                while ((linea = lector.readLine()) != null)
                    SB.append(linea + "\n");

                conexion.disconnect();
                lector.close();
                resultado = SB.toString();

                //TODO Parseo del JSON
                JSONObject json = new JSONObject(resultado);
                JSONArray jsonArray = json.getJSONArray("@graph");

                for (int i=0; i<jsonArray.length(); i++) {

                    try {
                        Centro centro = new Centro();

                        centro.setNombre(jsonArray.getJSONObject(i).getString("title"));
                        centro.setDireccion(jsonArray.getJSONObject(i).getJSONObject("address").getString("street-address"));
                        centro.setOrganizacion(jsonArray.getJSONObject(i).getJSONObject("organization").getString("organization-desc"));
                        centro.setFecha(new Date());
                        centro.setLatitud(jsonArray.getJSONObject(i).getJSONObject("location").getDouble("latitude"));
                        centro.setLongitud(jsonArray.getJSONObject(i).getJSONObject("location").getDouble("longitude"));

                        centros.add(centro);

                    } catch (JSONException jsone) {
                        jsone.printStackTrace();
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (JSONException jsone) {
                jsone.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            dialog = new ProgressDialog(MainActivity.this);
            dialog.setTitle(R.string.mensaje_cargando);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (dialog != null)
                dialog.dismiss();

            adapter.notifyDataSetChanged();
        }
    }



}
