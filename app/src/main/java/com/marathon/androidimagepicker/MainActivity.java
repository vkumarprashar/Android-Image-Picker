package com.marathon.androidimagepicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    final int GALLERY_CODE = 101;
    final int CAMERA_CODE = 102;

    final int CAMERA_PERMISSION = 200;
    final int STORAGE_PERMISSION = 201;
    Button uploadBtn;
    ListView listView;
    List<Uri> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uploadBtn = findViewById(R.id.uploadImageBtn);
        listView = findViewById(R.id.imageListView);

        uploadBtn.setOnClickListener(v -> {
            selectImage();
        });
    }

    private void selectImage() {
        String[] menu = {"Camera","Gallery","Cancel"};
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Image Picker");
        alertDialog.setItems(menu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (menu[which].equals("Camera")) {
                    Log.d("Camera", "onClick: Camera is selected");
                    openCamera();
                } else if (menu[which].equals("Gallery")) {
                    Log.d("Gallery", "onClick: Gallery is selected");
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(i,101);
                } else {
                    Log.d("Cancel", "onClick: Cancel is selected");
                    dialog.dismiss();
                }
            }
        });
        AlertDialog al = alertDialog.create();
        al.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Inside Result", "onActivityResult: Inside Activity result");
        if(requestCode == GALLERY_CODE && resultCode== RESULT_OK) {
            Uri uri = data.getData();
            Log.d("URI", "onActivityResult: " + uri);

            list.add(uri);

            CustomListViewAdapter arrayAdapter = new CustomListViewAdapter(this,  list);
            listView.setAdapter(arrayAdapter);
            //to get the selected image path
//            String[] path = {MediaStore.Images.Media.DATA};
//            Cursor cursor = getContentResolver().query(uri, path, null, null, null);
//            cursor.moveToFirst();
//            int index = cursor.getColumnIndex(path[0]);
//            String filepath = cursor.getString(index);
//            Log.d("Image Path", "onActivityResult: " + filepath);
        } else if (requestCode == CAMERA_CODE && resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                Uri uri = createImageUri(bitmap);
                list.add(uri);

                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();
                int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                String filepath = cursor.getString(index);
                cursor.close();
                Log.d("Image Path", "onActivityResult: " + filepath);


                CustomListViewAdapter customListViewAdapter = new CustomListViewAdapter(this, list);
                listView.setAdapter(customListViewAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Uri createImageUri(Bitmap bitmap) {
        try {
            String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + R.string.app_name + "/";
            Log.d("PATH", "createImageUri: " + fullPath);
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File photoFile = new File(fullPath, timeStamp + ".jpg");
            photoFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(photoFile);
//            BufferedOutputStream outputStream = new BufferedOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//            String imagePath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, UUID.randomUUID() + ",jpg", "");
            String imagePath = MediaStore.Images.Media.insertImage(getContentResolver(), photoFile.getAbsolutePath(), photoFile.getName(), photoFile.getName());

            outputStream.close();
            outputStream.flush();

            Log.d("URI", "createImageUri: " + imagePath);
            return Uri.parse(imagePath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Image cannot be saved");
        }

    }

    private void openCamera() {
        if (this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION);
        } else {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(i, CAMERA_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==CAMERA_CODE) {
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, CAMERA_CODE);
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }
}