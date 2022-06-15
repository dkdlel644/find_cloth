package com.example.clothfinder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ProductSelectActivity extends AppCompatActivity {
    private GridView gridView = null;
    private GridViewAdapter adapter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_page);

        Uri targetImageUri = getIntent().getParcelableExtra("targetImageUri");
        ImageView targetImageView = (ImageView) findViewById(R.id.targetImage);

        Glide.with(getApplicationContext()).load(targetImageUri).override(500).into(targetImageView);

        gridView = (GridView) findViewById((R.id.gridView));
        adapter = new GridViewAdapter();

        ArrayList<Product> products = (ArrayList<Product>) getIntent().getSerializableExtra("products");
        
        for (int i = 0; i < products.size(); i++) {
            adapter.addItem(products.get(i));
        }

        gridView.setAdapter(adapter);
    }

    class GridViewAdapter extends BaseAdapter {
        ArrayList<Product> items = new ArrayList<Product>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(Product item) {
            items.add(item);
        }

        @Override
        public Product getItem(int pos) {
            return items.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup viewGroup) {
            final Context context = viewGroup.getContext();
            final Product product = items.get(pos);

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.gridview_list_item, viewGroup, false);

                ImageView itemImageView = (ImageView) convertView.findViewById(R.id.item_image);
                TextView itemNameView = (TextView) convertView.findViewById(R.id.item_name);
                TextView itemPriceView = (TextView) convertView.findViewById(R.id.item_price);

                Glide.with(convertView).load(product.getImageUri()).override(500).into(itemImageView);
                itemNameView.setText(product.getName());
                itemPriceView.setText(Integer.toString(product.getPrice()) + "원");
            } else {
                View view = new View(context);
                view = (View) convertView;
            }

            //각 아이템 선택 event
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, product.getLinkUri());
                    startActivity(intent);
                }
            });

            return convertView;
        }
    }
}
