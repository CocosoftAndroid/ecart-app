package com.cocosoft.ecart.listeners;

/**
 * Created by.dmin on 4/24/2017.
 */

public interface WishlistListener {
    void onFavouriteClicked(String productid,boolean isChecked);
    void onFavouriteClicked(String productid,String productname,Double price,boolean isChecked);
}
