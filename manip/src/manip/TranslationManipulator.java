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
		float diffX = curMousePos.x - lastMousePos.x;
		float diffY = curMousePos.y - lastMousePos.y;
		
		switch(axis) {
			case X:
				reference.translation.set(0, 3, reference.translation.get(0,  3) + diffX);
				break;
			case Y:
				reference.translation.set(1, 3, reference.translation.get(1,  3) + diffY);
				break;
			case Z:
				reference.translation.set(1, 3, reference.translation.get(1,  3) + diffX);
				break;
		}
		
		
		System.out.println("IM HAPPENING");
		
		// A3 SOLUTION END
	}

	@Override
	protected String meshPath () {
		return "data/meshes/Translate.obj";
	}

}
