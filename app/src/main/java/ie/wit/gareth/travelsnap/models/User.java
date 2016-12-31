package ie.wit.gareth.travelsnap.models;

/**
 * Created by Gareth on 20/02/2015.
 */
public class User {

    private String email;
    private String name;
    private String password;
    private int imagesPosted;

    public User(int imagesPosted, String password, String name, String email) {
        this.imagesPosted = imagesPosted;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getImagesPosted() {
        return imagesPosted;
    }

    public void setImagesPosted(int imagesPosted) {
        this.imagesPosted = imagesPosted;
    }




}
