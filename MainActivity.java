package com.example.imageviewer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    TextView name;
    String filename;
    String path;

    ListView listview ;
    ListViewAdapter adapter;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new ListViewAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview);
        listview.setAdapter(adapter);

        imageView = (ImageView)findViewById(R.id.imageView);
        name = (TextView)findViewById(R.id.name);

        path = "/storage/emulated/0/Download/Sampleimage/"; //파일 위치

//        getFilesDir();
//        openFileOutput();
//        getFileStreamPath();
//        getDir();

        File f = new File(path);
        File[] files = f.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {

                return pathname.getName().toLowerCase(Locale.US).endsWith("g");//확장자
            }

        });

        for (int i = 0; i <files.length; i++) 
	{

            filename = String.valueOf(files[i]);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

            try {

                ExifInterface[] exif = new ExifInterface[files.length];


                exif[i] = new ExifInterface(filename);

                showExif(exif[i]);

                String date = formatter.format(files[i].lastModified());

                File imgFile = new File(filename);
                if (imgFile.exists()) {
                    Bitmap src = BitmapFactory.decodeFile(filename);
                    Bitmap resized = Bitmap.createScaledBitmap(src, 150, 150, true);

                    BitmapFactory.Options option = getBitmapSize(files[i]);
                    String size =(option.outWidth)+" X "+(option.outHeight);
                    adapter.addItem(resized, (String)name.getText()+
                            "\n날짜: "+date+"\n이미지크기: "+ size);
                }

            } 
catch(IOException e){
                e.printStackTrace();
                Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
            }

        }

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position);

                Intent intent = new Intent(getApplicationContext(), Detaillist.class);
                intent.putExtra("filename", String.valueOf(path+
                        item.getTitle().substring(item.getTitle().indexOf(":")+2,item.getTitle().lastIndexOf("파")-1)+
                        "."+item.getTitle().substring(item.getTitle().indexOf("식")+3,item.getTitle().lastIndexOf("날")-1)));
                startActivity(intent);
            }
        });

    }

    private BitmapFactory.Options getBitmapSize(File a) 
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(a.getAbsolutePath(), options);
        return options;
    }

    private void showExif(ExifInterface exif) {
        String myAttribute = "파일이름: ";

        myAttribute += (filename.substring(filename.lastIndexOf("/")+1,filename.lastIndexOf("."))+"\n");
        myAttribute += "파일형식: " + filename.substring(filename.lastIndexOf(".")+1);
        name.setText(myAttribute);
    }

}
