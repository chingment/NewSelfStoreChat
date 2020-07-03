package com.hyphenate.chatuidemo.fanju.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.fanju.model.ProductSkuBean;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductSkuAdapter extends BaseAdapter {

    private static final String TAG = "ProductSkuAdapter";
    private Context context;
    private List<ProductSkuBean> items = new ArrayList<>();

    public ProductSkuAdapter(Context context, List<ProductSkuBean> items) {
        this.context = context;
        this.items = items;
    }


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.fanju_item_list_sku_tmp1, parent, false);
        }


        final ProductSkuBean item = items.get(position);

        final ImageView img_main =convertView.findViewById( R.id.img_main);
        TextView txt_name = convertView.findViewById( R.id.txt_name);



        txt_name.setText(item.getName());

        loadImageFromUrl(context, img_main, item.getMainImgUrl());


        convertView.setClickable(false);


        return convertView;
    }


    private   void loadImageFromUrl(Context context, final ImageView photoView, String imageUrl) {

        Picasso.with(context).load(imageUrl)
                .placeholder(R.drawable.fanju_default_image).fit().centerInside()
                .into(photoView, new Callback() {
                    @Override
                    public void onSuccess() {

                        photoView.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onError() {
                        photoView.setBackgroundResource(R.drawable.fanju_default_image);
                    }
                });
    }
}
