package com.taraxippus.yui.render;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;
import com.taraxippus.yui.Main;
import com.taraxippus.yui.R;
import com.taraxippus.yui.game.FullscreenQuad;
import java.util.Random;

public class Pass
{
	private static int ID = 0;
	
	public final Pass parent;
	private final Program program;
	private final Framebuffer framebuffer;
	private final int[] attributes;
	private final String[] attributeNames, matrices;
	public final int ordinal;
	
	private boolean inOrder = true;
	
	public Pass(Main main, int vertex, int fragment, String[] attributeNames, int[] attributes, String[] matrices)
	{
		this(main, null, vertex, fragment, 0, 0, false, attributeNames, attributes, matrices);
	}
	
	public Pass(Main main, Pass parent, int vertex, int fragment, String[] attributeNames, int[] attributes, String[] matrices)
	{
		this(main, parent, vertex, fragment, 0, 0, false, attributeNames, attributes, matrices);
	}
	
	public Pass(Main main, Pass parent, int vertex, int fragment, int frameBufferWidth, int frameBufferHeight, boolean depthBuffer, String[] attributeNames, int[] attributes, String[] matrices)
	{
		this.parent = parent == null ? this : parent;
		this.ordinal = ID++;
		this.program = new Program();
		if (frameBufferWidth > 0)
		{
			this.framebuffer = new Framebuffer();
			this.framebuffer.init(depthBuffer, frameBufferWidth, frameBufferHeight);
		}
		else
			this.framebuffer = null;
			
		this.attributes = attributes;
		this.attributeNames = attributeNames;
		this.matrices = matrices;
		
		if (initInConstructor())
			initProgram(main, vertex, fragment);
	}
	
	public void initProgram(Main main, int vertex, int fragment)
	{
		this.program.init(modifyVertexShader(main.resourceHelper.getString(vertex)), modifyFragmentShader(main.resourceHelper.getString(fragment)), attributeNames);
		this.program.use();
	}
	
	public boolean initInConstructor()
	{
		return true;
	}
	
	protected String modifyVertexShader(String shader)
	{
		return shader;
	}
	
	protected String modifyFragmentShader(String shader)
	{
		return shader;
	}
	
	public Pass setInOrder(boolean inOrder)
	{
		this.inOrder = inOrder;
		
		return this;
	}
	
	public void delete()
	{
		if (program.initialized())
			program.delete();
				
		if (framebuffer != null && framebuffer.initialized())
			framebuffer.delete();
	}
	
	public Program getProgram()
	{
		return program;
	}
	
	public int[] getAttributes()
	{
		return attributes;
	}
	
	public String[] getAttributeNames()
	{
		return attributeNames;
	}
	
	public Framebuffer getFramebuffer()
	{
		return framebuffer;
	}
	
	public int getFramebufferTexUnit()
	{
		return -1;
	}
	
	public boolean inOrder()
	{
		return inOrder;
	}
	
	public Pass getParent()
	{
		return parent;
	}
	
	public void onRender(Renderer renderer)
	{
		this.getProgram().use();
	}
	
	public boolean usesAttribute(String attribute)
	{
		for (String s : attributeNames)
			if (s.equals(attribute))
				return true;

		return false;
	}
	
	public boolean usesMatrix(String matrix)
	{
		for (String s : matrices)
			if (s.equals(matrix))
				return true;
		
		return false;
	}
	
	public int getStride()
	{
		int stride = 0;
		for (int i : attributes)
			stride += i;
		
		return stride;
	}
	
	public int getAttributeIndex(String attribute)
	{
		int offset = 0;
		int i;
		for (i = 0; i < attributeNames.length; ++i)
		{
			if (attributeNames[i].equals(attribute))
				break;

			offset += attributes[i];
		}
		
		if (i == attributeNames.length)
			throw new RuntimeException("This pass has no attribute \"" + attributeNames + "\"!");
		
		return offset;
	}
	
