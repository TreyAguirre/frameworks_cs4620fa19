package ray1.camera;

import egl.math.Matrix4;
import egl.math.Matrix4d;
import egl.math.Vector3;
import ray1.Ray;
import egl.math.Vector3d;

import java.util.ArrayList;

public class OrthographicCamera extends Camera {

    //TODO#Ray Task 1: create necessary new variables/objects here, including an orthonormal basis
    //          formed by three basis vectors and any other helper variables 
    //          if needed.

    Vector3d viewX;
    Vector3d viewY;
    Vector3d viewZ;

    /**
     * Initialize the derived view variables to prepare for using the camera.
     */
    public void init() {
        // TODO#Ray Task 1:  Fill in this function.
        // 1) Set the 3 basis vectors in the orthonormal basis, 
        //    based on viewDir and viewUp
        Vector3d camZ = new Vector3d(viewDir.clone());
        Vector3d camY = new Vector3d(viewUp.clone());
        Vector3d camX = camZ.clone().cross(camY.clone());

        ArrayList<Vector3d> orthogonalBasis = grammSchmidt(camZ, camY, camX);

        viewZ = orthogonalBasis.get(0).clone();
        viewY = orthogonalBasis.get(1).clone();
        viewX = orthogonalBasis.get(2).clone();
        // 2) Set up the helper variables if needed
    }

    private ArrayList<Vector3d> grammSchmidt(Vector3d in1, Vector3d in2, Vector3d in3) {
        Vector3d out1 = in1.clone().normalize();
        Vector3d out2 = in2.clone().sub(out1.clone().mul(in2.dot(out1) / out1.lenSq())).normalize();
        Vector3d out3 = in3.clone().
                sub( out1.clone().mul( in3.dot(out1) / out1.lenSq() ) ).
                sub( out2.clone().mul( in3.dot(out2) / out2.lenSq() ) ).normalize();

        ArrayList<Vector3d> orthogonalBasis = new ArrayList<>();
        orthogonalBasis.add(out1);
        orthogonalBasis.add(out2);
        orthogonalBasis.add(out3);

        return orthogonalBasis;
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
        double u = inU * viewWidth - viewWidth / 2.0;
        double v = inV * viewHeight - viewHeight / 2.0;
        // 2) Set the origin field of outRay for an orthographic camera. 
        //    In an orthographic camera, the origin should depend on your transformed
        //    inU and inV and your basis vectors u and v.
        Vector3d origin = new Vector3d(viewPoint.clone());
        Vector3d camOrigin = origin.clone().add(viewX.clone().mul(u)).add(viewY.clone().mul(v));
        // 3) Set the direction field of outRay for an orthographic camera.
        // direction is just always camZ

        outRay.set(new Vector3d(camOrigin.clone()), new Vector3d(viewDir.clone()));
    }
}
