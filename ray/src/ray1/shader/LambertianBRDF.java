package ray1.shader;

import egl.math.*;

/**
 * A Lambertian material scatters light equally in all directions; its BRDF value is
 * a constant
 *
 * @author srm, zechenz
 */
public class LambertianBRDF extends BRDF {

	LambertianBRDF(Colorf diffuseReflectance, Texture diffuseReflectanceTexture) {
		super(diffuseReflectance, diffuseReflectanceTexture);
	}

	public String toString() {    
		return "Lambertian BRDF" + super.toString();
	}

	@Override
	public void EvalBRDF(Vector3d incoming, Vector3d outgoing, Vector3d surfaceNormal, Vector2 texCoords, Colorf BRDFValue) {
		// TODO#Ray: Evaluate the BRDF value of Lambertian reflectance and set it to variable BRDFValue
		// Hint: getDiffuseReflectance() method can be helpful.
		Colorf diffuseReflectance  = getDiffuseReflectance(texCoords);
		double dstToSrc = outgoing.len();
		double srcIrradiance = Math.max(0, incoming.clone().dot(surfaceNormal.clone())) / (dstToSrc * dstToSrc);

		Colorf bdrf = (Colorf)diffuseReflectance.clone().mul((float)(1 / Math.PI)).mul((float)srcIrradiance);
		BRDFValue.set(bdrf.clone());
	}
}
