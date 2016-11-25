package com.taraxippus.yui.game;

import android.view.MotionEvent;
import android.view.View;
import com.taraxippus.yui.Main;
import com.taraxippus.yui.render.Pass;
import com.taraxippus.yui.util.VectorF;

public abstract class Game implements View.OnTouchListener
{
	public final Main main;
	
	public Game(Main main)
	{
		this.main = main;
	}
	
	public abstract void init();
	
	public void update()
	{
		if (pointerRight != -1)
			main.camera.position.add(VectorF.obtain()
								 .set(newXRight - lastXRight, 0, newYRight - lastYRight)
								 .rotateX(main.camera.rotation.x)
								 .rotateY(main.camera.rotation.y)
								 .multiplyBy(Main.FIXED_DELTA * 4.5F)
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
	

	int pointerLeft = -1;
	int pointerRight = -1;

	float lastXLeft, lastYLeft;
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
						lastXLeft = event.getX(index) / (float) v.getWidth();
						lastYLeft = event.getY(index) / (float) v.getHeight();
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
					main.camera.rotation.y += (lastXLeft - event.getX(index) / v.getWidth()) * 180;
					main.camera.rotation.x += (lastYLeft - event.getY(index) / v.getHeight()) * 180;

					lastXLeft = event.getX(index) / (float) v.getWidth();
					lastYLeft = event.getY(index) / (float) v.getHeight();
				}

				index = event.findPointerIndex(pointerRight);

				if (index != -1)
				{
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
}
