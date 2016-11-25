package com.taraxippus.yui.game;

import com.taraxippus.yui.game.*;
import com.taraxippus.yui.render.*;

public class GameObject implements Comparable<GameObject>
{
	public final World world;
	public Shape shape;
	private Pass pass;
	
	public float depthOffset;
	
	public GameObject(World world)
	{
		this.world = world;
		this.pass = world.main.getDefaultPass();
	}
	
	public void init()
	{
		this.shape = createShape();
	}
	
	public Shape createShape()
	{
		return null;
	}
	
	public void update()
	{
		
	}
	
	public void render(Renderer renderer)
	{
		if (shape != null)
			shape.render();
	}
	
	public boolean renderPass(Pass pass)
	{
		return getPass().getParent() == pass;
	}
	
	public Pass getPass()
	{
		return pass;
	}
	
	public GameObject setPass(Pass pass)
	{
		this.pass = pass;
		
		return this;
	}
	
	public GameObject setDepthOffset(float depthOffset)
	{
		this.depthOffset = depthOffset;
		
		return this;
	}
	
	public void delete()
	{
		if (shape != null && shape.initialized())
			shape.delete();
	}

	public float getDepth()
	{
		return depthOffset;
	}
	
	@Override
	public int compareTo(GameObject o)
	{
		return (int) Math.signum(-this.getDepth() + o.getDepth());
	}
}
