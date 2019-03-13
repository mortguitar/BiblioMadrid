package com.mortega.bibliomadrid.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mortega.bibliomadrid.R;
import com.mortega.bibliomadrid.base.Centro;
import com.mortega.bibliomadrid.base.Opinion;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;

public class OpinionActivity extends AppCompatActivity {

    private ArrayList<Opinion> listaOpiniones = new ArrayList<>();
    private final String URL_SERVIDOR = "http://10.0.2.2:8082";

    private EditText opinionText;
    private TextView nombreBiblioteca;
    private RatingBar valoracion;
    private Button enviarB;

    private String cadena;
    private int puntuacion;
    private String nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        nombre = intent.getStringExtra("lugar");

        setContentView(R.layout.activity_opinion);

        nombreBiblioteca = (TextView) findViewById(R.id.textView);
        nombreBiblioteca.setText(nombre);

        opinionText = (EditText) findViewById(R.id.opinionText);
        valoracion = (RatingBar) findViewById(R.id.ratingBar);

        enviarB = (Button) findViewById(R.id.enviarB);
        enviarB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cadena = String.valueOf(opinionText);
                puntuacion = valoracion.getNumStars();
                new SubirDatos();
                onBackPressed();
            }
        });
    }

    class SubirDatos extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... strings) {

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.getForObject(URL_SERVIDOR + "/add_opinion?titulo=" + nombre + "&texto=" + cadena +
                    "&puntuacion=" + puntuacion, Void.class);

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
}
