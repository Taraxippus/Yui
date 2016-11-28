package com.taraxippus.yui.util;

import java.util.Random;

public class SimplexNoise
{
    final SimplexNoise_octave[] octaves;

    float[] frequencies;
    float[] amplitudes;

    int largestFeature;
    double persistence;
    long seed;

    public SimplexNoise(int largestFeature, double persistence, long seed)
    {
        this.largestFeature = largestFeature;
        this.persistence = persistence;
        this.seed = seed;

        int numberOfOctaves = (int) Math.ceil(Math.log10(largestFeature) / Math.log10(2));

        octaves = new SimplexNoise_octave[numberOfOctaves];
        frequencies = new float[numberOfOctaves];
        amplitudes = new float[numberOfOctaves];

        final Random rnd = new Random(seed);
		float sum = 0;
        for (int i = 0; i < numberOfOctaves; i++)
        {
            octaves[i] = new SimplexNoise_octave(rnd.nextLong());

            frequencies[i] = (float)Math.pow(2, i);
            sum += amplitudes[i] = (float)Math.pow(persistence, octaves.length - i);
        }
		
		for (int i = 0; i < numberOfOctaves; i++)
			amplitudes[i] /= sum;
    }

    public float getNoise(final float x, final float y)
    {
        float result = 0;

        for (int i = 0; i < octaves.length; i++)
        {
            result += octaves[i].noise(x / frequencies[i], y / frequencies[i]) * amplitudes[i];
        }

		System.out.println(result);
		
        return result;
    }
}
