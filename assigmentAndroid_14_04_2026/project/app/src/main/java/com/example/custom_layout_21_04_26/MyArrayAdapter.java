package com.example.custom_layout_21_04_26;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MyArrayAdapter extends ArrayAdapter<Phone> {
    Activity context;
    int idlayout;
    ArrayList<Phone> mylist;

    // Constructor để MainActivity truyền các tham số vào
    public MyArrayAdapter(Activity context, int idlayout, ArrayList<Phone> mylist) {
        super(context, idlayout, mylist);
        this.context = context;
        this.idlayout = idlayout;
        this.mylist = mylist;
    }

    // Override hàm getView để custom lại layout cho ListView
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater myInflater = context.getLayoutInflater();
        convertView = myInflater.inflate(idlayout, null);

        // Lấy đối tượng Phone ở vị trí position
        Phone myphone = mylist.get(position);

        // 1. Gán ảnh điện thoại vào ImageView
        ImageView imgphone = convertView.findViewById(R.id.imgphone);
        imgphone.setImageResource(myphone.getImagephone());

        // 2. Gán tên điện thoại vào TextView
        TextView txtnamephone = convertView.findViewById(R.id.txtnamephone);
        txtnamephone.setText(myphone.getNamephone());

        // 3. Gán giá bán vào TextView (PHẦN THÊM MỚI cho bài tập)
        TextView txtprice = convertView.findViewById(R.id.txtprice);
        txtprice.setText(myphone.getPrice());

        return convertView;
    }
}
