package com.taraxippus.yui.game;

import android.opengl.GLES20;
import com.taraxippus.yui.model.Model;
import com.taraxippus.yui.render.Renderer;
import com.taraxippus.yui.render.Shape;

public class OutlineSceneObject extends SceneObject
{
	protected Shape outlineShape;
	
	public OutlineSceneObject(World world)
	{
		super(world);
	}

	public OutlineSceneObject(World world, Model model)
	{
		super(world, model);
	}

	@Override
	public void init()
	{
		super.init();
		
		outlineShape = createOutlineShape();
	}
	
	@Override
	public void delete()
	{
		if (model != null)
			model.deleteSmoothShape();
		
		super.delete();
	}
	
	public Shape createOutlineShape()
	{
		return model == null ? null : model.getSmoothShape();
	}
	
	public void renderChildPass(Renderer renderer)
	{
		if (outlineShape == null)
			outlineShape = createOutlineShape();

		GLES20.glCullFace(GLES20.GL_FRONT);
		getPass().uniform(renderer, modelMatrix, normalMatrix);
		uniformChild();
		if (outlineShape != null)
			outlineShape.render();
		GLES20.glCullFace(GLES20.GL_BACK);
	}
}
