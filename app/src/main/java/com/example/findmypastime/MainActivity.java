package com.example.findmypastime;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import java.io.FileDescriptor;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 42;
    private static final String TAG = "MainActivity";
    private TextView nbVisagesDetectes;
    private int nbVisages;
    private ImageView imageView;
    private Button  trouver_jeux;
    private Button btn_camera;
    private Button btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        imageView = findViewById(R.id.imgview);


        this.nbVisagesDetectes = (TextView) findViewById(R.id.nbVisages);
        this.trouver_jeux = findViewById(R.id.trouver_jeux);

        //Une fois les visages détéctés, affiche un bouton qui ammene l'utilisateur à l'autre activité
        trouver_jeux.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                Intent otherActivity = new Intent(getApplicationContext(), GameListActivity.class);
                otherActivity.putExtra("message", Integer.toString(nbVisages));
                startActivity(otherActivity);
                finish();
            }
        });


        prendrePhoto();
        recupPhoto();

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        Uri uri = null;
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    getBitmapFromUriYes(uri);
                } catch (IOException e) {
//
                }
            }
        }
        if(requestCode == 100 && resultCode == Activity.RESULT_OK ){
            Bitmap captureImage = (Bitmap) resultData.getExtras().get("data");
            uri = resultData.getData();
            try {
                getBitmapFromUriYes(uri);
            } catch (IOException e) {
//
            }
            //Prend l'image récupérée et la met dans le placeHolder
            imageView.setImageBitmap(captureImage);
        }

    }

    private void prendrePhoto() {
        //Demander la persmission pour la caméra
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    }, 100);
        }
        /*Ouvre la caméra*/
        this.btn_camera = findViewById(R.id.btn_camera);
        btn_camera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(intent, 100);
            }
        });
    }

    private void recupPhoto() {
        //Recupère une image depuis la galerie
        this.btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");

                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void showImage(FileDescriptor fileDescriptor) {
        //Charge l'image
        ImageView myImageView = findViewById(R.id.imgview);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable=true;

        //créer un objet Paint pour dessiner
        Bitmap myBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);

        Paint myRectPaint = new Paint();
        myRectPaint.setStrokeWidth(5);
        myRectPaint.setColor(Color.RED);
        myRectPaint.setStyle(Paint.Style.STROKE);

        //créer un canvas sur lequel dessiner
        Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(),
                myBitmap.getHeight(),
                Bitmap.Config.RGB_565);

        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(myBitmap, 0, 0, null);

        //Créer la detection faciale
        FaceDetector faceDetector = new FaceDetector
                .Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .build();

        //Vérifie que la détéction de visage est bien disponible
        if(!faceDetector.isOperational()){
            new AlertDialog
                    .Builder(getApplicationContext())
                    .setMessage("L'api faceDetector n'a pas pu être installée correctement !")
                    .show();
            return;
        }

        //detecte faces
        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);

        for(int i=0; i<faces.size(); i++) {
            Face thisFace = faces.valueAt(i);
            float x1 = thisFace.getPosition().x;
            float y1 = thisFace.getPosition().y;
            float x2 = x1 + thisFace.getWidth();
            float y2 = y1 + thisFace.getHeight();
            tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
        }
        myImageView.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));

        if(faces.size() >= 0 ){

            nbVisagesDetectes.setText("Nombre de visages détéctés : " + faces.size());
            btn.setText("Importer une autre image");
            btn_camera.setText("Prendre une autre photo");
            nbVisagesDetectes.setVisibility(View.VISIBLE);
            trouver_jeux.setVisibility(View.VISIBLE);
            if(faces.size() == 0){
                trouver_jeux.setVisibility(View.INVISIBLE);
                nbVisagesDetectes.setText("Aucun visage détécté ! veuillez prendre une autre photo." );
                if(faces.size() == 1) {
                    trouver_jeux.setText("Améliorer ma soirée !");
                    nbVisagesDetectes.setText("Nombre de visage détécté : " + faces.size());
                }

            }
        }
        setNbVisages(faces.size());
    }

    private void getBitmapFromUriYes(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        showImage(fileDescriptor);

    }

    public void setNbVisages(int nbVisages) {
        this.nbVisages = nbVisages;
    }

}