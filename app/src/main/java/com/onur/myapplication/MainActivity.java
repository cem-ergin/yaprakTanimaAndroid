package com.onur.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    static{
        if(OpenCVLoader.initDebug()){
            Log.d(TAG,"Opencv basariyla yuklendi");


        }
        else{
            Log.d(TAG,"Yuklenemedi");
        }

    }
    //Helper3 asdf = new Helper3();


    private ImageView imageview;
    private Button btnSelectImage;
    private Bitmap bitmap;
    private File destination = null;
    private InputStream inputStreamImg;
    private String imgPath = null;
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;

    public MainActivity() throws IOException {
    }

    private void selectImage() {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Fotoğraf Çek", "Galeriden Seç","İptal"};
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                builder.setTitle("Seçim");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Fotoğraf Çek")) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, PICK_IMAGE_CAMERA);
                        } else if (options[item].equals("Galeriden Seç")) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                        } else if (options[item].equals("İptal")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else
                Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        inputStreamImg = null;
        if (requestCode == PICK_IMAGE_CAMERA) {
            try {
                Uri selectedImage = data.getData();
                bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                Log.e("Activity", "Pick from Camera::>>> ");
                destination = new File("/storage/emulated/0/Camera.jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final View alertDialog= getLayoutInflater().inflate(R.layout.custom_dialog, null);
                ImageView imageView= (ImageView) alertDialog
                        .findViewById(R.id.selectedImage);
                imageView.setImageBitmap(bitmap);

                AlertDialog.Builder alertadd = new AlertDialog.Builder(this);

                alertadd.setView(alertDialog);
                alertadd.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Helper helper = new Helper(destination.getPath(),MainActivity.this,"deneme");

                    }
                });
                alertadd.setNegativeButton("No", null);
                alertadd.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE_GALLERY) {
            try {
                Uri selectedImage = data.getData();
                imgPath = getRealPathFromURI(selectedImage);
                System.out.println(imgPath);

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());



                final View alertDialog= getLayoutInflater().inflate(R.layout.custom_dialog, null);
                ImageView imageView= (ImageView) alertDialog
                        .findViewById(R.id.selectedImage);
                imageView.setImageBitmap(bitmap);

                AlertDialog.Builder alertadd = new AlertDialog.Builder(this);

                alertadd.setView(alertDialog);
                alertadd.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Helper helper = new Helper(imgPath,MainActivity.this,"deneme");

                    }
                });
                alertadd.setNegativeButton("No", null);
                alertadd.show();



            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView title = (TextView) findViewById(R.id.title);

        TextView first = (TextView) findViewById(R.id.first);
        TextView second = (TextView) findViewById(R.id.second);
        TextView third = (TextView) findViewById(R.id.third);
        title.setVisibility(View.GONE);

        first.setVisibility(View.GONE);
        second.setVisibility(View.GONE);
        third.setVisibility(View.GONE);

