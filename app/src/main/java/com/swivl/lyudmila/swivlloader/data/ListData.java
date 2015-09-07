package com.swivl.lyudmila.swivlloader.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lyudmila on 06.09.2015.
 */
public class ListData implements Parcelable{

    public String urlAvatar;
    public String login;
    public String urlGit;

    public ListData(String urlAvatar, String login, String hrefGit) {
        this.urlAvatar = urlAvatar;
        this.login = login;
        this.urlGit = hrefGit;
    }

    private ListData(Parcel parcel) {
        urlAvatar = parcel.readString();
        login = parcel.readString();
        urlGit = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(urlAvatar);
        parcel.writeString(login);
        parcel.writeString(urlGit);
    }

    public static final Creator<ListData> CREATOR = new Creator<ListData>() {
        public ListData createFromParcel(Parcel in) {
            return new ListData(in);
        }
        public ListData[] newArray(int size) {
            return new ListData[size];
        }
    };
}
