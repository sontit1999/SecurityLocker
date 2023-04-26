package com.ls.entertainment.securitylocker.model;

import android.content.Context;

import java.util.List;

public class PackagesInfo {
    List appList;

    public PackagesInfo(Context context) {
        this.appList = context.getApplicationContext().getPackageManager().getInstalledApplications(0);
    }

}
