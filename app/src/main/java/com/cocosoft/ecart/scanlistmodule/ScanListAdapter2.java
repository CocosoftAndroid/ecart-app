package com.cocosoft.ecart.scanlistmodule;
/**
 * Created by.dmin on 3/20/2017.
 */

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cocosoft.ecart.R;
import com.cocosoft.ecart.listeners.CheckboxListener;

import java.util.ArrayList;


public class ScanListAdapter2 extends RecyclerView.Adapter<ScanListAdapter2.MyViewHolders> {


    private ArrayList<Product> productList;

    private Context context;
    private CheckboxListener listener;

    public class MyViewHolders extends RecyclerView.ViewHolder {

        public TextView productName, productPrice;
        public ImageView imageView;
        public CardView cardview;

        public MyViewHolders(View view) {
            super(view);
            productName = (TextView) view.findViewById(R.id.name_txt);
            cardview = (CardView) view.findViewById(R.id.card_view);
            productPrice = (TextView) view.findViewById(R.id.price_txt);
            imageView = (ImageView) view.findViewById(R.id.img_view);
        }
    }

    public ScanListAdapter2(Context c, ArrayList<Product> list, CheckboxListener listener) {
        this.productList = list;
        this.context = c;
        this.listener = listener;

    }


    @Override
    public MyViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_scanlist_item2, parent, false);
        return new MyViewHolders(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolders holder, final int position) {

        holder.productName.setText(productList.get(position).getProductName());
        holder.productPrice.setText("$ " + productList.get(position).getPrice());
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onChecked(productList.get(position).getProductId(), false);
            }
        });
        String[] splited = productList.get(position).getImgUrl().split("\\\\");
        Log.e("scanlist","http://54.68.141.32:8080/" + splited[splited.length - 1]);
        Glide.with(context).load("http://54.68.141.32:8080/" + splited[splited.length - 1])
                .thumbnail(0.5f)
                .crossFade()
                .placeholder(R.drawable.ic_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

}
