package com.taraxippus.yui.util;
import android.graphics.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class PoissonDisk
{
	public static ArrayList<VectorF> randomDistribution(Random random, float width, float height, float minDist, int newPointCount, int maxPoints)
	{
		return randomDistribution(random, width, height, minDist, minDist, null, 0, newPointCount, maxPoints);
	}
	
	public static ArrayList<VectorF> randomDistribution(Random random, float width, float height, float minDist, float maxDist, SimplexNoise noise, float noiseScale, int newPointCount, int maxPoints)
	{
		final float cellSize = (float) (maxDist / Math.sqrt(2));
		final int gridWidth = (int) Math.ceil(width / cellSize), gridHeight = (int) Math.ceil(height / cellSize);
		final ArrayList[] grid = new ArrayList[gridWidth * gridHeight];
		
		final ArrayList<VectorF> processList = new ArrayList<>();
		final ArrayList<VectorF> samplePoints = new ArrayList<>();

		VectorF tmp = VectorF.obtain(), newPoint, point = VectorF.obtain().set(random.nextFloat() * width, 0, random.nextFloat() * height);
		ArrayList list = new ArrayList();
		
		processList.add(point);
		samplePoints.add(point);
		list.add(point);
		grid[(int) (point.z / cellSize) * gridWidth + (int) (point.x / cellSize)] = list;
		maxPoints--;
		
		int i, gridX, gridZ, x, z;
		float distance;
		points:
		while (!processList.isEmpty())
		{
			point = processList.remove(random.nextInt(processList.size()));
			
			candidates:
			for (i = 0; i < newPointCount; i++)
			{
				distance = noise == null ? minDist : minDist + (maxDist - minDist) * (0.5F + noise.getNoise(point.x * noiseScale, point.z * noiseScale));
				
				newPoint = VectorF.obtain().set(0, 0, distance * (1 + random.nextFloat())).rotateY(360 * random.nextFloat()).add(point);
				if (newPoint.x < 0 || newPoint.z < 0 || newPoint.x >= width || newPoint.z >= height)
				{
					newPoint.release();
					continue;
				}
				
				gridX = (int) (newPoint.x / cellSize);
				gridZ = (int) (newPoint.z / cellSize);
				distance = noise == null ? minDist : minDist + (maxDist - minDist) * (0.5F + noise.getNoise(newPoint.x * noiseScale, newPoint.z * noiseScale));
				distance = distance * distance;
				
				for (x = -2; x <= 2; ++x)
					for (z = -2; z <= 2; ++z)
						if (x + gridX >= 0 && z + gridZ >= 0 && x + gridX < gridWidth && z + gridZ < gridHeight)
						{
							if (grid[(z + gridZ) * gridWidth + x + gridX] == null)
								continue;
								
							list = grid[(z + gridZ) * gridWidth + x + gridX];
							for (VectorF point1 : list)
								if (tmp.set(point1).subtract(newPoint).lengthSquared() < distance)
								{
									newPoint.release();
									continue candidates;
								}
						}
						
				processList.add(newPoint);
				samplePoints.add(newPoint);
				list = grid[gridZ * gridWidth + gridX];
				if (list == null)
					grid[gridZ * gridWidth + gridX] = list = new ArrayList();
				list.add(newPoint);
				maxPoints--;
				
				if (maxPoints == 0)
					break points;
			}
		}
		tmp.release();
		return samplePoints;
	}
}
