package manip;

import egl.math.*;
import gl.RenderObject;

public class RotationManipulator extends Manipulator {

	protected String meshPath = "Rotate.obj";

	public RotationManipulator(ManipulatorAxis axis) {
		super();
		this.axis = axis;
	}

	public RotationManipulator(RenderObject reference, ManipulatorAxis axis) {
		super(reference);
		this.axis = axis;
	}

	//assume X, Y, Z on stack in that order
	@Override
	protected Matrix4 getReferencedTransform() {
		Matrix4 m = new Matrix4();
		switch (this.axis) {
		case X:
			m.set(reference.rotationX).mulAfter(reference.translation);
			break;
		case Y:
			m.set(reference.rotationY)
				.mulAfter(reference.rotationX)
				.mulAfter(reference.translation);
			break;
		case Z:
			m.set(reference.rotationZ)
			.mulAfter(reference.rotationY)
			.mulAfter(reference.rotationX)
			.mulAfter(reference.translation);
			break;
		}
		return m;
	}

	@Override
	public void applyTransformation(Vector2 lastMousePos, Vector2 curMousePos, Matrix4 viewProjection) {
		// TODO#Manipulator: Modify this.reference.rotationX, this.reference.rotationY, or this.reference.rotationZ
		//   given the mouse input.
		// Use this.axis to determine the axis of the transformation.
		// Note that the mouse positions are given in coordinates that are normalized to the range [-1, 1]
		//   for both X and Y. That is, the origin is the center of the screen, (-1,-1) is the bottom left
		//   corner of the screen, and (1, 1) is the top right corner of the screen.
		Vector3 axisOrigin = new Vector3(reference.translation.get(0, 3), reference.translation.get(1, 3), reference.translation.get(2, 3));
		
		Vector3 p1 = getAxisPlanePoint(axisOrigin, this.axis, viewProjection, lastMousePos);
		Vector3 p2 = getAxisPlanePoint(axisOrigin, this.axis, viewProjection, curMousePos);
		
		// get the angle between p1 and p2 and then add that angle to the theta in this axis
		float angle = p1.angle(p2);
		
		
		switch(this.axis) {
		case X:
			float thetaX = (float)Math.acos(reference.rotationX.get(1, 1));
			
			Vector2 flatP1X = new Vector2(p1.y - axisOrigin.y, p1.z - axisOrigin.z);
			Vector2 flatP2X = new Vector2(p2.y - axisOrigin.y, p2.z - axisOrigin.z);
			float thetaFlatP1X = new Vector2(1, 0).angle(flatP1X);
			if (flatP1X.y < 0) thetaFlatP1X = 2 * (float)Math.PI - thetaFlatP1X;
			float thetaFlatP2X = new Vector2(1, 0).angle(flatP2X);
			if (flatP2X.y < 0) thetaFlatP2X = 2 * (float)Math.PI - thetaFlatP2X;
			
			float newAngleX = thetaFlatP2X - thetaFlatP1X;
			
			thetaX += newAngleX;
			thetaX *= -1;
			
			reference.rotationX.set(
					1f, 0f, 0f, 0f,
					0f, (float)Math.cos(thetaX), (float)-Math.sin(thetaX), 0f,
					0f, (float)Math.sin(thetaX), (float)Math.cos(thetaX), 0f,
					0f, 0f, 0f, 1f
			);
			break;
		case Y:
			float thetaY = (float)Math.acos(reference.rotationY.get(0, 0));
			Vector2 flatP1Y = new Vector2(p1.x, p1.z);
			Vector2 flatP2Y = new Vector2(p2.x, p2.z);
			float newAngleY = flatP1Y.angle(flatP2Y);
			
			thetaY += newAngleY;
//			reference.rotationY.set(
//				(float)Math.cos(thetaY), 0f, (float)Math.sin(thetaY), 0f,
//				0f, 1f, 0f, 0f,
//				-(float)Math.sin(thetaY), 0f, (float)Math.cos(thetaY), 0f,
//				0f, 0f, 0f, 1f
//			);
			break;
		case Z:
			float thetaZ = (float)Math.acos(reference.rotationZ.get(0, 0));
			Vector2 flatP1Z = new Vector2(p1.x - axisOrigin.x, p1.y - axisOrigin.y);
			Vector2 flatP2Z = new Vector2(p2.x - axisOrigin.x, p2.z - axisOrigin.z);
			
			thetaZ += thetaZ;
//			reference.rotationZ.set(
//				(float)Math.cos(thetaZ), (float)-Math.sin(thetaZ), 0, 0,
//				(float)Math.sin(thetaZ), (float)Math.cos(thetaZ), 0, 0,
//				0, 0, 1, 0,
//				0, 0, 0, 1
//			);
			break;
		}
		
	}

