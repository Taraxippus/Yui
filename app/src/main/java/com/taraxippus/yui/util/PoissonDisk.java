package com.taraxippus.yui.util;
import android.graphics.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class PoissonDisk
{
	public static ArrayList<VectorF> randomDistribution(Random random, float width, float height, float minDist, int newPointCount)
	{
		final float cellSize = (float) (minDist / Math.sqrt(2));
		final int gridWidth = (int) Math.ceil(width / cellSize), gridHeight = (int) Math.ceil(height / cellSize);
		final VectorF[] grid = new VectorF[gridWidth * gridHeight];
		
		final ArrayList<VectorF> processList = new ArrayList<>();
		final ArrayList<VectorF> samplePoints = new ArrayList<>();

		VectorF tmp = VectorF.obtain(), newPoint, point = VectorF.obtain().set(random.nextFloat() * width, 0, random.nextFloat() * height);

		processList.add(point);
		samplePoints.add(point);
		
		grid[(int) (point.z / cellSize) * gridWidth + (int) (point.x / cellSize)] = point;

		int i, gridX, gridZ, x, z;
		while (!processList.isEmpty())
		{
			point = processList.remove(random.nextInt(processList.size()));
			
			candidates:
			for (i = 0; i < newPointCount; i++)
			{
				newPoint = VectorF.obtain().set(0, 0, minDist * (1 + random.nextFloat())).rotateY(360 * random.nextFloat()).add(point);
				
				if (newPoint.x < 0 || newPoint.z < 0 || newPoint.x >= width || newPoint.z >= height)
				{
					newPoint.release();
					continue;
				}
				
				gridX = (int) (newPoint.x / cellSize);
				gridZ = (int) (newPoint.z / cellSize);
				
				for (x = -2; x <= 2; ++x)
					for (z = -2; z <= 2; ++z)
						if (x + gridX >= 0 && z + gridZ >= 0 && x + gridX < gridWidth && z + gridZ < gridHeight)
						{
							if (grid[(z + gridZ) * gridWidth + x + gridX] == null)
								continue;
								
							if (tmp.set(grid[(z + gridZ) * gridWidth + x + gridX]).subtract(newPoint).lengthSquared() < minDist * minDist)
							{
								newPoint.release();
								continue candidates;
							}
						}
						
				processList.add(newPoint);
				samplePoints.add(newPoint);
				grid[gridZ * gridWidth + gridX] = newPoint;
			}
		}
		tmp.release();
		return samplePoints;
	}
}
