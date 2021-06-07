package com.example.eva1_12_clima;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

   /* Clima[] aClimaCd = {
            new Clima(R.drawable.sunny, "Chihuahua", 28, "Despejado con viento"),
            new Clima(R.drawable.atmospher, "Delicias", 15, "Vientos huracanados"),
            new Clima(R.drawable.cloudy, "Camargo", 22.3, "Nublado con probabilidad de lluvia"),
            new Clima(R.drawable.light_rain, "Casas Grandes", 15, "Lluvia ligera"),
            new Clima(R.drawable.rainy, "Parral", 11, "Lluvioso con tormentas eléctricas"),
            new Clima(R.drawable.snow, "Cuahutemoc", -3, "Nieve"),
            new Clima(R.drawable.thunderstorm, "Madera", 24, "Tormentas fuertes"),
            new Clima(R.drawable.tornado, "Guerrero", 17, "Run like hell"),
            new Clima(R.drawable.sunny, "Creel", 12, "A todo dar"),
            new Clima(R.drawable.light_rain, "Ahumada", 13, "Pal cafecito"),

    };*/
    List<Clima> lstCiudades = new ArrayList<>();
    ListView lstVwClima;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lstVwClima = findViewById(R.id.lstVwClima);
        //lstVwClima.setAdapter(new ClimaAdaptador(this,R.layout.mi_lista_clima, aClimaCd));
        ConexionClima cc = new ConexionClima();
        cc.execute("http://api.openweathermap.org/data/2.5/find?lat=28.6&lon=-106&cnt=30&units=metric&appid=72b08f5fb2a3fcc414e049bef6ecbf1d");
    }
                                            //URL, NADA, JSON(STRING)
    class ConexionClima extends AsyncTask<String,Void,String>{
        //Aqui vamos a hacer la conexion (trabajo en segundo plano)
        @Override
        protected String doInBackground(String... strings) {
            String sUrl = strings[0];
            String sResu = null;
        //HttpUrlConnection
            try {
                URL url = new URL(sUrl);
                //Aqui se realiza la conexión
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                //Aqui verificamos si la conexion fue exitosa
                if(httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                    //Aqui es como leer un archivo de texto
                    InputStreamReader isReader = new InputStreamReader(httpURLConnection.getInputStream());
                    BufferedReader brDatos = new BufferedReader(isReader);
                    sResu = brDatos.readLine();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sResu;
        }

        //Aqui vamos a llenar la lista con datos
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(!(s.equals("")|| s == null)){ //Verificar que tengamos una respuesta
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    //Recuperar el arreglo de ciudades
                    JSONArray jsaCiudades = jsonObject.getJSONArray("list");
                    for(int i = 0; i < jsaCiudades.length(); i++){
                        JSONObject jsonCiudad = jsaCiudades.getJSONObject(i);
                        //Leer cada ciudad
                        Clima climaCiudad = new Clima();
                        climaCiudad.setCiudad(jsonCiudad.getString("name"));
                        JSONObject jsonMain = jsonCiudad.getJSONObject("main");
                        climaCiudad.setTemp(jsonMain.getDouble("temp"));
                        JSONArray jsaWeather = jsonCiudad.getJSONArray("weather");
                        //Tomamos el primer elemento
                        JSONObject jsonClimaActual = jsaWeather.getJSONObject(0);
                        climaCiudad.setDesc(jsonClimaActual.getString("description"));
                        int id = jsonClimaActual.getInt("id");
                        if(id < 300){ //Lluvia
                            climaCiudad.setImagen(R.drawable.thunderstorm);
                        }else if (id < 400){ //Lluvia ligera
                            climaCiudad.setImagen(R.drawable.light_rain);
                        }else if (id < 600){ //Lluvia ligera
                            climaCiudad.setImagen(R.drawable.rainy);
                        }else if (id < 700){ //Lluvia ligera
                            climaCiudad.setImagen(R.drawable.snow);
                        }else if (id < 801){ //Lluvia ligera
                            climaCiudad.setImagen(R.drawable.sunny);
                        }else if (id < 900){ //despejado
                            climaCiudad.setImagen(R.drawable.cloudy);
                        }else{ //nublado
                            climaCiudad.setImagen(R.drawable.tornado);
                        }
                        lstCiudades.add(climaCiudad);
                    }
                    lstVwClima.setAdapter(new ClimaAdaptador(MainActivity.this,
                            R.layout.mi_lista_clima, lstCiudades));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}