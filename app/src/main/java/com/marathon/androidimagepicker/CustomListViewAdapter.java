package com.marathon.androidimagepicker;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class CustomListViewAdapter extends BaseAdapter {

    Activity activity;
    List<Uri> uris;
    public CustomListViewAdapter(Activity activity, List<Uri> imageUris) {
        this.activity = activity;
        this.uris = imageUris;
    }
    @Override
    public int getCount() {
        return uris.size();
    }

    @Override
    public Uri getItem(int position) {
        return uris.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = activity.getLayoutInflater().inflate(R.layout.list_view_image_show, null, false);
        ImageView imageView = view.findViewById(R.id.image);
        Uri uri = getItem(position);
        imageView.setImageURI(uri);

        //to get the selected image path
        String[] path = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(uri, path, null, null, null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(path[0]);
        String filepath = cursor.getString(index);
        TextView filePath = view.findViewById(R.id.filePath);
        filePath.setText(filepath);
        //To Get the file from path (in order to save or send it in API, or get Name or other file function)
        File  file = new File(filepath);
        TextView name = view.findViewById(R.id.fileName);
        name.setText(file.getName());

        return view;
    }
}
