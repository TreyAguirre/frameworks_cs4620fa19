package ray1.shader;

import egl.math.Color;
import egl.math.Colorf;
import egl.math.Vector2;
import egl.math.Vector3;
import egl.math.Vector3d;
import ray1.IntersectionRecord;
import ray1.Light;
import ray1.Ray;
import ray1.RayTracer;
import ray1.Scene;

public abstract class ReflectionShader extends Shader {

	/** BEDF used by this shader. */
	protected BRDF brdf = null;

	/** Coefficient for mirror reflection. */
	protected final Colorf mirrorCoefficient = new Colorf();
	public void setMirrorCoefficient(Colorf mirrorCoefficient) { this.mirrorCoefficient.set(mirrorCoefficient); }
	public Colorf getMirrorCoefficient() {return new Colorf(mirrorCoefficient);}

	public ReflectionShader() {
		super();
	}

	/**
	 * Evaluate the intensity for a given intersection using the Microfacet shading model.
	 *
	 * @param outIntensity The color returned towards the source of the incoming ray.
	 * @param scene The scene in which the surface exists.
	 * @param ray The ray which intersected the surface.
	 * @param record The intersection record of where the ray intersected the surface.
	 * @param depth The recursion depth.
	 */
	@Override
	public void shade(Colorf outIntensity, Scene scene, Ray ray, IntersectionRecord record, int depth) {
		Vector3d incoming = new Vector3d();
		Vector3d outgoing = new Vector3d();
				
		outgoing.set(ray.origin).sub(record.location).normalize();
		Vector3d surfaceNormal = record.normal;
		Vector2 texCoords = new Vector2(record.texCoords);
		
		Colorf BRDFVal = new Colorf();
		
		// direct reflection from light sources
		outIntensity.setZero();
		
		// TODO#Ray Task 5: Fill in this function.
		// 1) Loop through each light in the scene.
		for (Light light : scene.getLights()) {	
			// 2) If the intersection point is shadowed, skip the calculation for the light.
			//	  See Shader.java for a useful shadowing function.
			if (this.isShadowed(scene, light, record))
				continue;
			
			// 3) Compute the incoming direction by subtracting
			//    the intersection point from the light's position.
			Vector3d lightPos = new Vector3d(light.position.clone());
			incoming = lightPos.clone().sub(record.location.clone()).normalize();
			
			// line from hit point to ray origin
			outgoing.set(ray.origin.clone().sub(record.location)).normalize();

			// 4) Compute the color of the point using the shading model.
			//	  EvalBRDF method of brdf object should be called to evaluate BRDF value at the shaded surface point.
			brdf.EvalBRDF(incoming, outgoing, surfaceNormal, texCoords, BRDFVal);

			// 5) Add the computed color value to the output.
			Colorf finalColor = new Colorf();
			finalColor.set(BRDFVal.clone());
            double max = Math.max(surfaceNormal.dot(incoming), 0);
            double dist = light.position.clone().dist(new Vector3(record.location.clone()));
            double factor = max / (dist * dist);
            Colorf intensityFactored = new Colorf();
            intensityFactored.set(light.intensity.clone().mul((float)factor));
            finalColor.mul(intensityFactored);

			outIntensity.add(finalColor);
		}

		// 6) If mirrorCoefficient is not zero vector, add recursive mirror reflection
		if (!mirrorCoefficient.isZero()) {
			//		6a) Compute the mirror reflection ray direction by reflecting the direction vector of "ray" about surface normal
			double reflectionDot = ray.direction.clone().dot(surfaceNormal) * 2;
			Vector3d reflectionDir = surfaceNormal.clone().mul(reflectionDot);
			Vector3d mirrorReflection = ray.direction.clone().sub(reflectionDir).normalize();

			//		6b) Construct mirror reflection ray starting from the intersection point (record.location) and pointing along
			//			direction computed in 6a) (Hint: remember to call makeOffsetRay to avoid self-intersecting)
			ray.makeOffsetRay();
			Ray reflectionRay = new Ray(record.location.clone(), mirrorReflection.clone());

			//      6c) Compute the Fresnel's reflectance coefficient with Schlick's approximation
			double theta = surfaceNormal.dot(ray.direction);
			if (theta < 0) theta = 1;

			Colorf oneMinusMirror = new Colorf();
			oneMinusMirror.x = 1 - mirrorCoefficient.x;
			oneMinusMirror.y = 1 - mirrorCoefficient.y;
			oneMinusMirror.z = 1 - mirrorCoefficient.z;
			oneMinusMirror.mul((float)Math.pow(1 - theta, 5));

			// 		6d) call RayTracer.shadeRay() with the mirror reflection ray and (depth+1)
			Colorf reflectanceCoefficient = new Colorf();
			reflectanceCoefficient.set(mirrorCoefficient.clone().add(oneMinusMirror));
			Colorf reflectionColor = new Colorf();
			RayTracer.shadeRay(reflectionColor, scene, reflectionRay, depth+1);
			reflectionColor.mul(reflectanceCoefficient);
			// 		6e) add returned color value in 6d) to output
			outIntensity.add(reflectionColor);
		}
	}

}