package ray1.surface;

import egl.math.Vector2d;
import ray1.IntersectionRecord;
import ray1.Ray;
import egl.math.Vector3;
import egl.math.Vector3d;

/**
 * Represents a sphere as a center and a radius.
 *
 * @author ags
 */
public class Sphere extends Surface {
  
  /** The center of the sphere. */
  protected final Vector3 center = new Vector3();
  public void setCenter(Vector3 center) { this.center.set(center); }
  
  /** The radius of the sphere. */
  protected float radius = 1.0f;
  public void setRadius(float radius) { this.radius = radius; }
  
  protected final double M_2PI = 2 * Math.PI;
  
  public Sphere() { }
  
  /**
   * Tests this surface for intersection with ray. If an intersection is found
   * record is filled out with the information about the intersection and the
   * method returns true. It returns false otherwise and the information in
   * outRecord is not modified.
   *
   * @param outRecord the output IntersectionRecord
   * @param rayIn the ray to intersect
   * @return true if the surface intersects the ray
   */
  public boolean intersect(IntersectionRecord outRecord, Ray rayIn) {
    // TODO#Ray Task 2: fill in this function.
        // Look at textbook chapter 4.4.1 for the derivation of this
        // we want to find the point on the sphere at which we intersect with the sphere
        // we can get this by finding t in the equation r(t) = p + td, where p is the origin of RayIn and d is the direction

        double negativeB = -rayIn.direction.dot(rayIn.origin.clone().sub(center));
        double bSquared = negativeB * negativeB;
        Vector3d originMinusCenter = rayIn.origin.clone().sub(center);
        double dirDotDir = rayIn.direction.dot(rayIn.direction);

        double A = dirDotDir;
        double B = -negativeB;
        double C = originMinusCenter.dot(originMinusCenter) - radius*radius;

        // if the determinant is negative, return false
        double determinant = B*B - A*C;
        if (determinant < 0) return false;

        double determinantSqrt = Math.sqrt(determinant);
        double tPos = (negativeB + determinantSqrt) / dirDotDir;
        double tNeg = (negativeB - determinantSqrt) / dirDotDir;
        
        double t = tNeg;
        if (t < rayIn.start || t > rayIn.end) {
        	t = tPos;
        	if (t < rayIn.start || t > rayIn.end) { 
        		// both are out of range
        		System.out.println("fai");
        		return false;
        	}
        }

	    // If there was an intersection, fill out the intersection record
        Vector3d location = new Vector3d(rayIn.origin.clone().add(rayIn.direction.clone().mul(t)));
        Vector3d centerToHit = location.clone().sub(center);
        Vector3d normal = centerToHit.clone().normalize();
       

        //Calculate uv by using spherical coordinates and the last assignment's method of calculation
        // horizontal angle, starting at 0 on positive x axis
        double theta = Math.atan(centerToHit.y / centerToHit.x);
        // Vertical angle, starting at 0 on position y axis
        double phi = Math.atan(Math.sqrt((centerToHit.x * centerToHit.x) + (centerToHit.y * centerToHit.y)) / centerToHit.z); 
        
        Vector2d texCoords = new Vector2d(phi, theta);

        outRecord.location.set(location);
        outRecord.texCoords.set(texCoords);
        outRecord.normal.set(normal);
        outRecord.t = t;
        outRecord.surface = this;
	    
	    return true;
  }
  
  /**
   * @see Object#toString()
   */
  public String toString() {
    return "sphere " + center + " " + radius + " " + shader + " end";
  }

}
