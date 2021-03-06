/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vacuumCleaner.simulator.perception;

import vacuumCleaner.simulator.objects.Dirt;
import vacuumCleaner.simulator.objects.PhysicalObject;
import vacuumCleaner.simulator.State;
import java.awt.geom.Point2D;
import vacuumCleaner.simulator.shapes.Shape;

/**
 *
 * @author santi
 * This class returns the same as FourRayDistancePerception, but 
 * the rays are rotated as a function of the vacuum angle 
 */

public class TurnableFourRayDistancePerception extends Perception {

    double granularity = 0.1;
    double nearThreshold = 1;
    
    public TurnableFourRayDistancePerception(double g, double nt) {
        granularity = g;
        nearThreshold = nt;
    }


    public TurnableFourRayDistancePerception(double g, double nt, State s, PhysicalObject subject) throws Exception {
        granularity = g;
        nearThreshold = nt;
        // create a fake object to use for collision detection:
        Point2D.Double pos = new Point2D.Double(subject.getPosition().x,subject.getPosition().y);
        String feature[] = {"l","r","u","d"};
        Shape shape = subject.getShape();
        
        double offsx[] = {-1,1,0,0};
        double offsy[] = {0,0,-1,1};
        double pos_x = pos.x;
        double pos_y = pos.y;
        double ca = Math.cos(subject.getAngle()*Math.PI/180.0);
        double sa = Math.sin(subject.getAngle()*Math.PI/180.0);

        for(int i = 0;i<feature.length;i++) {
            double distance = 0;
            do{
                pos.x+=offsx[i]*ca*granularity - offsy[i]*sa*granularity;
                pos.y+=offsx[i]*sa*granularity + offsy[i]*ca*granularity;;
                if (s.collision(subject, pos, subject.getAngle())) {
                    PhysicalObject c = s.collisionWithObjects(subject, pos, 0);
                    if (distance<nearThreshold) values.put("d" + feature[i], 0);
                                           else values.put("d" + feature[i], 1);
                    if (c==null) {
                        values.put(feature[i], 0);
                        break;
                    } else {
                        if (c instanceof Dirt) {
                            values.put(feature[i], 1);
                            break;
                        } else {
                            values.put(feature[i], 0);
                            break;
                        }
                    }
                }
                distance+=granularity;
            }while(true);
            pos.x = pos_x;
            pos.y = pos_y;
        }
    }


    @Override
    public Perception perceive(State s, PhysicalObject subject) {
        try {
            return new TurnableFourRayDistancePerception(granularity, nearThreshold, s, subject);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
