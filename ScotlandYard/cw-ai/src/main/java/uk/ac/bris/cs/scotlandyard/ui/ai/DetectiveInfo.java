package uk.ac.bris.cs.scotlandyard.ui.ai;

import uk.ac.bris.cs.scotlandyard.model.*;

public class DetectiveInfo {
    private Colour colour;
    private int location;

    public DetectiveInfo(Colour colour,int location){
        this.colour= colour;
        this.location = location;
    }

    public Colour getColour() {
        return colour;
    }

    public int getLocation() {
        return location;
    }

    public void setColour(Colour colour) {
        this.colour = colour;
    }

    public void setLocation(int location) {
        this.location = location;
    }
}

