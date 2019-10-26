package manip;

import egl.math.*;
import gl.RenderObject;

public class TranslationManipulator extends Manipulator {
	// hard coded mesh for now


	public TranslationManipulator (ManipulatorAxis axis) {
		super();
		this.axis = axis;
	}

	public TranslationManipulator (RenderObject reference, ManipulatorAxis axis) {
		super(reference);
		this.axis = axis;
	}

	@Override
	protected Matrix4 getReferencedTransform () {
		if (this.reference == null) {
			throw new RuntimeException ("Manipulator has no controlled object!");
		}
		return new Matrix4().set(reference.translation);
	}

	@Override
	public void applyTransformation(Vector2 lastMousePos, Vector2 curMousePos, Matrix4 viewProjection) {
		// TODO#A3: Modify this.reference.translation given the mouse input.
		// Use this.axis to determine the axis of the transformation.
		// Note that the mouse positions are given in coordinates that are normalized to the range [-1, 1]
		//   for both X and Y. That is, the origin is the center of the screen, (-1,-1) is the bottom left
		//   corner of the screen, and (1, 1) is the top right corner of the screen.

		// A3 SOLUTION START
		Vector3 axisOrigin = new Vector3(reference.translation.get(0, 3), reference.translation.get(1, 3), reference.translation.get(2, 3));
		// get this axis' direction and then rotate it by the reference's current rotation
		Vector4 axisDir = new Vector4();
		switch(axis) {
		case X:
			axisDir.set(1, 0, 0, 1);
			break;
		case Y:
			axisDir.set(0, 1, 0, 1);
			break;
		case Z:
			axisDir.set(0, 0, 1, 1);
			break;
		}
		reference.rotationX.mul(axisDir);
		reference.rotationY.mul(axisDir);
		reference.rotationZ.mul(axisDir);
		Vector3 rotatedAxisDir = new Vector3(axisDir.x, axisDir.y, axisDir.z);
		
		float t1 = getAxisT(axisOrigin, rotatedAxisDir, viewProjection, lastMousePos);
		float t2 = getAxisT(axisOrigin, rotatedAxisDir, viewProjection, curMousePos);
		
		Vector3 translation = new Vector3(
				reference.translation.get(0, 3),
				reference.translation.get(1, 3),
				reference.translation.get(2, 3)
		);
		Vector3 translationAmount = rotatedAxisDir.clone().mul(t2-t1);
		
		reference.translation.set(0, 3, translation.x + translationAmount.x);
		reference.translation.set(1, 3, translation.y + translationAmount.y);
		reference.translation.set(2, 3, translation.z + translationAmount.z);
		
		// A3 SOLUTION END
	}

	@Override
	protected String meshPath () {
		return "data/meshes/Translate.obj";
	}

}
