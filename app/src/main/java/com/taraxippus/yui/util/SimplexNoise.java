package com.taraxippus.yui.util;

import java.util.Random;

public class SimplexNoise
{
    SimplexNoise_octave[] octaves;

    float[] frequencies;
    float[] amplitudes;

    int largestFeature;
    double persistence;
    long seed;

    public final int reduction = 1;

    public SimplexNoise(int largestFeature, double persistence, long seed)
    {
        this.largestFeature = largestFeature;
        this.persistence = persistence;
        this.seed = seed;

        int numberOfOctaves = (int) Math.ceil(Math.log10(largestFeature) / Math.log10(2));

        octaves = new SimplexNoise_octave[numberOfOctaves / reduction];
        frequencies = new float[numberOfOctaves / reduction];
        amplitudes = new float[numberOfOctaves / reduction];

        Random rnd = new Random(seed);

        for (int i = 0; i < numberOfOctaves / reduction; i++)
        {
            octaves[i] = new SimplexNoise_octave(rnd.nextLong());

            frequencies[i] = (float)Math.pow(2, i * reduction);
            amplitudes[i] = (float)Math.pow(persistence, (octaves.length * reduction - i));
        }
    }

    public float getNoise(final float x, final float y)
    {
        float result = 0;

        for (int i = 0; i < octaves.length; i++)
        {
            result += octaves[i].noise(x / frequencies[i], y / frequencies[i]) * amplitudes[i];
        }

        return result;
    }
}
