package com.cocosoft.ecart.scanlistmodule;
/**
 * Created by.dmin on 3/20/2017.
 */

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cocosoft.ecart.R;
import com.cocosoft.ecart.listeners.CheckboxListener;

import java.util.ArrayList;


public class ScanListAdapter2 extends RecyclerView.Adapter<ScanListAdapter2.MyViewHolders> {


    private ArrayList<Product> productList;

    private Context context;
    private CheckboxListener listener;

    public class MyViewHolders extends RecyclerView.ViewHolder {

        public TextView productName, productPrice;
        public CardView cardview;

        public MyViewHolders(View view) {
            super(view);
            productName = (TextView) view.findViewById(R.id.name_txt);
            cardview = (CardView) view.findViewById(R.id.card_view);
            productPrice = (TextView) view.findViewById(R.id.price_txt);
        }
    }

    public ScanListAdapter2(Context c, ArrayList<Product> list, CheckboxListener listener) {
        this.productList = list;
        this.context = c;
        this.listener = listener;

    }


    @Override
    public MyViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_scanlist_item, parent, false);
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


    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

}
