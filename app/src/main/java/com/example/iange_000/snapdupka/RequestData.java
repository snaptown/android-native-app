package com.example.iange_000.snapdupka;

import android.graphics.Bitmap;
import android.location.Location;

/**
 * Created by iange_000 on 21-Jul-15.
 */
public class RequestData {
    private Bitmap photo;
    private String comment;
   private Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public RequestData(Bitmap photo, String comment, Location location) {
        this.photo = photo;
        this.comment = comment;
        this.location = location;

    }

    @Override
    public String toString() {
        return "RequestData{" +
                "photo=" + photo +
                ", comment='" + comment + '\'' +
                ", location=" + location +
                '}';
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


}
