package ray1.surface;

import egl.math.*;
import ray1.IntersectionRecord;
import ray1.Ray;
import ray1.shader.Shader;
import ray1.OBJFace;

/**
 * Represents a single triangle, part of a triangle mesh
 *
 * @author ags
 */
public class Triangle extends Surface {
  /** The normal vector of this triangle, if vertex normals are not specified */
  Vector3 norm;
  
  /** The mesh that contains this triangle */
  Mesh owner;
  
  /** The face that contains this triangle */
  OBJFace face = null;
  
  double a, b, c, d, e, f;

  Vector3 v0, v1, v2;

  public Triangle(Mesh owner, OBJFace face, Shader shader) {
    this.owner = owner;
    this.face = face;

    v0 = owner.getMesh().getPosition(face,0);
    v1 = owner.getMesh().getPosition(face,1);
    v2 = owner.getMesh().getPosition(face,2);
    
    if (!face.hasNormals()) {
      Vector3 e0 = new Vector3(), e1 = new Vector3();
      e0.set(v1).sub(v0);
      e1.set(v2).sub(v0);
      norm = new Vector3();
      norm.set(e0).cross(e1).normalize();
    }

    a = v0.x-v1.x;
    b = v0.y-v1.y;
    c = v0.z-v1.z;
    
    d = v0.x-v2.x;
    e = v0.y-v2.y;
    f = v0.z-v2.z;
    
    this.setShader(shader);
  }

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
    // The derivation for these calculations can be found in section 4.4 of the textbook
    if (owner == null || face == null) return false;

    Matrix3d A = new Matrix3d(
            a, d, rayIn.direction.x,
            b, e, rayIn.direction.y,
            c, f, rayIn.direction.z
    );
    double Adeterminant = A.determinant();

    double g, h, i;
    g = v0.x - rayIn.origin.x;
    h = v0.y - rayIn.origin.y;
    i = v0.z - rayIn.origin.z;
    // Calculate t in r(t) = p + td <- ray = ray origin + t * ray direction
    Matrix3d tMat = new Matrix3d(
            a, d, g,
            b, e, h,
            c, f, i
    );
    double t = tMat.determinant() / Adeterminant;
    if (t < rayIn.start || t > rayIn.end) return false;

    // Compute gamma
    Matrix3d gammaMat = new Matrix3d(
            a, g, rayIn.direction.x,
            b, h, rayIn.direction.y,
            c, i, rayIn.direction.z
    );
    double gamma = gammaMat.determinant() / Adeterminant;
    if (gamma < 0 || gamma > 1) return false;

    // computer beta
    Matrix3d betaMat = new Matrix3d(
            g, d, rayIn.direction.x,
            h, e, rayIn.direction.y,
            i, f, rayIn.direction.z
    );
    double beta = betaMat.determinant() / Adeterminant;
    if (beta < 0 || beta > 1 - gamma) return false;

    double alpha = 1 - beta - gamma;

    // If there was an intersection, fill out the intersection record
    Vector3d location = rayIn.origin.clone().add(rayIn.direction.clone().mul(t));

    Vector3d normal;
    if (face.hasNormals()) {
      Vector3d n0 = new Vector3d(owner.getMesh().getNormal(face, face.normals[0]).clone()).mul(alpha);
      Vector3d n1 = new Vector3d(owner.getMesh().getNormal(face, face.normals[1]).clone()).mul(beta);
      Vector3d n2 = new Vector3d(owner.getMesh().getNormal(face, face.normals[2]).clone()).mul(gamma);

      // weighted average of each vertex normal
      normal = n0.clone().add(n1).add(n2).normalize();
    } else {
      normal = new Vector3d(norm);
    }

    // weighted average of vertex uvs
    Vector2d texCoord0 = new Vector2d(owner.getMesh().getUV(face, face.uvs[0]).clone()).mul(alpha);
    Vector2d texCoord1 = new Vector2d(owner.getMesh().getUV(face, face.uvs[1]).clone()).mul(beta);
    Vector2d texCoord2 = new Vector2d(owner.getMesh().getUV(face, face.uvs[2]).clone()).mul(gamma);
    Vector2d texCoord = texCoord0.clone().add(texCoord1).add(texCoord2).normalize();

    outRecord.location.set(location);
    outRecord.normal.set(normal);
    outRecord.surface = this;
    outRecord.t = t;
    outRecord.texCoords.set(texCoord);

	return true;
  }

  /**
   * @see Object#toString()
   */
  public String toString() {
    return "Triangle ";
  }
}