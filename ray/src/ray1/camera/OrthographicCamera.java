package ray1.camera;

import egl.math.Matrix4;
import egl.math.Matrix4d;
import egl.math.Vector3;
import ray1.Ray;
import egl.math.Vector3d;

public class OrthographicCamera extends Camera {

    //TODO#Ray Task 1: create necessary new variables/objects here, including an orthonormal basis
    //          formed by three basis vectors and any other helper variables 
    //          if needed.

    Vector3 camX;
    Vector3 camY;
    Vector3 camZ;

    /**
     * Initialize the derived view variables to prepare for using the camera.
     */
    public void init() {
        // TODO#Ray Task 1:  Fill in this function.
        // 1) Set the 3 basis vectors in the orthonormal basis, 
        //    based on viewDir and viewUp
        camZ = new Vector3(viewDir.clone());
        camY = new Vector3(viewUp.clone());
        camX = camZ.clone().cross(camY.clone());
        // 2) Set up the helper variables if needed
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
        inV = inV * viewHeight - viewHeight / 2f;
        // 2) Set the origin field of outRay for an orthographic camera. 
        //    In an orthographic camera, the origin should depend on your transformed
        //    inU and inV and your basis vectors u and v.
        Vector3 camOrigin = viewPoint.clone().add(camX.clone().mul(inU)).add(camY.clone().mul(inV));
        // 3) Set the direction field of outRay for an orthographic camera.
        // direction is just always camZ

        outRay.set(new Vector3d(camOrigin.clone()), new Vector3d(camZ.clone()));
    }

}
