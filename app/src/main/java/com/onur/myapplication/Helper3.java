package com.onur.myapplication;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Helper3 {


    float[] dizi = new float[1910];
    public Helper3() throws IOException {


                String path = Environment.getExternalStorageDirectory().toString()+"/yaprakornek";
                Log.d("Files", "Path: " + path);
                System.out.println("yol==="+path);
                File directory = new File(path);
                File[] files = directory.listFiles();
                System.out.println("directory = "+directory);
                Log.d("Files", "Size: "+ files.length);
                for (int i = 0; i < files.length; i++)
                {

                    Log.d("Files", "FileName:" + files[i].getName());

                    Mat frame = Imgcodecs.imread(files[i].getPath(), 1);
                    Mat grayImage = new Mat();
                    Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);

                    grayImage.convertTo(grayImage, CvType.CV_8U);
                    if(Imgcodecs.imwrite("/storage/emulated/0/detectedGrayImage.jpg", grayImage))
                    {
                        System.out.println(i+".edge is detected .......");
                    }

                    Bitmap bmp = null;

                    Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_RGB2BGRA);
                    bmp = Bitmap.createBitmap(grayImage.cols(), grayImage.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(grayImage, bmp);


                    histogramLbp(bmp,i);
                }

                for (int i=0 ; i<dizi.length ; i++){
                    System.out.println(i+". değer = "+dizi[i]);

                }



    }

    public int getPixel(Bitmap img,int center,int x , int y){

        int new_value = 0;
        try{
            if(img.getPixel(x,y) >=center){
                new_value = 1;
            }}catch (Exception e){
            System.out.println(e);
        }
        return new_value;


    }
    public double lbpCalculatedPixel(Bitmap img,int x, int y){

        int center = img.getPixel(x,y);

        ArrayList<Integer> val_ar = new ArrayList<Integer>();


        val_ar.add(getPixel(img,center,x-1,y+1));
        val_ar.add(getPixel(img,center,x,y+1));
        val_ar.add(getPixel(img,center,x+1,y+1));
        val_ar.add(getPixel(img,center,x+1,y));
        val_ar.add(getPixel(img,center,x+1,y-1));
        val_ar.add(getPixel(img,center,x,y-1));
        val_ar.add(getPixel(img,center,x-1,y-1));
        val_ar.add(getPixel(img,center,x-1,y));

        int[] power_val = {1,2,4,8,16,32,64,128};

        double value = 0;

        for (int i = 0 ; i <val_ar.size();i++){

            value+= val_ar.get(i) * power_val[i];
        }

        return value;

    }



    public void histogramLbp(Bitmap grayImg,int sayac) throws IOException {


        Mat img_lbp  = new Mat();
        Utils.bitmapToMat(grayImg, img_lbp);

        Imgproc.cvtColor(img_lbp, img_lbp, Imgproc.COLOR_RGB2BGRA);

        double [][] asd = new double[grayImg.getWidth()][grayImg.getHeight()];

        float toplam = 0;
        System.out.println("toplam = " + toplam);

        for(int i = 0 ; i < grayImg.getWidth() ; i ++) {

            for (int j = 0; j < grayImg.getHeight(); j++) {
                asd[i][j] = lbpCalculatedPixel(grayImg,i,j);
                toplam += asd[i][j]/(grayImg.getHeight()*grayImg.getWidth());

                //img_lbp.put(i,j,lbpCalculatedPixel(grayImg,i,j));
                //img_lbp.put(i,j,asd[i][j]);

            }
            System.out.println("toplam = " + toplam);

        }


        System.out.println("toplam son = " + toplam);
        dizi[sayac] = toplam;
        if (sayac == 1905){
            // Open the file
            FileInputStream fstream = new FileInputStream("/storage/emulated/0/deneme.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            FileWriter writer = new FileWriter("/storage/emulated/0/yeni.txt");
            String strLine;

            //Read File Line By Line
            int sayac2=0;
            while ((strLine = br.readLine()) != null)   {
                writer.write(dizi[sayac2]+",");
                writer.write(strLine);
                writer.write(System.getProperty( "line.separator" ));
                sayac2++;
            }

            //Close the input stream
            fstream.close();
            writer.close();
        }

    }

}
