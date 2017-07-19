package com.cocosoft.ecart.adminmodule;
/**
 * Created by.dmin on 3/20/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cocosoft.ecart.R;
import com.cocosoft.ecart.loginmodule.User;

import java.util.ArrayList;


public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.MyViewHolders> {


    private ArrayList<User> productList;
    private Context context;

    public class MyViewHolders extends RecyclerView.ViewHolder {
        public TextView userName;
        public TextView userEmail;
        public LinearLayout rootView;
        public MyViewHolders(View view) {
            super(view);
            userName = (TextView) view.findViewById(R.id.name_txt);
            userEmail = (TextView) view.findViewById(R.id.email_txt);
            rootView = (LinearLayout) view.findViewById(R.id.rootview);
        }
    }


    public AllUsersAdapter(Context c, ArrayList<User> list) {
        this.productList = list;
        this.context = c;

    }


    @Override
    public MyViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_allusers_item, parent, false);

        return new MyViewHolders(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolders holder, final int position) {
        if(position % 2 == 0)
        {
            //holder.rootView.setBackgroundColor(Color.BLACK);
            holder.rootView.setBackgroundResource(R.color.grey_e4);
        }
        else
        {
            //holder.rootView.setBackgroundColor(Color.WHITE);
            holder.rootView.setBackgroundResource(R.color.white);
        }
        holder.userName.setText(productList.get(position).getFirstName()+" "+productList.get(position).getLastName());
        holder.userEmail.setText(productList.get(position).getEmail());

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}