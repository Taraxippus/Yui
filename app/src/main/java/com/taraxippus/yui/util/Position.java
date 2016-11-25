package com.taraxippus.yui.util;

import com.taraxippus.yui.game.World;

public class Position
{
    public final VectorF position = new VectorF(), gravity = new VectorF();
    public final World world;

    public Position(World world, VectorF position)
    {
        this(world, position.x, position.y, position.z);
    }

    public Position(World world, float x, float y, float z)
    {
        this.world = world;
        this.position.set(x, y, z);
        this.gravity.set(this.world.getGravity(x, z));
    }
}
