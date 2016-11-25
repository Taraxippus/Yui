package com.taraxippus.yui.util;

import java.util.Vector;

public class Quaternion
{
    public float x, y, z, w = 1;

    public static final Quaternion identity = new Quaternion();

    public Quaternion(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Quaternion(float angle, VectorF axis)
    {
        this.setAngleAxis(angle, axis);
    }

    public Quaternion() {}

    public Quaternion add(Quaternion other)
    {
        this.w += other.w;
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }

    public Quaternion add(float x1, float y1, float z1, float w1)
    {
        this.x += x1;
        this.y += y1;
        this.z += z1;
		this.w += w1;
        return this;
    }

    public Quaternion subtract(Quaternion other)
    {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
		this.w -= other.w;
        return this;
    }

    public Quaternion multiplyLeftBy(Quaternion other)
    {
		return this.multiplyLeftBy(other.x, other.y, other.z, other.w);
    }

	public Quaternion multiplyRightBy(Quaternion other)
    {
		return this.multiplyRightBy(other.x, other.y, other.z, other.w);
    }
	
    public Quaternion multiplyLeftBy(float x1, float y1, float z1, float w1)
    {
		return set(
			w1 * x + x1 * w + y1 * z - z1 * y,
			w1 * y + y1 * w + z1 * x - x1 * z,
			w1 * z + z1 * w + x1 * y - y1 * x,
			w1 * w - x1 * x - y1 * y - z1 * z);
    }
	
	public Quaternion multiplyRightBy(float x1, float y1, float z1, float w1)
    {
       	return set(
			w * x1 + x * w1 + y * z1 - z * y1,
			w * y1 + y * w1 + z * x1 - x * z1,
			w * z1 + z * w1 + x * y1 - y * x1,
			w * w1 - x * x1 - y * y1 - z * z1);
    }

    public Quaternion multiplyLeftByAngleAxis(float angle, float x, float y, float z)
    {
        final float a = angle * 0.5F;
        final float s = (float)Math.sin(a);

        return this.multiplyLeftBy(x * s, y * s, z * s, (float) Math.cos(a));
    }

	public Quaternion multiplyRightByAngleAxis(float angle, float x, float y, float z)
    {
        final float a = angle * 0.5F;
        final float s = (float)Math.sin(a);

        return this.multiplyRightBy(x * s, y * s, z * s, (float) Math.cos(a));
    }
	
	public Quaternion divideBy(float d)
    {
        return this.multiplyBy(1F / d);
    }
	
    public Quaternion multiplyBy(float d)
    {
        this.x *= d;
        this.y *= d;
        this.z *= d;
		this.w *= d;
        return this;
    }

    public float dot(Quaternion other)
    {
        return w * other.w + x * other.x + y * other.y + z * other.z;
    }

    public Quaternion slerp(Quaternion a, Quaternion b, float t)
    {
        final float cosHalfTheta = a.w * b.w + a.x * b.x + a.y * b.y + a.z * b.z;

        if (Math.abs(cosHalfTheta) > 0.999)
        {
            return this.set(a);
        }

        final float halfTheta = (float) Math.acos(cosHalfTheta);
        final float sinHalfTheta = (float) Math.sqrt(1 - cosHalfTheta * cosHalfTheta);

        if (Math.abs(halfTheta) < 0.001)
        {
            return this.set(a).multiplyBy(t).add(b.x * (1 - t), b.y * (1 - t), b.z * (1 - t), b.w * (1 - t));
        }

        final float ratioA = (float) Math.sin((1 - t) * halfTheta) / sinHalfTheta;
        final float ratioB = (float) Math.sin(t * halfTheta) / sinHalfTheta;

        return this.set(a).multiplyBy(ratioA).add(b.x * ratioB, b.y * ratioB, b.z * ratioB, b.w * ratioB);
    }


    public Quaternion conjugate()
    {
        this.x = -x;
        this.y = -y;
        this.z = -z;
        return this;
    }

    public Quaternion inverse()
    {
        return this.divideBy(lengthSquared()).conjugate();
    }

    public float length()
    {
        return (float)Math.sqrt(w * w + x * x + y * y + z * z);
    }

    public float lengthSquared()
    {
        return w * w + x * x + y * y + z * z;
    }

    public Quaternion normalize()
    {
        return this.divideBy(length());
    }

    public boolean normalized()
    {
        return lengthSquared() == 1;
    }

    public Quaternion copy()
    {
        return new Quaternion(this.x, this.y, this.z, this.w);
    }

    public Quaternion set(Quaternion other)
    {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
		this.w = other.w;
		
        return this;
    }

    public Quaternion set(VectorF v)
    {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
		this.w = 0;
		
        return this;
    }

    public Quaternion set(float x, float y, float z,float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
		this.w = w;
		
        return this;
    }

    public Quaternion setAngleAxis(final float angle, final VectorF axis)
    {
        return setAngleAxis(angle, axis);
    }

    public Quaternion setAngleAxis(final float angle, final float x, final float y, final float z)
    {
        final float a = angle * 0.5F;
        final float s = (float)Math.sin(a);
        this.w = (float)Math.cos(a);
        this.x = x * s;
        this.y = y * s;
        this.z = z * s;

        return this;
    }

    public float[] getMatrix(float[] matrix)
    {
        final float xx      = x * x;
        final float xy      = x * y;
        final float xz      = x * z;
        final float xw      = x * w;

        final float yy      = y * y;
        final float yz      = y * z;
        final float yw      = y * w;

        final float zz      = z * z;
        final float zw      = z * w;

        matrix[0] = (1 - 2 * ( yy + zz ));
        matrix[1] = (2 * ( xy - zw ));
        matrix[2] = (2 * ( xz + yw ));

        matrix[4]  = (2 * ( xy + zw ));
        matrix[5]  = (1 - 2 * ( xx + zz ));
        matrix[6]  = (2 * ( yz - xw ));

        matrix[8]  = (2 * ( xz - yw ));
        matrix[9]  = (2 * ( yz + xw ));
        matrix[10] = (1 - 2 * ( xx + yy ));

        matrix[3]  = matrix[7] = matrix[11] = matrix[12] = matrix[13] = matrix[14] = 0;
        matrix[15] = 1;
        return  matrix;
    }

    public float angleAxis(final VectorF axis)
    {
        final float angle;
        final float squareLength = x*x + y*y + z*z;

        if (squareLength > 0.00001f * 0.00001f)
        {
            angle = 2.0F * (float)Math.acos(w);
            final float inverseLength = 1.0f / (float)Math.pow(squareLength, 0.5f);
            axis.x = x * inverseLength;
            axis.y = y * inverseLength;
            axis.z = z * inverseLength;
        }
        else
        {
            angle = 0.0f;
            axis.x = 0.0f;
            axis.y = 1.0f;
            axis.z = 0.0f;
        }

        return angle;
    }

    public Quaternion rotateX(final float angle)
    {
        final float halfAngle = 0.5F * angle;
        final float sin = (float) Math.sin(halfAngle);
        final float cos = (float) Math.cos(halfAngle);
        return set(x * cos + w * sin,
                y * cos + z * sin,
                -y * sin + z * cos,
                -x * sin + w * cos);
    }

    public Quaternion rotateY(final float angle)
    {
        final float halfAngle = 0.5f * angle;
        final float sin = (float) Math.sin(halfAngle);
        final float cos = (float) Math.cos(halfAngle);
        return set(x * cos - z * sin,
                y * cos + w * sin,
                x * sin + z * cos,
                -y * sin + w * cos);
    }

    public Quaternion rotateZ(final float angle)
    {
        final float halfAngle = 0.5f * angle;
        final float sin = (float) Math.sin(halfAngle);
        final float cos = (float) Math.cos(halfAngle);
        return set(x * cos + y * sin,
                -x * sin + y * cos,
                z * cos + w * sin,
                -z * sin + w * cos);
    }
	
	private static final Vector<Quaternion> pool = new Vector<>();

    public static Quaternion obtain()
    {
        if (pool.isEmpty())
            return new Quaternion();
        else
            return pool.remove(0).set(0, 0, 0, 1);
    }

    public static Quaternion release(Quaternion tmp)
    {
        pool.add(tmp);
        return tmp;
    }

    public Quaternion release()
    {
        return release(this);
    }

    @Override
    public String toString()
    {
        return "[" + w + ", " + x + ", " + y + ", " + z + "]";
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof Quaternion && ((Quaternion)o).w == this.w && ((Quaternion)o).x == this.x && ((Quaternion)o).y == this.y && ((Quaternion)o).z == this.z;
    }
}
