package ray1.camera;

import egl.math.Matrix4;
import egl.math.Vector3;
import ray1.Ray;
import egl.math.Vector3d;

import java.util.ArrayList;

/**
 * Represents a camera with perspective view. For this camera, the view window
 * corresponds to a rectangle on a plane perpendicular to viewDir but at
 * distance projDistance from viewPoint in the direction of viewDir. A ray with
 * its origin at viewPoint going in the direction of viewDir should intersect
 * the center of the image plane. Given u and v, you should compute a point on
 * the rectangle corresponding to (u,v), and create a ray from viewPoint that
 * passes through the computed point.
 */
public class PerspectiveCamera extends Camera {

    protected float projDistance = 1.0f;
    public float getProjDistance() { return projDistance; }
    public void setprojDistance(float projDistance) {
        this.projDistance = projDistance;
    }


    //TODO#Ray Task 1: create necessary new variables/objects here, including an orthonormal basis
    //          formed by three basis vectors and any other helper variables 
    //          if needed.

    // Useful explanation of different spaces https://learnopengl.com/Getting-started/Coordinate-Systems

    // Perspective projection matrix, maps from 3D world space to the on-screen 2D image space
    // For an explanation on the derivation of this matrix, look here.
    // https://www.scratchapixel.com/lessons/3d-basic-rendering/perspective-and-orthographic-projection-matrix/opengl-perspective-projection-matrix

    Vector3d viewX;
    Vector3d viewY;
    Vector3d viewZ;

    /**
     * Initialize the derived view variables to prepare for using the camera.
     */
    public void init() {
        // TODO#Ray Task 1: Fill in this function.
        // 1) Set the 3 basis vectors in the orthonormal basis,
        // based on viewDir and viewUp
        // 2) Set up the helper variables if needed
        Vector3d camZ = new Vector3d(viewDir.clone());
        Vector3d camY = new Vector3d(viewUp.clone());
        Vector3d camX = new Vector3d(camZ.clone().cross(camY.clone()));

        ArrayList<Vector3d> orthographicBasis = grammSchmidt(camZ, camY, camX);
        viewZ = orthographicBasis.get(0);
        viewY = orthographicBasis.get(1);
        viewX = orthographicBasis.get(2);
    }

    /**
     * Set outRay to be a ray from the camera through a point in the image.
     *
     * @param outRay The output ray (not normalized)
     * @param inU The u coord of the image point (range [0,1])
     * @param inV The v coord of the image point (range [0,1])
     */
    public void getRay(Ray outRay, float inU, float inV) {
        // TODO#Ray Task 1: Fill in this function.
        // 1) Transform inU so that it lies between [-viewWidth / 2, +viewWidth / 2] 
        //    instead of [0, 1]. Similarly, transform inV so that its range is
        //    [-vieHeight / 2, +viewHeight / 2]
        inU = inU * viewWidth - viewWidth / 2f;
        inV = inV * viewHeight - viewHeight /2f;
        
        // 2) Set the origin field of outRay for a perspective camera.
        Vector3d camOrigin = new Vector3d(viewPoint.clone());
        Vector3d camView = new Vector3d(viewDir.clone().normalize());
        // 3) Set the direction field of outRay for an perspective camera. This
        //    should depend on your transformed inU and inV and your basis vectors,
        //    as well as the projection distance.
        Vector3d viewPlaneOrigin = camOrigin.clone().add(camView.clone().mul(projDistance));
        Vector3d viewPlanePoint = viewPlaneOrigin.clone().add(viewX.clone().mul(inU)).clone().add(viewY.clone().mul(inV));
        Vector3d camToViewPlanePoint = viewPlanePoint.clone().sub(camOrigin.clone());
        outRay.set(new Vector3d(viewPoint.clone()), new Vector3d(camToViewPlanePoint.clone().normalize()));
    }
}