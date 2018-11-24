package farthestpairfinder;

import java.awt.*;

public class Point2D {
    
    double x, y;
    boolean visited; //might need this in the convex hull finding algorithm
    Color color;
    
    public Point2D(int x, int y) {
        this.x = x;
        this.y = y;
        this.visited = false;
        this.color = Color.yellow;
    }
    
    //Returns the vector that stretches between this and other.
    public Vector subtract( Point2D other ) {
        return new Vector( this.x - other.x, this.y - other.y);
    }
    
    public boolean equals (Point2D other){
        if (this.x == other.x && this.y == other.y){
            return true;
        }
        else{
            return false;
        }
    }
}
