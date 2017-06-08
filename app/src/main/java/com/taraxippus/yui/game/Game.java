package com.taraxippus.yui.game;

import android.view.MotionEvent;
import android.view.View;
import com.taraxippus.yui.Main;
import com.taraxippus.yui.render.Pass;
import com.taraxippus.yui.util.VectorF;

public abstract class Game implements View.OnTouchListener
{
	public final Main main;
	private boolean defaultCamera, invertMovementAxis, invertX, invertZ;
	
	public Game(Main main)
	{
		this.main = main;
	}
	
	public abstract void init();
	
	public void update()
	{
		if (pointerRight != -1 && defaultCamera)
			main.camera.position.add(VectorF.obtain()
									 .set(invertX ? lastXRight - newXRight : newXRight - lastXRight, invertMovementAxis ? (invertZ ? newYRight - lastYRight : lastYRight - newYRight) : 0, !invertMovementAxis ? (invertZ ? lastYRight - newYRight : newYRight - lastYRight) : 0)
								 .rotateX(main.camera.perspective ? main.camera.rotation.x : -90)
								 .rotateY(main.camera.rotation.y)
								 .multiplyBy(Main.FIXED_DELTA * Camera.Z_FAR * 0.25F)
								 .release());
								 
		main.camera.update();
		main.world.update();
	}
	
	public void updateReal() {}
	
	public void delete()
	{
		main.world.delete();
	}
	
	public void onTap(MotionEvent e) {}
	
	public void setUseDefaultCamera(boolean defaultCamera, boolean invertMovementAxis)
	{
		this.defaultCamera = defaultCamera;
		this.invertMovementAxis = invertMovementAxis;
	}
	
	public void invertMovement(boolean x, boolean z)
	{
		this.invertX = x;
		this.invertZ = z;
	}
	
	int pointerLeft = -1;
	int pointerRight = -1;

	float lastXLeft, lastYLeft, newXLeft, newYLeft;
	float lastXRight, lastYRight, newXRight, newYRight;

	@Override
    public boolean onTouch(View v, MotionEvent event)
    {
		int index = event.getActionIndex();
		int pointer = event.getPointerId(index);

        switch (event.getActionMasked())
		{
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
				if (event.getX(index) < v.getWidth() / 2F)
				{
					if (pointerLeft == -1)
					{
						pointerLeft = pointer;
						newXLeft = lastXLeft = event.getX(index) / (float) v.getWidth();
						newYLeft = lastYLeft = event.getY(index) / (float) v.getHeight();
					}
				}
				else if (pointerRight == -1)
				{
					pointerRight = pointer;
					newXRight = lastXRight = event.getX(index) / (float) v.getWidth();
					lastYRight = lastYRight = event.getY(index) / (float) v.getHeight();
				}

				break;
			case MotionEvent.ACTION_MOVE:
				index = event.findPointerIndex(pointerLeft);

				if (index != -1)
				{
					onMovePointerLeft(v, event, index);
					
					newXLeft = event.getX(index) / (float) v.getWidth();
					newYLeft = event.getY(index) / (float) v.getHeight();
				}
					
				index = event.findPointerIndex(pointerRight);

				if (index != -1)
				{
					onMovePointerRight(v, event, index);
					
					newXRight = event.getX(index) / (float) v.getWidth();
					newYRight = event.getY(index) / (float) v.getHeight();
				}
					
				break;

			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				pointerLeft = -1;
				pointerRight = -1;
				break;

			case MotionEvent.ACTION_POINTER_UP:
				if (pointer == pointerLeft)
					pointerLeft = -1;

				if (pointer == pointerRight)
					pointerRight = -1;

				break;
		}

        return true;
    }
	
	public void onMovePointerLeft(View v, MotionEvent event, int index)
	{
		if (defaultCamera)
		{
			main.camera.rotation.y += (newXLeft - event.getX(index) / v.getWidth()) * 180;
			main.camera.rotation.x += (newYLeft - event.getY(index) / v.getHeight()) * 180;
		}
	}
	
	public void onMovePointerRight(View v, MotionEvent event, int index) {}
}
