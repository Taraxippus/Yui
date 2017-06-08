package com.taraxippus.yui.render;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import com.taraxippus.yui.Main;
import com.taraxippus.yui.game.FullscreenQuad;
import com.taraxippus.yui.texture.Texture;
import com.taraxippus.yui.util.VectorF;
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Renderer implements GLSurfaceView.Renderer
{
	final Main main;
	
	public int width, height;
	final float[] mvpMatrix = new float[16];
	
	public Renderer(Main main)
	{
		this.main = main;
	}
	
	@Override
	public void onSurfaceCreated(GL10 p1, EGLConfig p2)
	{
		width = main.view.getWidth();
		height = main.view.getHeight();
		
		VectorF color = main.getClearColor();
		GLES20.glClearColor(color.x, color.y, color.z, 0);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glEnable(GLES20.GL_DITHER);
		GLES20.glCullFace(GLES20.GL_BACK);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glDepthMask(true);

		Texture.init();
		Pass.init();
		main.initPasses();
		main.camera.init();
		main.game.init();
		
		ArrayList<Pass> postPasses = new ArrayList<>();
		for (Pass p : main.getPasses())
		{
			if (p == null)
				System.err.println("A pass is null! This is probably not intended!");
				
			if (p instanceof Pass.Post)
				postPasses.add(p);
		}
			
		if (!postPasses.isEmpty())
		{
			if (main.getPostPass() == null)
				System.err.println("There are post passes, but main.getPostPass() returns null! This will probably not work as intended.");
				
			main.world.add(new FullscreenQuad(main.world, postPasses.toArray(new Pass[postPasses.size()])));
		}
	}

	@Override
	public void onSurfaceChanged(GL10 p1, int width, int height)
	{
		this.width = width;
		this.height = height;
		
		GLES20.glViewport(0, 0, width, height);
		
		main.camera.onResize(width, height);
	}

	public long lastTime;
	private float accumulator;
	private float accumulatorReal;
	
	private long lastFPSUpdate;
	private int frames;
	
	public float partial;
	public Pass currentPass;
	
	@Override
	public void onDrawFrame(GL10 p1)
	{
		if (lastTime == 0)
			lastTime = SystemClock.elapsedRealtime();
			
		float delta = (SystemClock.elapsedRealtime() - lastTime) / 1000F;
		lastTime = SystemClock.elapsedRealtime();
		
		accumulatorReal += delta;
		while (accumulatorReal >= Main.FIXED_DELTA)
		{
			main.game.updateReal();
			accumulatorReal -= Main.FIXED_DELTA;
		}
		
		delta *= main.timeFactor;
		
		accumulator += delta;
		while (accumulator >= Main.FIXED_DELTA)
		{
			main.game.update();
			accumulator -= Main.FIXED_DELTA;
		}
		
		partial = accumulator / Main.FIXED_DELTA;
		
		currentPass = null;
        try
        {
           	this.onRenderFrame();
            for (Pass pass : main.getPasses())
            {
                if (!pass.inOrder())
                    continue;

                pass.onRender(this);
                currentPass = pass;
				main.onRenderPass(pass);
                main.world.render(this, pass);
				pass.onFinishRender(this);
            }
        }
        catch (RuntimeException e) { e.printStackTrace(); }

		frames++;
		
		if (SystemClock.elapsedRealtime() - lastFPSUpdate >= 1000)
		{
			main.runOnUiThread(new Runnable()
			{
					@Override
					public void run()
					{
						main.textViewFPS.setText("" + frames);
						frames = 0;
					}
			});
	
			lastFPSUpdate = SystemClock.elapsedRealtime();
		}
	}
	
	public void onRenderFrame()
	{
		if (main.getPostPass() != null)
			main.getPostPass().getFramebuffer().bind();
			
		else
			Framebuffer.release(this);
			
		GLES20.glEnable(GLES20.GL_BLEND);
	}
	
	public void delete()
	{
		for (Pass p : main.getPasses())
			p.delete();
		main.game.delete();
	}
}