	public void uniform(Renderer renderer, float[] modelMatrix, float[] normalMatrix)
	{
		for (String s : matrices)
		{
			if (s.equals("u_MVP"))
			{
				Matrix.multiplyMM(renderer.mvpMatrix, 0, renderer.main.camera.projectionViewMatrix, 0, modelMatrix, 0);
				GLES20.glUniformMatrix4fv(getProgram().getUniform(s), 1, false, renderer.mvpMatrix, 0);
			}
			else if (s.equals("u_MV"))
			{
				Matrix.multiplyMM(renderer.mvpMatrix, 0, renderer.main.camera.viewMatrix, 0, modelMatrix, 0);
				GLES20.glUniformMatrix4fv(getProgram().getUniform(s), 1, false, renderer.mvpMatrix, 0);
			}
			else if (s.equals("u_VP"))
				GLES20.glUniformMatrix4fv(getProgram().getUniform(s), 1, false, renderer.main.camera.projectionViewMatrix, 0);
			
			else if (s.equals("u_N"))
				GLES20.glUniformMatrix4fv(getProgram().getUniform(s), 1, false, normalMatrix, 0);
			
			else if (s.equals("u_M"))
				GLES20.glUniformMatrix4fv(getProgram().getUniform(s), 1, false, modelMatrix, 0);
			
			else if (s.equals("u_V"))
				GLES20.glUniformMatrix4fv(getProgram().getUniform(s), 1, false, renderer.main.camera.viewMatrix, 0);
			
			else if (s.equals("u_P"))
				GLES20.glUniformMatrix4fv(getProgram().getUniform(s), 1, false, renderer.main.camera.projectionMatrix, 0);
			
		}
	}
	
	public static void init()
	{
		ID = 0;
	}
	
	public static class Post extends Pass
	{
		final int textureUnit;
		
		public Post(Main main, int vertex, int fragment, int frameBufferWidth, int frameBufferHeight, boolean depthBuffer)
		{
			super(main, null, vertex, fragment, frameBufferWidth, frameBufferHeight, depthBuffer, new String[] { "a_Position", }, new int[] { 2 }, new String[] {});
			
			textureUnit = Texture.getFreeTextureUnit();
			getFramebuffer().bindTexture(textureUnit);
			
			if (initInConstructor())
				GLES20.glUniform1i(getProgram().getUniform("u_Texture"), textureUnit);
		}

		@Override
		public int getFramebufferTexUnit()
		{
			return textureUnit;
		}
		
		@Override
		public void onRender(Renderer renderer)
		{
			GLES20.glDisable(GLES20.GL_BLEND);
			if (this.ordinal == renderer.main.getPasses().length - 1)
			{
				GLES20.glDepthMask(true);
				Framebuffer.release(renderer);
			}
			
			super.onRender(renderer);
		}
	}
	
	public static class DefaultPost extends Post
	{
		private final float vignetteFactor, timeVignetteFactor;
		private final int[] combinePasses;
		
		private final Texture dither = new Texture();
		
		public DefaultPost(Main main, int frameBufferWidth, int frameBufferHeight, float vignetteFactor, float timeVignetteFactor, int[] combinePasses)
		{
			super(main, R.raw.vertex_post, R.raw.fragment_post, frameBufferWidth, frameBufferHeight, true);
			
			this.vignetteFactor = vignetteFactor;
			this.timeVignetteFactor = timeVignetteFactor;
			this.combinePasses = combinePasses;
			
			initProgram(main, R.raw.vertex_post, R.raw.fragment_post);
			GLES20.glUniform2f(getProgram().getUniform("u_InvResolution"), 1F / getFramebuffer().width, 1F / getFramebuffer().height);
			
			int ditherTexUnit = Texture.getFreeTextureUnit();
			
			final int[] colors = new int[main.renderer.width * main.renderer.height];
			int gray;
			final Random random = new Random();

			for (int i = 0; i < colors.length; ++i)
			{
				gray = random.nextInt(256);
				colors[i] = Color.rgb(gray, gray, gray);
			}

			dither.init(Bitmap.createBitmap(colors, 0, main.renderer.width, main.renderer.width, main.renderer.height, Bitmap.Config.RGB_565), GLES20.GL_NEAREST, GLES20.GL_NEAREST, GLES20.GL_CLAMP_TO_EDGE);
			dither.bind(ditherTexUnit);
			
			GLES20.glUniform1i(getProgram().getUniform("u_Texture"), getFramebufferTexUnit());
			GLES20.glUniform1i(getProgram().getUniform("u_Dither"), ditherTexUnit);
			for (int i = 0; i < combinePasses.length; ++i)
				GLES20.glUniform1i(getProgram().getUniform("u_Combine" + i), combinePasses[i]);
		}

