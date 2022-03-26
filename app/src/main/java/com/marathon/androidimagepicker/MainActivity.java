package com.marathon.androidimagepicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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
        if(requestCode == 101 && resultCode== RESULT_OK) {
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
        }
    }
}