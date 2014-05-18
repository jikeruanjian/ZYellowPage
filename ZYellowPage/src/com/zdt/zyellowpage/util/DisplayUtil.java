package com.zdt.zyellowpage.util;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class DisplayUtil {
	
	/**
	 * 设计默认宽度
	 */
	private static final int DESIGN_DEFAULT_WIDTH = 480;
	/**
	 * 设计默认高度
	 */
	private static final int DESIGN_DEFAULT_HEIGHT = 800;
	/**
	 * 设计默认动态图片最高高度比例
	 */
	public static final float VIEWPAGER_IMAGE_MAX_HEIGHT_SCALE = 0.44f;
	/**
	 * 不处理动态图片最大比例
	 */
	public static final float VIEWPAGER_IMAGE_MAX_SCALE_NO_USE = -1;
	
	private int windowWidth;
	private int windowHeight;
	private float widthScale;
	private float heightScale;
	private float density;
	private float scaledDensity;
	private float realDensity;
	private float realDensityDpi;
	private static DisplayUtil displayUtil = null;
	
	public static DisplayUtil getInstance(Context context) {
		if (displayUtil == null) {
			displayUtil = new DisplayUtil(context);
		}
		return displayUtil;
	}
	
	private DisplayUtil(Context context) {
		// TODO 注：由于目前设计来不及制作、项目工期紧，来不及使用.9patch及各dpi配置文件适配各种屏幕尺寸，暂时使用最简单的方法，通过真实屏幕与设计图比例来计算实际显示大小
		// 在配置anyDensity false后，实际分别率被设置为360*640， density为1
		DisplayMetrics dm = new DisplayMetrics();
		dm.setToDefaults();
		realDensity = dm.density;
		realDensityDpi = dm.densityDpi;
		
		dm = context.getResources().getDisplayMetrics();
		windowWidth = dm.widthPixels;
		windowHeight = dm.heightPixels;
		widthScale = (float)windowWidth / (float)DESIGN_DEFAULT_WIDTH;
		heightScale = (float)windowHeight / (float)DESIGN_DEFAULT_HEIGHT;
		density = dm.density;
		scaledDensity = dm.scaledDensity;
		
	}
	
	/** 
     * 将px值转换为dip或dp值，保证尺寸大小不变 
     *  
     * @param pxValue 
     * @param scale 
     *            （DisplayMetrics类中属性density） 
     * @return 
     */  
    public int px2dip(float pxValue) {  
        return (int) (pxValue / density + 0.5f);  
    }  
  
    /** 
     * 将dip或dp值转换为px值，保证尺寸大小不变 
     *  
     * @param dipValue 
     * @param scale 
     *            （DisplayMetrics类中属性density） 
     * @return 
     */  
    public int dip2px(float dipValue) {  
        return (int) (dipValue * density + 0.5f);  
    }  
  
    /** 
     * 将px值转换为sp值，保证文字大小不变 
     *  
     * @param pxValue 
     * @param fontScale 
     *            （DisplayMetrics类中属性scaledDensity） 
     * @return 
     */  
    public int px2sp(Context context, float pxValue) {  
        return (int) (pxValue / scaledDensity + 0.5f);  
    }  
  
    /** 
     * 将sp值转换为px值，保证文字大小不变 
     *  
     * @param spValue 
     * @param fontScale 
     *            （DisplayMetrics类中属性scaledDensity） 
     * @return 
     */  
    public int sp2px(Context context, float spValue) {  
        return (int) (spValue * scaledDensity + 0.5f);  
    }  
    
    /**
     * 获取实际宽度
     * @param pxValue
     * @return
     */
    public int getActualWidth(float pxValue) {
    	return (int) (pxValue * widthScale);
    }
    
    /**
     * 获取实际高度
     * @param context
     * @param pxValue
     * @return
     */
    public int getActualHeight(float pxValue) {
    	return (int) (pxValue * heightScale);
    }
    
    /**
     * 通过measure获取View实际的LayoutParam
     * @param view
     * @return
     */
    public LinearLayout.LayoutParams getActualLayoutParams(View view) {
    	// 需要测量计算，性能可能不是最优
    	int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED); 
    	int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED); 
    	view.measure(w, h); 
    	int height = view.getMeasuredHeight(); 
    	int width = view.getMeasuredWidth();
    	return new LinearLayout.LayoutParams(this.getActualWidth(width), this.getActualHeight(height));
    }
    
    /**
     * 直接获取Drawable实际的LayoutParam
     * @param view
     * @return
     */
    public LayoutParams getActualLayoutParams(Drawable drawable) {
    	return new LayoutParams(this.getActualWidth(drawable.getIntrinsicWidth()), this.getActualHeight(drawable.getIntrinsicHeight()));
    }
    
    /**
     * 不需要margin的设置视图尺寸
     * @param view
     * @param drawable
     */
    public void setViewLayoutParams(View view, Drawable drawable) {
    	view.setLayoutParams(this.getActualLayoutParams(drawable));
	}
    
    /**
     * 不需要margin的设置视图尺寸
     * @param view
     * @param drawable
     * @param maxWidthScale 最大宽度比例
     * @param maxHeightScale 最大高度比例
     */
    public void setViewLayoutParams(View view, Drawable drawable, float maxWidthScale, float maxHeightScale) {
    	this.setViewWidthLayoutParams(view, drawable, maxWidthScale);
    	this.setViewHeigthLayoutParams(view, drawable, maxHeightScale);
    }
    
    /**
     * 不需要margin的设置视图宽度
     * @param view
     * @param drawable
     * @param maxWidthScale 最大宽度比例
     */
    public void setViewWidthLayoutParams(View view, Drawable drawable, float maxWidthScale) {
    	LayoutParams lp = view.getLayoutParams();
    	int width = this.getActualWidth(drawable.getIntrinsicWidth());
    	if (maxWidthScale != VIEWPAGER_IMAGE_MAX_SCALE_NO_USE) {
	    	int maxWidth = (int) (this.windowWidth * maxWidthScale);
	    	if (width > maxWidth) {
	    		width = maxWidth;
	    	}
    	}
    	lp.width = width;
    	view.setLayoutParams(lp);
	}
    
    /**
     * 不需要margin的设置视图高度
     * @param view
     * @param drawable
     * @param maxHeightScale 最大宽度比例
     */
    public void setViewHeigthLayoutParams(View view, Drawable drawable, float maxHeightScale) {
    	LayoutParams lp = view.getLayoutParams();
    	int height = this.getActualHeight(drawable.getIntrinsicHeight());
    	if (maxHeightScale != VIEWPAGER_IMAGE_MAX_SCALE_NO_USE) {
    		int maxHeight = (int) (this.windowHeight * maxHeightScale);
        	if (height > maxHeight) {
        		height = maxHeight;
        	}
    	}
    	lp.height = height;
    	
    	view.setLayoutParams(lp);
	}
    
    /**
     * 默认父布局是LinearLayout的设置LayoutParams的尺寸
     * @param view
     * @param drawable
     * @param margin
     */
    public void setViewLayoutParams(View view, Drawable drawable, int margin) {
		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(this.getActualLayoutParams(drawable));
		layout.setMargins(margin, margin, margin, margin);
		view.setLayoutParams(layout);
	}
    /**
     * 设置按钮或者图片背景自适应屏幕---高宽
     * @param view
     * @param drawable
     * @param w
     */
    public void setViewLayoutParamsW(View view, Drawable drawable, int w,int h) {
    	LinearLayout.LayoutParams layout = (LinearLayout.LayoutParams)view.getLayoutParams();
		//layout.height = h;
		layout.width = w;
		view.setLayoutParams(layout);
	}
    /**
     * 将宽度设置成屏幕的x分支1
     * @param view
     * @param w
     */
    public void setViewLayoutParamsByX(View view, int x, int w) {
    	LinearLayout.LayoutParams layout = (LinearLayout.LayoutParams)view.getLayoutParams();
		layout.width = w/x;
		view.setLayoutParams(layout);
	}
    public void setViewLayoutParamsTextView(View view,int w) {
    	LinearLayout.LayoutParams layout = (LinearLayout.LayoutParams)view.getLayoutParams();
		layout.height = w/2;
		view.setLayoutParams(layout);
	}
    public void setViewLayoutParamsTextViewC(View view,int w,int h) {
    	LinearLayout.LayoutParams layout = (LinearLayout.LayoutParams)view.getLayoutParams();
		layout.height = h/2;
		layout.width = (5*w)/2;
		view.setLayoutParams(layout);
	}
    public void setViewLayoutParamsR(View view,int w,int h) {
    	RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams)view.getLayoutParams();
    	if(w != 0){
    		layout.width = w;
    	}
    	if(h != 0){
    		layout.height = h;
    	}
		view.setLayoutParams(layout);
	}
    
    public void setViewLayoutParamsL(View view,int w,int h) {
    	LinearLayout.LayoutParams layout = (LinearLayout.LayoutParams)view.getLayoutParams();
    	if(w != 0){
    		layout.width = w;
    	}
    	if(h != 0){
    		layout.height = h;
    	}
		view.setLayoutParams(layout);
	}
    
    public void setViewLayoutParamsLayout(View view,int w) {
    	LinearLayout.LayoutParams layout = (LinearLayout.LayoutParams)view.getLayoutParams();
		layout.height = w+w/5;
		view.setLayoutParams(layout);
	}
    /**
     * 设置按钮或者图片背景自适应屏幕---宽度
     * @param view
     * @param drawable
     * @param w
     */
    public void setViewLayoutParamsX(View view, Drawable drawable, int w) {
		LinearLayout.LayoutParams layout = (LinearLayout.LayoutParams)view.getLayoutParams();
		double f = w/layout.width;
		//layout.height =(int) (layout.height*f); 
		layout.width = w;
		view.setLayoutParams(layout);
	}
    /**
     * 默认父布局是FrameLayout的设置LayoutParams的尺寸
     * @param view
     * @param drawable
     * @param margin
     */
    public void setViewFrameLayoutParams(View view, Drawable drawable, int margin) {
    	FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(this.getActualLayoutParams(drawable));
		layout.setMargins(margin, margin, margin, margin);
		view.setLayoutParams(layout);
	}
    
    /**
     * 按屏幕比例缩放图片按钮
     * @param imageButton
     */
    public void setImageViewSize(ImageView imageView) {
		Matrix matrix = new Matrix();
		matrix.set(imageView.getImageMatrix());
		matrix.postScale(this.getWidthScale(), this.getHeightScale());
		imageView.setImageMatrix(matrix);
	}
    
    /**
     * 按屏幕比例缩放图片按钮
     * @param imageButton
     */
    public Drawable getDrawableSize(Resources res, Drawable drawable) {
    	return zoomDrawable(res, drawable, this.getWidthScale(), this.getHeightScale());
	}
    
    public Drawable getDrawableSizeW(Resources res, Drawable drawable,int w) {
    	return zoomDrawable(res, drawable, w, w);
	}
    
    /**
     * drawable 转换成 bitmap 
     * @param drawable
     * @return
     */
    public Bitmap drawableToBitmap(Drawable drawable) {
    	int width = drawable.getIntrinsicWidth();   // 取 drawable 的长宽 
    	int height = drawable.getIntrinsicHeight();
    	Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565;         // 取 drawable 的颜色格式 
    	Bitmap bitmap = Bitmap.createBitmap(width, height, config);     // 建立对应 bitmap 
    	Canvas canvas = new Canvas(bitmap);         // 建立对应 bitmap 的画布 
    	drawable.setBounds(0, 0, width, height);
    	drawable.draw(canvas);      // 把 drawable 内容画到画布中 
    	return bitmap;
    }

	public Drawable zoomDrawable(Resources res, Drawable drawable, float widthScale, float heightScale) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap oldbmp = drawableToBitmap(drawable); // drawable 转换成 bitmap
		Matrix matrix = new Matrix(); // 创建操作图片用的 Matrix 对象
		matrix.postScale(widthScale, heightScale); // 设置缩放比例
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
				matrix, true); // 建立新的 bitmap ，其内容是对原 bitmap 的缩放后的图
		return new BitmapDrawable(res, newbmp); // 把 bitmap 转换成 drawable 并返回
	}
    
	public  void setListViewHeightBasedOnChildren(ListView listView, int attHeight) {  
        ListAdapter listAdapter = listView.getAdapter();   
        if (listAdapter == null) {  
            // pre-condition  
            return;  
        }  
  
        int totalHeight = 0;  
        for (int i = 0; i < listAdapter.getCount(); i++) {  
            View listItem = listAdapter.getView(i, null, listView);  
            listItem.measure(0, 0);  
            totalHeight += listItem.getMeasuredHeight();  
        }  
  
        ViewGroup.LayoutParams params = listView.getLayoutParams();  
       // params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + attHeight;  
        params.height =getActualHeight(910);
        listView.setLayoutParams(params);  
    }  


	public int getWindowWidth() {
		return windowWidth;
	}

	public int getWindowHeight() {
		return windowHeight;
	}

	public float getWidthScale() {
		return widthScale;
	}

	public float getHeightScale() {
		return heightScale;
	}

	public float getDensity() {
		return density;
	}

	public float getScaledDensity() {
		return scaledDensity;
	}

	public float getRealDensity() {
		return realDensity;
	}

	public float getRealDensityDpi() {
		return realDensityDpi;
	}
	
}
