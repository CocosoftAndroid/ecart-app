package com.cocosoft.ecart.listeners;

import org.json.JSONObject;

/**
 * Created by.dmin on 4/7/2017.
 */

public interface ScanResultListener {
    void onScanResult(JSONObject obj,int scantype);
}
