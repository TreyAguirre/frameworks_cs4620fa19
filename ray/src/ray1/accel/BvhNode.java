package ray1.accel;

import ray1.Ray;
import egl.math.Vector3d;

/**
 * A class representing a node in a bounding volume hierarchy.
 * 
 * @author pramook 
 */
public class BvhNode {

	/** The current bounding box for this tree node.
	 *  The bounding box is described by 
	 *  (minPt.x, minPt.y, minPt.z) - (maxBound.x, maxBound.y, maxBound.z).
	 */
	public final Vector3d minBound, maxBound;
	
	/**
	 * The array of children.
	 * child[0] is the left child.
	 * child[1] is the right child.
	 */
	public final BvhNode child[];

	/**
	 * The index of the first surface under this node. 
	 */
	public int surfaceIndexStart;
	
	/**
	 * The index of the surface next to the last surface under this node.	 
	 */
	public int surfaceIndexEnd; 
	
	/**
	 * Default constructor
	 */
	public BvhNode()
	{
		minBound = new Vector3d();
		maxBound = new Vector3d();
		child = new BvhNode[2];
		child[0] = null;
		child[1] = null;		
		surfaceIndexStart = -1;
		surfaceIndexEnd = -1;
	}
	
	/**
	 * Constructor where the user can specify the fields.
	 * @param minBound
	 * @param maxBound
	 * @param leftChild
	 * @param rightChild
	 * @param start
	 * @param end
	 */
	public BvhNode(Vector3d minBound, Vector3d maxBound, BvhNode leftChild, BvhNode rightChild, int start, int end) 
	{
		this.minBound = new Vector3d();
		this.minBound.set(minBound);
		this.maxBound = new Vector3d();
		this.maxBound.set(maxBound);
		this.child = new BvhNode[2];
		this.child[0] = leftChild;
		this.child[1] = rightChild;		   
		this.surfaceIndexStart = start;
		this.surfaceIndexEnd = end;
	}
	
	/**
	 * @return true if this node is a leaf node
	 */
	public boolean isLeaf()
	{
		return child[0] == null && child[1] == null; 
	}
	
	/** 
	 * Check if the ray intersects the bounding box.
	 * @param ray
	 * @return true if ray intersects the bounding box
	 */
	public boolean intersects(Ray ray) {
		// TODO#Ray Part 2 Task 3: fill in this function.
		// You can find this in the slides.
        boolean intersectsX, intersectsY, intersectsZ;
        intersectsX = intersectsY = intersectsZ  = false;

        // if the ray's direction is 0 in a dimension, we can determine intersection by the ray origin in that dimension
        Vector3d tMin = new Vector3d();
        Vector3d tMax = new Vector3d();
        if (ray.direction.x == 0) {
            intersectsX = ray.origin.x > minBound.x && ray.origin.x < maxBound.x;
        } else {
            double firstBound = (ray.direction.x > 0) ? minBound.x : maxBound.x;
            double secondBound = (ray.direction.x < 0) ? minBound.x : maxBound.x;
            tMin.x = (firstBound - ray.origin.x) / ray.direction.x;
            tMax.x = (secondBound - ray.origin.x) / ray.direction.x;
        }
        if (ray.direction.y == 0) {
            intersectsY = ray.origin.y > minBound.y && ray.origin.y < maxBound.y;
        } else {
            double firstBound = (ray.direction.y > 0) ? minBound.y : maxBound.y;
            double secondBound = (ray.direction.y < 0) ? minBound.y : maxBound.y;
            tMin.y = (firstBound - ray.origin.y) / ray.direction.y;
            tMax.y = (secondBound - ray.origin.y) / ray.direction.y;
        }
        if (ray.direction.z == 0) {
            intersectsZ = ray.origin.z > minBound.z && ray.origin.z < maxBound.z;
        } else {
            double firstBound = (ray.direction.z > 0) ? minBound.z : maxBound.z;
            double secondBound = (ray.direction.z < 0) ? minBound.z : maxBound.z;
            tMin.z = (firstBound - ray.origin.z) / ray.direction.z;
            tMax.z = (secondBound - ray.origin.z) / ray.direction.z;
        }

        // if we haven't already evaluated intersection in this dimension, do tMin/tMax check
        if (ray.direction.x != 0) {
            intersectsX = tMin.x < tMax.y && tMin.x < tMax.z;
        }
        if (ray.direction.y != 0) {
            intersectsY = tMin.y < tMax.x && tMin.y < tMax.z;
        }
        if (ray.direction.z != 0) {
            intersectsZ = tMin.z < tMax.x && tMin.z < tMax.y;
        }

		// the ray intersects this box
		return intersectsX && intersectsY && intersectsZ;
	}


}