		@Override
		public boolean initInConstructor()
		{
			return false;
		}

		@Override
		protected String modifyFragmentShader(String shader)
		{
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < combinePasses.length; ++i)
				sb.append("uniform sampler2D u_Combine" + i + ";\n");
			
			shader = shader.replace("*COMBINE_DECLARATION*", sb.toString());
			
			sb.setLength(0);
			for (int i = 0; i < combinePasses.length; ++i)
				sb.append("gl_FragColor += texture2D(u_Combine" + i + ", v_UV);\n");
	
			shader = shader.replace("*COMBINE*", sb.toString());
			
			return shader;
		}

		@Override
		public void onRender(Renderer renderer)
		{
			super.onRender(renderer);
			
			GLES20.glUniform1f(getProgram().getUniform("u_VignetteFactor"), vignetteFactor + timeVignetteFactor / renderer.main.timeFactor);
		}
	}
	
	public static class Bloom extends Post
	{
		final float r, g, b, constant;
		
		public Bloom(Main main, int frameBufferWidth, int frameBufferHeight, boolean dir, float r, float g, float b, float constant)
		{
			super(main, R.raw.vertex_bloom, R.raw.fragment_bloom1, frameBufferWidth, frameBufferHeight, false);

			this.r = r;
			this.g = g;
			this.b = b;
			this.constant = constant;
			
			initProgram(main, R.raw.vertex_bloom, R.raw.fragment_bloom1);
			GLES20.glUniform2f(getProgram().getUniform("u_InvResolution"), 1F / getFramebuffer().width, 1F / getFramebuffer().height);
			GLES20.glUniform2f(getProgram().getUniform("u_Dir"), dir ? 1 : 0, dir ? 0 : 1);
		}
		
		public Bloom(Main main, int frameBufferWidth, int frameBufferHeight, boolean dir)
		{
			super(main, R.raw.vertex_bloom, R.raw.fragment_bloom2, frameBufferWidth, frameBufferHeight, false);
			
			r = g = b = 0;
			constant = Float.NaN;
			
			initProgram(main, R.raw.vertex_bloom, R.raw.fragment_bloom2);
			GLES20.glUniform2f(getProgram().getUniform("u_InvResolution"), 1F / getFramebuffer().width, 1F / getFramebuffer().height);
			GLES20.glUniform2f(getProgram().getUniform("u_Dir"), dir ? 1 : 0, dir ? 0 : 1);
		}

		@Override
		public boolean initInConstructor()
		{
			return false;
		}
		
		@Override
		protected String modifyFragmentShader(String shader)
		{
			shader = shader.replace("*Filter*", "vec4(pixel.rgb * max(0.0, length(pixel.rgb * vec3(" + r + ", " + g + ", " + b + ")) + " + constant +"), 1.0)");
			
			return super.modifyFragmentShader(shader);
		}
		
		public void setInputTexture(int texUnit)
		{
			getProgram().use();
			GLES20.glUniform1i(getProgram().getUniform("u_Texture"), texUnit);
		}

		@Override
		public void onRender(Renderer renderer)
		{
			getFramebuffer().bind();
			
			super.onRender(renderer);
		}
	}
}
