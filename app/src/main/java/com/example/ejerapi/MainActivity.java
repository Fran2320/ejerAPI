package com.example.ejerapi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.ejerapi.databinding.ActivityMainBinding;

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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ArrayList<String> userList;
    ArrayAdapter<String> listAdapter;
    Handler mainHadler = new Handler();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeUserList();
        binding.buttonID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new fetchData().start();
            }
        });

    }

    private void initializeUserList() {

        userList = new ArrayList<>();
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,userList);
        binding.playerList.setAdapter(listAdapter);
    }

    class fetchData extends Thread{
        String data = " ";



        @Override
        public void run() {

            mainHadler.post(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Fletching data");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                }
            });
            try {
                URL url = new URL("https://api.npoint.io/a937fa4ee964833d44fd");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();  //para abrir l aconexion
                InputStream inputStream =  httpURLConnection.getInputStream();     //recibimos los datos
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));   //se le pasan los datos el lector
                String line;

                while((line = bufferedReader.readLine()) != null){    //se leen los datos linea a linea
                    data = data + line;
                }

                if(!data.isEmpty()){
                    JSONObject jsonObject = new JSONObject(data);  //meto los datos en un obejto de tipo JSON
                    JSONArray users =jsonObject.getJSONArray("Users");    //saco del JSON los datos que me interesan con el identificador correspondiente
                    userList.clear(); //para limpiar el array cada vez que pulse el boton
                    for(int i = 0;i<users.length();i++){

                        JSONObject names = users.getJSONObject(i);  //guardo en un json todos los nombres
                        String name = names.getString("name");
                        userList.add(name);
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mainHadler.post(new Runnable() {
                @Override
                public void run() {
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                    listAdapter.notifyDataSetChanged();
                    }
            });

        }
    }
}