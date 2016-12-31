package ie.wit.gareth.travelsnap.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Gareth on 31/01/2015.
 */
@ParseClassName("UserPhotos")
public class UserPhoto extends ParseObject {


    private String title;
    private ParseGeoPoint point;
    private String address;
    private ParseFile file;
    private ParseFile iconImage;
    private ParseUser user;

    public UserPhoto(){

    }

    public UserPhoto(String title, ParseGeoPoint point, String address, ParseFile file, ParseUser user, ParseFile iconImage){
        this.title = title;
        this.point = point;
        this.address = address;
        this.file = file;
        this.user = user;
        this.iconImage = iconImage;


    }

    public void uploadPhoto(){

        put("title", title);
        put("location", point);
        put("city", address);
        put("photo", file);
        put("user", user);
        put("icon", iconImage);
        saveInBackground();

}

    public String getTitle() {
        return getString("title");
    }

    public String getCity() {
        return getString("city");
    }

    public void setPhotoBytes(byte[] photo){
        put("photo", photo);
    }

    public void setIconBytes(byte[] icon){
        put("icon", icon);
    }

    public ParseFile getPhoto() {
        return getParseFile("photo");
    }


    public void setTitle(String title) {
        put("title", title);
    }

    public void setPoint(ParseGeoPoint p) {
        put("location", p);
    }

    public void setPointLocal(ParseGeoPoint point) {
        put("location", point);
    }

    public void setIconImage(ParseFile icon) {
        put("icon", icon);
    }

    public void setPhoto(ParseFile file){
        put("photo", file);
    }

    public void setCity(String city){
        put("city", city);
    }

    public void setUser(ParseUser user){
        put("user", user);
    }

    public void setLocalID(String id){
        put("localID", id);
    }

    public void setLocalDate(String date){
        put("localDate", date);
    }




    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



    public void setFile(ParseFile file) {
        this.file = file;
    }

    public ParseUser getUser() {
        return user;
    }




}
