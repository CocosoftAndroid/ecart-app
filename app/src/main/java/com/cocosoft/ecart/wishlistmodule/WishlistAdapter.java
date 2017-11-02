package com.cocosoft.ecart.wishlistmodule;
/**
 * Created by.dmin on 3/20/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cocosoft.ecart.R;
import com.cocosoft.ecart.listeners.CheckboxListener;
import com.cocosoft.ecart.listeners.WishlistListener;
import com.cocosoft.ecart.scanlistmodule.ProductItem;

import java.util.ArrayList;


public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.MyViewHolders> {


    private CheckboxListener checkboxListener;
    private WishlistListener wishlistListener;

    private ArrayList<ProductItem> productList;

    private Context context;

    public class MyViewHolders extends RecyclerView.ViewHolder {
        public TextView productName, productPrice,wproduct_desc;
        public ImageView  removeBtn,imageView;
        public CheckBox checkBox;




        public MyViewHolders(View view) {
            super(view);
            productName = (TextView) view.findViewById(R.id.name_txt);
            productPrice = (TextView) view.findViewById(R.id.price_txt);
            wproduct_desc=(TextView)view.findViewById(R.id.wproduct_desc);


            removeBtn = (ImageView) view.findViewById(R.id.remove_btn);
            checkBox = (CheckBox) view.findViewById(R.id.chk_box);
            imageView = (ImageView) view.findViewById(R.id.img_view);


        }
    }


    public WishlistAdapter(Context c, ArrayList<ProductItem> list,  WishlistListener wishlistLis,CheckboxListener checkListener) {
        this.productList = list;
        this.context = c;

        this.wishlistListener = wishlistLis;
        this.checkboxListener =checkListener;


    }

    @Override
    public MyViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_wishlist_item, parent, false);

        return new MyViewHolders(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolders holder, final int position) {

        holder.productName.setText(productList.get(position).getProductName());
        holder.productPrice.setText("$" + productList.get(position).getProductPrice()+"0");
        holder.wproduct_desc.setText(productList.get(position).getProductDesc());




        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wishlistListener.onFavouriteClicked(productList.get(position).getProductId(),productList.get(position).getProductName(),productList.get(position).getProductPrice(),false,productList.get(position).getProductDesc());
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkboxListener.onChecked(productList.get(position).getProductId(),isChecked)
                ;
            }
        });
        String[] splited = productList.get(position).getImageUrl().split("\\\\");
        Log.e("wishlist","http://54.68.141.32:8080/" + splited[splited.length - 1]);
        Glide.with(context).load("http://54.68.141.32:8080/name" + productList.get(position).getProductId()+".jpg")
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