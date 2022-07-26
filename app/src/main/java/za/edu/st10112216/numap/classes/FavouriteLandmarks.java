package za.edu.st10112216.numap.classes;

public class FavouriteLandmarks {
    String name;
    String ID;

    public FavouriteLandmarks() {

    }

    public FavouriteLandmarks(String name, String ID) {
        this.name = name;
        this.ID = ID;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
