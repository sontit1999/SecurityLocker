package com.ls.entertainment.securitylocker.model;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.CheckBox;

public class TaskInfo implements Parcelable {
    public static final Creator<TaskInfo> CREATOR = new Creator<TaskInfo>() {
        public TaskInfo createFromParcel(Parcel parcel) {
            return new TaskInfo(parcel);
        }

        public TaskInfo[] newArray(int i) {
            return new TaskInfo[i];
        }
    };
    ApplicationInfo appoints;
    boolean chucked;
    CheckBox chkTask;
    long mem;
    int pid;
    PackagesInfo pkgInfo;
    PackageManager pm;
    ActivityManager.RunningAppProcessInfo runinfo;
    String title;

    public TaskInfo() {
    }

    public TaskInfo(Context context, ActivityManager.RunningAppProcessInfo runningAppProcessInfo) {
        this.appoints = null;
        this.pkgInfo = null;
        this.title = null;
        this.runinfo = runningAppProcessInfo;
        this.pm = context.getApplicationContext().getPackageManager();
    }

    public TaskInfo(Context context, ApplicationInfo applicationInfo) {
        this.appoints = null;
        this.pkgInfo = null;
        this.runinfo = null;
        this.title = null;
        this.appoints = applicationInfo;
        this.pm = context.getApplicationContext().getPackageManager();
    }

    protected TaskInfo(Parcel parcel) {
        this.appoints = (ApplicationInfo) parcel.readParcelable(ApplicationInfo.class.getClassLoader());
        this.mem = parcel.readLong();
        this.runinfo = (ActivityManager.RunningAppProcessInfo) parcel.readParcelable(ActivityManager.RunningAppProcessInfo.class.getClassLoader());
        this.title = parcel.readString();
        this.chucked = parcel.readByte() != 0;
        this.pid = parcel.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public int getIcon() {
        return this.appoints.icon;
    }

    public String getPackageName() {
        return this.appoints.packageName;
    }

    public String getTitle() {
        if (this.title == null) {
            try {
                this.title = this.appoints.loadLabel(this.pm).toString();
            } catch (Exception unused) {
                Log.e("fff", "");
            }
        }
        return this.title;
    }

    public long getMem() {
        return this.mem;
    }

    public void setMem(long j) {
        this.mem = j;
    }

    public boolean isGoodProcess() {
        return this.appoints != null;
    }

    public ApplicationInfo getAppinfo() {
        return this.appoints;
    }

    public void setAppinfo(ApplicationInfo applicationInfo) {
        this.appoints = applicationInfo;
    }

    public PackagesInfo getPkgInfo() {
        return this.pkgInfo;
    }

    public void setPkgInfo(PackagesInfo packagesInfo) {
        this.pkgInfo = packagesInfo;
    }

    public ActivityManager.RunningAppProcessInfo getRuninfo() {
        return this.runinfo;
    }

    public void setRuninfo(ActivityManager.RunningAppProcessInfo runningAppProcessInfo) {
        this.runinfo = runningAppProcessInfo;
    }

    public boolean isChceked() {
        return this.chucked;
    }

    public void setChceked(boolean z) {
        this.chucked = z;
    }

    public void setChkTask(CheckBox checkBox) {
        this.chkTask = checkBox;
    }

    public int getPid() {
        return this.pid;
    }

    public void setPid(int i) {
        this.pid = i;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.appoints, i);
        parcel.writeLong(this.mem);
        parcel.writeParcelable(this.runinfo, i);
        parcel.writeString(this.title);
        parcel.writeByte(this.chucked ? (byte) 1 : 0);
        parcel.writeInt(this.pid);
    }
}