//        Button egitim = findViewById(R.id.egitim);
//        Button egitimTest = findViewById(R.id.egitimTest);

        FloatingActionButton fab = findViewById(R.id.fab);

        Button egitimTest = findViewById(R.id.egitimTest);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectImage();


            }
        });


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            egitimTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ArrayList<String> allImages = new ArrayList<>();
                    ArrayList<String> testImages = new ArrayList<>();
                    ArrayList<String> trainImages = new ArrayList<>();
                    float[] maxOzellik= new float[256];


                    try(BufferedReader br = new BufferedReader(new FileReader("/storage/emulated/0/dusukcozunurluklbpson.txt"))) {
                        int sayac=0;
                        for(String line; (line = br.readLine()) != null; ) {
                            allImages.add(line);
                            trainImages.add(line);
                            String[] maxParse = line.split(",");
                            System.out.println("aha bu maxparse sayısı :"+maxParse.length);
                            for(int i = 0;i<256;i++){
                                if(maxOzellik[i]<Float.parseFloat(maxParse[i])){
                                    maxOzellik[i] = Float.parseFloat(maxParse[i]);
                                }
                            }
                        }
                        // line is not visible here.
                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                    for(int i = 0 ; i < (allImages.size()*0.2);i++){
                        int rnd = (int)(Math.random()* allImages.size());
                        testImages.add(allImages.get(rnd));
                        trainImages.remove(allImages.get(rnd));
                    }

                    System.out.println(testImages.size());
                    System.out.println(trainImages.size());

                    int dogruSayisi = 0;
                    int yanlisSayisi = 0;

                    float[] uzakliklar = new float[trainImages.size()];
                    float[] uzakliklar2 = new float[trainImages.size()];
                    for(int x = 0 ; x < testImages.size() ; x++ ) {

                        String[] parse = testImages.get(x).split(",");
                        for (int j = 0; j < trainImages.size(); j++) {
                            float hesapla = 0;
                            String[] parse2 = trainImages.get(j).split(",");
                            for (int i = 0; i < parse.length - 1; i++) {
                                float a = Float.parseFloat(parse2[i])/maxOzellik[i];
                                float b = Float.parseFloat(parse[i])/maxOzellik[i];
                                hesapla += (Math.pow((a - b), 2));

                            }
                            uzakliklar[j] = hesapla;
                            uzakliklar2[j] = hesapla;
                        }
                        Arrays.sort(uzakliklar2);
                        int[] siniflandirmaSonuc = new int[3];
                        System.out.println("Siniflandirilan = " + testImages.get(x));
                        for(int i = 0 ; i < 3 ; i ++ ){
                            System.out.println("index = " + findIndex(uzakliklar,uzakliklar2[i]));
                            System.out.println("line = " +trainImages.get(findIndex(uzakliklar,uzakliklar2[i])));
                            String[] parseSonuc = trainImages.get(findIndex(uzakliklar,uzakliklar2[i])).split(",");
                            String[] parseSonuc2 = parseSonuc[256].split("\\.");
                            siniflandirmaSonuc[i] = Integer.parseInt(parseSonuc2[0]);
                        }
                        String[] siniflandirmaImage = parse[256].split("\\.");
                        int siniflandirmaResim = Integer.parseInt(siniflandirmaImage[0]);
                        if(siniflandirmaKontrol(siniflandirmaResim,siniflandirmaSonuc)==1){
                            dogruSayisi++;
                        }else{
                            yanlisSayisi++;
                        }
                        System.out.println("Dogru Sayisi = " + dogruSayisi);
                        System.out.println("Yanlis Sayisi = " + yanlisSayisi);
                        System.out.println("================================================================");

                    }
                    //float accuracy = (float)(TP+FP)/(TP+TN+FP+FN);
                    //float presicion = (float)(TP)/(TP+FP);
                    //float recall = (float)(TP)/(TP+FN);
                    //float fScore = (float)(2*presicion*recall)/(presicion+recall);
                    System.out.println("Doğruluk Oranı = " +(float)(((float)dogruSayisi/(float)testImages.size())*100));

                    TextView title = findViewById(R.id.title);
                    title.setText("Doğruluk Oranı = " + (float)(((float)dogruSayisi/(float)testImages.size())*100));
                    TextView first = findViewById(R.id.first);
                    first.setText("Test Kümesindeki Veri Sayısı = " + (dogruSayisi + yanlisSayisi) +"\nDogru Sınıflandırma Sayısı = " + dogruSayisi +"\nYanlış Sınıflandırma Sayısı = " + yanlisSayisi);
                    title.setVisibility(View.VISIBLE);
                    first.setVisibility(View.VISIBLE);
                    TextView second = findViewById(R.id.second);
                    TextView third = findViewById(R.id.third);
                    second.setVisibility(View.GONE);
                    third.setVisibility(View.GONE);
                    ImageView firstImage = findViewById(R.id.firstImage);
                    ImageView secondImage = findViewById(R.id.secondImage);
                    ImageView thirdImage = findViewById(R.id.thirdImage);
                    firstImage.setVisibility(View.GONE);
                    secondImage.setVisibility(View.GONE);
                    thirdImage.setVisibility(View.GONE);


                    //tv.setText("Dogru Sayisi = " + dogruSayisi +"\nYanlis Sayisi = " + yanlisSayisi+"\nDogruluk orani = " +(float)(((float)dogruSayisi/(float)testImages.size())*100));


                }
            });
        }




