package com.taraxippus.yui.game;

import com.taraxippus.yui.*;
import com.taraxippus.yui.util.*;
import java.nio.*;

public class Particle
{
	public final VectorF rotPosition = new VectorF();
    public final VectorF prevRotPosition = new VectorF();
    public final VectorF position = new VectorF();
    public final VectorF prevPosition = new VectorF();
    public final VectorF color = new VectorF();
    public final VectorF prevColor = new VectorF();
    public final VectorF velocity = new VectorF();

    public final VectorF color_start = new VectorF(), color_end = new VectorF();

    public float size = 0;
    public float minSize = 0F, maxSize = 1.15F;

	public float alpha = 1F;
	
    public float lifeTime;
    public float maxLifeTime;

    public float rotVelocity;
    public float minRadius, maxRadius;

    public float rotation;
    public float acceleration;
    public boolean alive = true;
	
	public final ParticleEmitter emitter;
	
	public Particle(ParticleEmitter emitter)
	{
		this.emitter = emitter;
	}
	
	public void update()
	{
		this.lifeTime -= Main.FIXED_DELTA;

        if (this.lifeTime < 0)
        {
            if (this.alive)
                emitter.spawn(this);

            return;
        }

        this.size = minSize + maxSize * (this.lifeTime / this.maxLifeTime);

        this.prevColor.set(color);
        this.color.approach(this.color_start, this.color_end, this.lifeTime / this.maxLifeTime);

        this.prevPosition.set(this.position);
        this.position.add(velocity.x * Main.FIXED_DELTA, velocity.y * Main.FIXED_DELTA, velocity.z * Main.FIXED_DELTA);

        this.prevRotPosition.set(this.rotPosition);
        this.rotPosition.set(0, 0, minRadius + (maxRadius - minRadius) * (1 - this.lifeTime / this.maxLifeTime)).rotateY(this.rotation);

        this.rotation += this.rotVelocity * Main.FIXED_DELTA;

        this.velocity.multiplyBy(acceleration);
        this.rotVelocity *= acceleration;
		
//		if (emitter.world.main.level.isBlocked(null, (int) (emitter.position.x + position.x), (int) (emitter.position.y + position.y), (int) (emitter.position.z + position.z)))
//			this.position.set(prevPosition);
	}
	
	public void buffer(FloatBuffer vertices, float partial)
	{
		vertices.put((position.x + rotPosition.x) * partial + (prevPosition.x + prevRotPosition.x) * (1 - partial));
        vertices.put((position.y + rotPosition.y) * partial + (prevPosition.y + prevRotPosition.y) * (1 - partial));
        vertices.put((position.z + rotPosition.z) * partial + (prevPosition.z + prevRotPosition.z) * (1 - partial));
        vertices.put(size);

        vertices.put(color.x * partial + prevColor.x * (1 - partial));
        vertices.put(color.y * partial + prevColor.y * (1 - partial));
        vertices.put(color.z * partial + prevColor.z * (1 - partial));
		vertices.put(alpha);
		
		vertices.put(-1);
		vertices.put(-1);
		
		
		vertices.put((position.x + rotPosition.x) * partial + (prevPosition.x + prevRotPosition.x) * (1 - partial));
        vertices.put((position.y + rotPosition.y) * partial + (prevPosition.y + prevRotPosition.y) * (1 - partial));
        vertices.put((position.z + rotPosition.z) * partial + (prevPosition.z + prevRotPosition.z) * (1 - partial));
        vertices.put(size);

        vertices.put(color.x * partial + prevColor.x * (1 - partial));
        vertices.put(color.y * partial + prevColor.y * (1 - partial));
        vertices.put(color.z * partial + prevColor.z * (1 - partial));
		vertices.put(alpha);
		
		vertices.put(1);
		vertices.put(-1);
		
		
		vertices.put((position.x + rotPosition.x) * partial + (prevPosition.x + prevRotPosition.x) * (1 - partial));
        vertices.put((position.y + rotPosition.y) * partial + (prevPosition.y + prevRotPosition.y) * (1 - partial));
        vertices.put((position.z + rotPosition.z) * partial + (prevPosition.z + prevRotPosition.z) * (1 - partial));
        vertices.put(size);

        vertices.put(color.x * partial + prevColor.x * (1 - partial));
        vertices.put(color.y * partial + prevColor.y * (1 - partial));
        vertices.put(color.z * partial + prevColor.z * (1 - partial));
		vertices.put(alpha);
		
		vertices.put(-1);
		vertices.put(1);
		
		
		vertices.put((position.x + rotPosition.x) * partial + (prevPosition.x + prevRotPosition.x) * (1 - partial));
        vertices.put((position.y + rotPosition.y) * partial + (prevPosition.y + prevRotPosition.y) * (1 - partial));
        vertices.put((position.z + rotPosition.z) * partial + (prevPosition.z + prevRotPosition.z) * (1 - partial));
        vertices.put(size);

        vertices.put(color.x * partial + prevColor.x * (1 - partial));
        vertices.put(color.y * partial + prevColor.y * (1 - partial));
        vertices.put(color.z * partial + prevColor.z * (1 - partial));
		vertices.put(alpha);
		
		vertices.put(1);
		vertices.put(1);
	}
}
