package co.allza.mararewards.items;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;
import java.lang.reflect.Field;
/**
 * Created by Tavo on 17/06/2016.
 */
public class CustomViewPager extends ViewPager {

    MyScroller elScroller;
    private boolean deshabilitarTouch=false;
    public CustomViewPager(Context context) {
        super(context);
        setMyScroller();
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMyScroller();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        for(int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if(h > height) height = h;
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        elScroller.setVelocidad(1500);
        super.setCurrentItem(item, smoothScroll);
        elScroller.setVelocidad(200);
            }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(deshabilitarTouch==true)
            return true;
        else
        return super.onTouchEvent(ev);
    }
    private void setMyScroller() {
        try {
            elScroller=new MyScroller(getContext());
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this, elScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isDeshabilitarTouch() {
        return deshabilitarTouch;
    }

    public void setDeshabilitarTouch(boolean deshabilitarTouch) {
        this.deshabilitarTouch = deshabilitarTouch;
    }

    public class MyScroller extends Scroller {

        private int velocidad=200;
        public MyScroller(Context context) {
            super(context, new DecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, velocidad );
        }

        public int getVelocidad() {
            return velocidad;
        }

        public void setVelocidad(int velocidad) {
            this.velocidad = velocidad;
        }
    }



}