	Vector3 getAxisPlanePoint(Vector3 axisOrigin, ManipulatorAxis axis, Matrix4 mVP, Vector2 mousePos) {
		Vector3 p1 = new Vector3(mousePos.x, mousePos.y, -1);
		Vector3 p2 = new Vector3(mousePos.x, mousePos.y, 1);
		Matrix4 mVPI = mVP.clone().invert();
		//mouse ray in world space
		mVPI.mulPos(p1);
		mVPI.mulPos(p2);
		Vector3 rayOrigin = p1;
		Vector3 rayDirection = p2.clone().sub(p1).normalize();
		
		// find intersection point with plane created by other two axes
		Vector3 axisDirection = new Vector3();
		Vector3 otherDir1 = new Vector3();
		Vector3 otherDir2 = new Vector3();
		switch(axis) {
		case X:
			axisDirection.set(1, 0, 0);
			otherDir1.set(0, 1, 0);
			otherDir2.set(0, 0, 1);
			break;
		case Y:
			axisDirection.set(0, 1, 0);
			otherDir1.set(1, 0, 0);
			otherDir2.set(0, 0, 1);
			break;
		case Z:
			axisDirection.set(0, 0, 1);
			otherDir1.set(1, 0, 0);
			otherDir1.set(0, 1, 0);
			break; 
		}
		// rotate all these
		Vector4 axisDir4 = new Vector4(axisDirection.x, axisDirection.y, axisDirection.z, 1);
		Vector4 otherDir14 = new Vector4(otherDir1.x, otherDir1.y, otherDir1.z, 1);
		Vector4 otherDir24 = new Vector4(otherDir2.x, otherDir2.y, otherDir2.z, 1);
		
		reference.rotationX.mul(axisDir4);
		reference.rotationX.mul(otherDir14);
		reference.rotationX.mul(otherDir24);
		
		reference.rotationY.mul(axisDir4);
		reference.rotationY.mul(otherDir14);
		reference.rotationY.mul(otherDir24);
		
		reference.rotationZ.mul(axisDir4);
		reference.rotationZ.mul(otherDir14);
		reference.rotationZ.mul(otherDir24);
		
		axisDirection.set(axisDir4.x, axisDir4.y, axisDir4.z);
		otherDir1.set(otherDir14.x, otherDir14.y, otherDir14.z);
		otherDir2.set(otherDir24.x, otherDir24.y, otherDir24.z);
		
		// find point of intersection with plane created by otherDir1 and otherDir2
		Vector3 planeNormal = otherDir1.clone().cross(otherDir2).normalize();
		// A(x-x0) + B(y-y0) + C(z-z0) = 0
		// (A, B, C) = planeNormal
		// (x0, y0, z0) = axisOrigin
		// planeNormal.x * x + planeNormal.y * y + planeNormal.z * z = planeNormal.x * axisOrigin.x + planeNormal.y * axisOrigin.y + planeNormal.z * axisOrigin.z
		// (x, y, z) = r(t) = rayOrigin + t * rayDirection
		// Plug into x, y, z, solve for t
		float D = planeNormal.x * axisOrigin.x + planeNormal.y * axisOrigin.y + planeNormal.z * axisOrigin.z;
		float totalT = planeNormal.x * rayDirection.x + planeNormal.y * rayDirection.y + planeNormal.z * rayDirection.z;
		
		// t = D/totalT
		float t = D/totalT;
		
		Vector3 p = rayOrigin.clone().add(rayDirection.clone().mul(t));
		
		return p;
	}

	@Override
	protected String meshPath () {
		return "data/meshes/Rotate.obj";
	}
}
