package farthestpairfinder;

import javax.swing.JFrame;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.ArrayList;

public class FarthestPairFinder extends JFrame {

     int pointSize = 6;
     int numPoints = 500;
     
     Point2D[] S = new Point2D[ numPoints ]; //the set S
     Point2D[] farthestPair = new Point2D[ 2 ]; //the two points of the farthest pair

     
     ArrayList<Point2D> convexHull = new ArrayList(); //the vertices of the convex hull of S
     
     Color convexHullColour = Color.white;
     Color genericColour = Color.yellow;

    
    //fills S with random points
    public void makeRandomPoints() {
        Random rand = new Random();
 
        for (int i = 0; i < numPoints; i++) {
            int x = 50 + rand.nextInt(700);
            int y = 50 + rand.nextInt(700);
            S[i] = new Point2D( x, y );            
        }        
    }

    
    public void paint(Graphics g) {        
        Image img = createImage();
        g.drawImage(img, 8, 30, this);
        
    }
    
    public Image createImage(){
        BufferedImage bufferedImage = new BufferedImage(800,800, BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.getGraphics();
        
        //draw random points
        g.setColor(genericColour);
        for (int i = 0; i < S.length; i++){
            g.fillOval((int)S[i].x - pointSize/2,(int)S[i].y - pointSize/2 ,pointSize, pointSize);
        }
        //colour convexHull points
        g.setColor(convexHullColour);
        for (int i = 0; i < convexHull.size(); i++ ){
            g.fillOval((int)convexHull.get(i).x - pointSize/2,(int)convexHull.get(i).y - pointSize/2,pointSize, pointSize);
        } 
        
        //draw convexHull lines
        g.setColor(convexHullColour);
        for (int i = 0; i <convexHull.size() - 1; i++){
            g.drawLine((int)convexHull.get(i).x, (int)convexHull.get(i).y, 
                    (int)convexHull.get(i + 1).x, (int)convexHull.get(i + 1).y);
        }
        //draw last line in convexHull
        g.drawLine((int)convexHull.get(0).x,(int)convexHull.get(0).y, 
                (int) convexHull.get(convexHull.size() -1).x,(int) convexHull.get(convexHull.size() -1).y);
        
        //draw farthest pair line using efficient method
        g.setColor(Color.red);        
        g.drawLine((int)farthestPair[0].x, (int)farthestPair[0].y, (int)farthestPair[1].x, (int)farthestPair[1].y);

        
        return bufferedImage;
    }
    //finds the leftmost x point and puts it in the front of the array
    public void sort(Point2D [] a){
        double lowestX = a[0].x;
        int index = 0;
        Point2D temp;
        for (int i = 1; i < a.length; i ++){
            if (a[i].x < lowestX){
                lowestX = a[i].x;
                index = i;
            }
        }
        temp = a[index];        
        a[index] = a[0];
        a[0] = temp;
    }
    //finds the convex hull
    public void findConvexHull() {
        //uses the leftmost point and draws the first vector to compare to
        sort(S);
        Point2D current = S[0];
        Vector currentVector = new Vector (-1,0);//pointing negative x direction
        
        boolean backToBeginning = false;
        while (backToBeginning == false){
            
            int minIndex = -1;
            double minAngle = 7;
            Vector minVector = null;
            
            //finds the angle between the vectors of each point and the vector that is being compared
            for (int i = 0; i < numPoints; i++){
                Point2D nextPoint = S[i];
                Vector nextVector = nextPoint.subtract(current);
                double Angle = currentVector.getAngle(nextVector);
                
                //if the point is not itself and it has found a smaller angle update the minimum angle
                if (nextPoint.equals(current) != true && minAngle > Angle ){
                    minAngle = Angle;
                    minIndex = i;
                    minVector = nextVector;
                }
            }
            //add the point to the array of convexHull points
            convexHull.add (S[minIndex]);
            currentVector = minVector;
            current = S[minIndex];
            if (minIndex == 0){
                backToBeginning = true;
            }
        }
    }
    //checks a fixed point's distances to the other points in the convex hull and then 
    //once the farthes length is found, fixes the found point and the fixed point moves around
    public void findFarthestPair_EfficientWay() {
        int aIndex = 0;
        int bIndex = convexHull.size()/2;
        farthestPair[0] = convexHull.get(aIndex);
        farthestPair[1] = convexHull.get(bIndex);
        double currMax = length(convexHull.get(aIndex), convexHull.get(bIndex));
        boolean switched = true;
        int count = 0;
        while (switched == true){
            count ++;
            switched = false;
            double blengthRight = length(convexHull.get(aIndex), convexHull.get(toTheRight(bIndex)));
            double blengthLeft = length(convexHull.get(aIndex), convexHull.get(toTheLeft(bIndex)));

            if (currMax < blengthRight){
                currMax = blengthRight;
                bIndex = toTheRight(bIndex);
                farthestPair[1] = convexHull.get(bIndex);
                switched = true;
            }
            else if (currMax < blengthLeft){
                currMax = blengthLeft;
                bIndex = toTheLeft(bIndex);
                farthestPair[1] = convexHull.get(bIndex);
                switched = true;
            }
            double alengthRight = length(convexHull.get(toTheRight(aIndex)), convexHull.get(bIndex));
            double alengthLeft = length(convexHull.get(toTheLeft(aIndex)), convexHull.get(bIndex));
            if (currMax < alengthRight){
                currMax = alengthRight;
                aIndex = toTheRight(aIndex);
                farthestPair[0] = convexHull.get(aIndex);
                switched = true;
            }
            else if (currMax < alengthLeft){
                currMax = alengthLeft;
                aIndex = toTheLeft(aIndex);
                farthestPair[0] = convexHull.get(aIndex);
                switched = true;
            }
       
        }
        System.out.println(count);
    }
    //gets the index of the point to the right
    public int toTheRight(int Index){
        if (Index + 1 >= convexHull.size()){
            return 0;
        }
        else{
            return Index + 1;
        }
    }
    //gets the index of the point to the left
    public int toTheLeft(int Index){
        if (Index - 1 < 0 ){
            return convexHull.size()-1;
        }
        else{
            return Index - 1;
        }
    }
    //find length of two points
    public double length(Point2D a, Point2D b){
        return Math.sqrt(Math.pow(b.x -a.x, 2) + Math.pow(b.y - a.y, 2));
    }
    
    public void findFarthestPair_BruteForceWay() {
        //code this just for fun, to see how many more distance calculations and comparisons it does than the efficient way
        double maxLength = -1;
        int count = 0;
        for (int i = 1; i < S.length; i++){
            for(int j = 1; j < S.length; j ++){
                count ++;
                if (maxLength < length(S[i],S[j])){
                    maxLength = length(S[i],S[j]);
                    farthestPair[0] = S[i];
                    farthestPair[1] = S[j];
                }
            }
        }
        System.out.println(count);
    }
    
   
    public static void main(String[] args) {

        //no changes are needed in main().  Just code the blank methods above.
        
        FarthestPairFinder fpf = new FarthestPairFinder();
        
        fpf.setBackground(Color.BLACK);
        fpf.setSize(800, 800);
        fpf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        fpf.makeRandomPoints();
        
        fpf.findConvexHull();
        
        fpf.findFarthestPair_EfficientWay();
        fpf.findFarthestPair_BruteForceWay();
        
        fpf.setVisible(true); 
    }
}
