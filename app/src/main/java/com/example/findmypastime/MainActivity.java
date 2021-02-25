package com.example.findmypastime;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    //initialisation des variables
    ImageView imageView;
    Button btn_camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assigner les variables
        imageView = findViewById(R.id.imgAffichePhoto);
        btn_camera = findViewById(R.id.btn_camera);

        //Demander la persmission pour la caméra
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    }, 100);
        }
        btn_camera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Ouvre la caméra
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
startActivityForResult(intent, 100);
            }
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       if(requestCode == 100){
           //Recupère l'image capturée
           Bitmap captureImage = (Bitmap) data.getExtras().get("data");
           //Prend l'image récupérée et la met dans le placeHolder
           imageView.setImageBitmap(captureImage);

       }
    }
}