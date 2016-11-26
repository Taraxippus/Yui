package com.taraxippus.yui.model;

import com.taraxippus.yui.render.Pass;
import com.taraxippus.yui.util.TriangleIndices;
import com.taraxippus.yui.util.VectorF;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class IcoSphereModel extends Model
{
	final int recursionLevel;
	public ArrayList<VectorF> vertices;
	public ArrayList<TriangleIndices> faces;
	Dictionary<Integer, Short> middlePointIndexCache;
	
	public IcoSphereModel(Pass pass, int recursionLevel)
	{
		super(pass);
		
		this.recursionLevel = recursionLevel;
	}
	
    private short getMiddlePoint(short p1, short p2)
    {
        boolean firstIsSmaller = p1 < p2;
        short smallerIndex = firstIsSmaller ? p1 : p2;
       	short greaterIndex = firstIsSmaller ? p2 : p1;
        int key = (smallerIndex << 16) + greaterIndex;

        Short cached = this.middlePointIndexCache.get(key);
        if (cached != null)
            return cached;

        VectorF point1 = vertices.get(p1);
        VectorF point2 = vertices.get(p2);
       	VectorF middle = VectorF.obtain().set(
            (point1.x + point2.x) / 2.0F, 
            (point1.y + point2.y) / 2.0F, 
            (point1.z + point2.z) / 2.0F);

        vertices.add(middle.normalize().multiplyBy(0.5F));
		short i = (short) (vertices.size() - 1);
		
        this.middlePointIndexCache.put(key, i);
        return i;
    }

	public void generate()
	{
		middlePointIndexCache = new Hashtable<Integer, Short>();
		vertices = new ArrayList<VectorF>();
		faces = new ArrayList<TriangleIndices>();
		
        vertices.add(VectorF.obtain().set(-1,  t,  0).normalize().multiplyBy(0.5F));
        vertices.add(VectorF.obtain().set( 1,  t,  0).normalize().multiplyBy(0.5F));
        vertices.add(VectorF.obtain().set(-1, -t,  0).normalize().multiplyBy(0.5F));
        vertices.add(VectorF.obtain().set( 1, -t,  0).normalize().multiplyBy(0.5F));

        vertices.add(VectorF.obtain().set( 0, -1,  t).normalize().multiplyBy(0.5F));
        vertices.add(VectorF.obtain().set( 0,  1,  t).normalize().multiplyBy(0.5F));
        vertices.add(VectorF.obtain().set( 0, -1, -t).normalize().multiplyBy(0.5F));
        vertices.add(VectorF.obtain().set( 0,  1, -t).normalize().multiplyBy(0.5F));

        vertices.add(VectorF.obtain().set( t,  0, -1).normalize().multiplyBy(0.5F));
        vertices.add(VectorF.obtain().set( t,  0,  1).normalize().multiplyBy(0.5F));
        vertices.add(VectorF.obtain().set(-t,  0, -1).normalize().multiplyBy(0.5F));
        vertices.add(VectorF.obtain().set(-t,  0,  1).normalize().multiplyBy(0.5F));

        faces.add(new TriangleIndices(0, 11, 5));
        faces.add(new TriangleIndices(0, 5, 1));
        faces.add(new TriangleIndices(0, 1, 7));
        faces.add(new TriangleIndices(0, 7, 10));
        faces.add(new TriangleIndices(0, 10, 11));

        faces.add(new TriangleIndices(1, 5, 9));
        faces.add(new TriangleIndices(5, 11, 4));
        faces.add(new TriangleIndices(11, 10, 2));
        faces.add(new TriangleIndices(10, 7, 6));
        faces.add(new TriangleIndices(7, 1, 8));

        faces.add(new TriangleIndices(3, 9, 4));
        faces.add(new TriangleIndices(3, 4, 2));
        faces.add(new TriangleIndices(3, 2, 6));
        faces.add(new TriangleIndices(3, 6, 8));
        faces.add(new TriangleIndices(3, 8, 9));

        faces.add(new TriangleIndices(4, 9, 5));
        faces.add(new TriangleIndices(2, 4, 11));
        faces.add(new TriangleIndices(6, 2, 10));
        faces.add(new TriangleIndices(8, 6, 7));
        faces.add(new TriangleIndices(9, 8, 1));

		short a, b, c;
        for (int i = 0; i < recursionLevel; i++)
		{
			final ArrayList<TriangleIndices> faces2 = new ArrayList<TriangleIndices>();

			for (TriangleIndices tri : faces)
            {
                a = getMiddlePoint(tri.v1, tri.v2);
                b = getMiddlePoint(tri.v2, tri.v3);
                c = getMiddlePoint(tri.v3, tri.v1);

                faces2.add(new TriangleIndices(tri.v1, a, c));
                faces2.add(new TriangleIndices(tri.v2, b, a));
                faces2.add(new TriangleIndices(tri.v3, c, b));
                faces2.add(new TriangleIndices(a, b, c));
          	}
			faces = faces2;
		}
	}
	
	private static final float t = (float) (1.0 + Math.sqrt(5.0)) / 2.0F;
	
	@Override
	public float[] getVertices()
	{
		generate();
		
		return VectorF.toArrayList(vertices, true);
	}
	
	@Override
	public short[] getIndices()
	{
		return TriangleIndices.toIndices(faces, 0);
	}

	@Override
	protected void freeShapeSpace()
	{
		super.freeShapeSpace();
		
		vertices = null;
		faces = null;
		middlePointIndexCache = null;
	}
}
