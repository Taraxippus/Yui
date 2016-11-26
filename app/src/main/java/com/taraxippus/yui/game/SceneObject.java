package com.taraxippus.yui.game;

import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;
import com.taraxippus.yui.game.World;
import com.taraxippus.yui.model.Model;
import com.taraxippus.yui.render.Pass;
import com.taraxippus.yui.render.Renderer;
import com.taraxippus.yui.render.Shape;
import com.taraxippus.yui.util.VectorF;

public class SceneObject extends GameObject
{
	public Model model;
	
	public final float[] modelMatrix = new float[16];
	public final float[] invModelMatrix = new float[16];
	public float[] normalMatrix = new float[16];
	
	public final VectorF color = new VectorF(0xCC / 255F, 0xCC / 255F, 0xCC / 255F);
	public float alpha = 1F;
	
	public final VectorF position = new VectorF();
	public final VectorF scale = new VectorF(1, 1, 1);
	public final VectorF rotation = new VectorF();

	public float radius;
	
	public boolean updateAlways = false, renderAlways = false, noUpdate = false;
	public boolean touchable = false;
	public boolean enabled = true;
	
	public SceneObject(World world)
	{
		this(world, null);
	}
	
	public SceneObject(World world, Model model)
	{
		super(world);
		
		this.setPass(world.main.getDefaultPass());
		this.setModel(model);
	}

	public SceneObject setModel(Model model)
	{
		this.model = model;
		if (model != null)
			this.setPass(model.pass);
			
		return this;
	}

	@Override
	public SceneObject setPass(Pass pass)
	{
		if (pass.usesMatrix("u_N"))
		{
			if (normalMatrix == null)
				normalMatrix = new float[16];
		}
		else if (normalMatrix != null)
			normalMatrix = null;
			
		return (SceneObject) super.setPass(pass);
	}
		
	public SceneObject setColor(int rgb)
	{
		this.color.set(Color.red(rgb) / 255F, Color.green(rgb) / 255F, Color.blue(rgb) / 255F);
		
		return this;
	}
	
	public SceneObject setAlpha(float alpha)
	{
		this.alpha = alpha;
		return this;
	}
	
	public SceneObject setTouchable(boolean touchable)
	{
		this.touchable = touchable;
		
		return this;
	}
	
	public void onTouch(VectorF intersection, VectorF normal)
	{
		
	}
	
	public void onLongTouch(VectorF intersection, VectorF normal)
	{

	}
	
	public void onSingleTouch(VectorF intersection, VectorF normal)
	{

	}
	
	public void onDoubleTouch(VectorF intersection, VectorF normal)
	{

	}
	
	public void rotateCamera(float[] viewMatrix)
	{
		
	}
	
	public float getCameraFOV()
	{
		return 1;
	}
	
	public SceneObject setEnabled(boolean enabled)
	{
		this.enabled = enabled;

		return this;
	}
	
	public SceneObject translate(VectorF translation)
	{
		return translate(translation.x, translation.y, translation.z);
	}
	
	public SceneObject translate(float x, float y, float z)
	{
		this.position.add(x, y, z);
		
		this.updateMatrix();
		
		return this;
	}
	
	public SceneObject rotate(float x, float y, float z)
	{
		this.rotation.add(x, y, z);
		
		this.updateMatrix();
		
		return this;
	}
	
	public SceneObject scale(float x, float y, float z)
	{
		this.scale.multiplyBy(x, y, z);
		
		this.updateMatrix();
		
		return this;
	}
	
	public void updateMatrix()
	{
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, position.x, position.y, position.z);
		Matrix.rotateM(modelMatrix, 0, rotation.y, 0, 1, 0);
		Matrix.rotateM(modelMatrix, 0, rotation.x, 1, 0, 0);
		Matrix.rotateM(modelMatrix, 0, rotation.z, 0, 0, 1);
		Matrix.scaleM(modelMatrix, 0, scale.x, scale.y, scale.z);
		
		Matrix.invertM(invModelMatrix, 0, modelMatrix, 0);
		
		if (normalMatrix != null)
		{
			Matrix.setIdentityM(normalMatrix, 0);
			Matrix.rotateM(normalMatrix, 0, rotation.y, 0, 1, 0);
			Matrix.rotateM(normalMatrix, 0, rotation.x, 1, 0, 0);
			Matrix.rotateM(normalMatrix, 0, rotation.z, 0, 0, 1);
		}
		
		this.radius = getRadius();
	}
	
	public float getRadius()
	{
		return (float) Math.sqrt(scale.x * scale.x * 0.5 * 0.5 + scale.y * scale.y * 0.5 * 0.5 + scale.z * scale.z * 0.5 * 0.5);
	}

	@Override
	public void init()
	{
		super.init();
		
		this.updateMatrix();
	}

	@Override
	public Shape createShape()
	{
		return model == null ? super.createShape() : model.getShape();
	}
	
	@Override
	public void render(Renderer renderer)
	{
		if (!enabled || !renderAlways && !world.main.camera.insideFrustum(position, radius))
		{
			noUpdate = !updateAlways;
			return;
		}
			
		noUpdate = false;
	
		getPass().getParent().uniform(renderer, modelMatrix, invModelMatrix);
		uniformParent();
		super.render(renderer);
		
		if (renderer.currentPass != getPass())
		{
			getPass().onRender(renderer);
			
			getPass().getParent().onRender(renderer);
		}
	}
	
	public void renderChildPass(Renderer renderer)
	{
		getPass().uniform(renderer, modelMatrix, normalMatrix);
		uniformChild();
		
		if (shape != null)
			shape.render();
	}

	@Override
	public void delete()
	{
		if (model == null)
			super.delete();
			
		else
		{
			model.deleteShape();
		}
	}
	
	public void uniformParent()
	{
		GLES20.glUniform4f(getPass().getParent().getProgram().getUniform("u_Color"), color.x, color.y, color.z, alpha);
	}

	public void uniformChild()
	{
		GLES20.glUniform4f(getPass().getProgram().getUniform("u_Color"), color.x * 0.5F, color.y * 0.5F, color.z * 0.5F, 1.0F);
	}
	
	@Override
	public float getDepth()
	{
		return super.getDepth() + VectorF.obtain().set(position).subtract(world.main.camera.eye).release().length();
	}
}