//        egitim.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                Helper helper = new Helper("/storage/emulated/0/yaprakOrnek/1001.jpg",MainActivity.this,"1001");
////                Helper helper2 = new Helper("/storage/emulated/0/yaprakOrnek/1002.jpg",MainActivity.this,"1002");
//
//                String path = Environment.getExternalStorageDirectory().toString()+"/yaprakOrnek";
//                Log.d("Files", "Path: " + path);
//                File directory = new File(path);
//                File[] files = directory.listFiles();
//                Log.d("Files", "Size: "+ files.length);
//                for (int i = 0; i < files.length; i++)
//                {
//                    Log.d("Files", "FileName:" + files[i].getName());
//                    Helper2 helper = new Helper2(path +"/"+ files[i].getName(),MainActivity.this,files[i].getName());
//
//                }
//
//            }
//        });



    }
    public int siniflandirmaKontrol(int line, int[] uzunluklar){
        float puan = 0;

        if(line<=1059){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]<1059){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>1059&&line<=1122){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>1059&&uzunluklar[i]<=1122){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>1122&&line<=1194){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>1122&&uzunluklar[i]<=1194){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>1194&&line<=1267){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>1194&&uzunluklar[i]<=1267){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>1267&&line<=1323){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>1267&&uzunluklar[i]<=1323){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>1323&&line<=1385){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>1323&&uzunluklar[i]<=1385){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>1385&&line<=1437){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>1385&&uzunluklar[i]<=1437){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>1437&&line<=1496){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>1437&&uzunluklar[i]<=1496){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>1496&&line<=1551){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>1496&&uzunluklar[i]<=1551){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>1551&&line<=1616){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>1551&&uzunluklar[i]<=1616){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>2000&&line<=2050){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>2000&&uzunluklar[i]<=2050){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>2050&&line<=2113){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>2050&&uzunluklar[i]<=2113){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>2113&&line<=2165){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>2113&&uzunluklar[i]<=2165){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>2165&&line<=2230){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>2165&&uzunluklar[i]<=2230){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>2230&&line<=2290){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>2230&&uzunluklar[i]<=2290){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>2290&&line<=2346){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>2290&&uzunluklar[i]<=2346){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>2346&&line<=2423){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>2346&&uzunluklar[i]<=2423){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>2423&&line<=2485){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>2423&&uzunluklar[i]<=2485){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>2485&&line<=2546){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>2485&&uzunluklar[i]<=2546){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>2546&&line<=2612){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>2546&&uzunluklar[i]<=2612){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>2615&&line<=2675){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>2615&&uzunluklar[i]<=2675){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>3000&&line<=3055){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>3000&&uzunluklar[i]<=3055){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>3055&&line<=3110){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>3055&&uzunluklar[i]<=3110){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>3110&&line<=3175){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>3110&&uzunluklar[i]<=3175){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>3175&&line<=3229){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>3175&&uzunluklar[i]<=3229){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>3229&&line<=3281){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>3229&&uzunluklar[i]<=3281){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>3281&&line<=3334){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>3281&&uzunluklar[i]<=3334){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>3334&&line<=3389){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>3334&&uzunluklar[i]<=3389){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>3389&&line<=3446){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>3389&&uzunluklar[i]<=3446){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>3446&&line<=3510){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>3446&&uzunluklar[i]<=3510){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>3510&&line<=3563){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>3510&&uzunluklar[i]<=3563){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        if(line>3565&&line<=3621){
            for(int i = 0 ; i < uzunluklar.length ; i ++){
                if(uzunluklar[i]>3565&&uzunluklar[i]<=3621){
                    puan++;
                }
            }
            if((2*puan)/uzunluklar.length>1){
                System.out.println("Dogru siniflandirildi, puan = " + puan);
                return 1;
            }else{
                System.out.println("Yanlis siniflandirildi, puan = " + puan);
                return 0;
            }
        }
        return 0;




    }
    public static int findIndex(float arr[], float t)
    {

        // if array is Null
        if (arr == null) {
            return -1;
        }

        // find length of array
        int len = arr.length;
        int i = 0;

        // traverse in the array
        while (i < len) {

            // if the i-th element is t
            // then return the index
            if (arr[i] == t) {
                return i;
            }
            else {
                i = i + 1;
            }
        }
        return -1;
    }

}


