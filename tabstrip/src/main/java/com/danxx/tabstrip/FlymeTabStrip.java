package com.danxx.tabstrip;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Flyme6 ViewPager指示器
 * @author Dawish_大D
 * 博客地址：http://blog.csdn.net/u010072711
 */
public class FlymeTabStrip extends HorizontalScrollView {
	private static final String TAG = "TabStrip";
	/**
	 * 指示器容器
	 */
	private LinearLayout container;
	/**
	 * 指示器个数
	 */
	private int tabCount;
	/**
	 * 当前tab位置，默认为0
	 */
	private int currentPosition = 0;
	/**
	 * 选中的tab位置
	 */
	private int selectedPosition;
	/**
	 *
	 */
	private float currentPositionOffset = 0f;
	/**
	 *
	 */
	private int lastScrollX = 0;
	/**
	 * LayoutParams用于添加指示器到指示器容器中时使用，按等权重分配指示器宽度
	 */
	private LinearLayout.LayoutParams expandedTabLayoutParams;
	/**
	 * 指示器颜色
	 */
	private int indicatorColor;
	/**
	 * 文字颜色
	 */
	private int textColor;
	/**
	 * 文字大小
	 */
	private int textSize;
	/**
	 * 选中位置的文字大小
	 */
	private int selectedTextSize;
	/**
	 * 指示器高度
	 */
	private int indicatorHeight;
	/**
	 * 指示器左右间距
	 */
	private int indicatorMargin;
	/**
	 * ViewPager
	 */
	private ViewPager viewPager;
	/**
	 * viewpager的适配器
	 */
	private PagerAdapter pagerAdapter;

	/**
	 * page改变监听器
	 */
	private final PagerStateChangeListener pagerStateChangeListener = new PagerStateChangeListener();
	/**
	 * 画笔
	 */
	private Paint paint;
	private Context context;

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public FlymeTabStrip(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context, attrs, defStyleAttr, defStyleRes);
	}

	public FlymeTabStrip(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr, 0);
	}

	public FlymeTabStrip(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0, 0);
	}

	public FlymeTabStrip(Context context) {
		super(context);
		init(context, null, 0, 0);
	}

	/**
	 * 初始化
	 *
	 * @param context
	 * @param attrs
	 * @param defStyleAttr
	 * @param defStyleRes
	 */
	private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		this.context = context;
		// 取消横向的滚动条
		setHorizontalScrollBarEnabled(false);
		// 指示器容器初始化
		container = new LinearLayout(context);
		container.setOrientation(LinearLayout.HORIZONTAL);
		container.setLayoutParams(new LayoutParams(
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT));
		// 添加指示器容器到scrollview
		addView(container);

		// 获取屏幕相关信息
		DisplayMetrics dm = getResources().getDisplayMetrics();

		TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.FlymeTabStrip, defStyleAttr, defStyleRes);
		int n = typedArray.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = typedArray.getIndex(i);
			if (attr == R.styleable.FlymeTabStrip_indicatorColor) {
				indicatorColor = typedArray.getColor(attr, Color.YELLOW);

				// 指示器高度，默认2
			} else if (attr == R.styleable.FlymeTabStrip_indicatorHeight) {
				indicatorHeight = typedArray.getDimensionPixelSize(attr, 2);

				// 指示器左右间距，默认20
			} else if (attr == R.styleable.FlymeTabStrip_indicatorMargin) {
				indicatorMargin = typedArray.getDimensionPixelSize(attr, 20);

				// 文字颜色,默认黑色
			} else if (attr == R.styleable.FlymeTabStrip_indicatorTextColor) {
				textColor = typedArray.getColor(attr, Color.BLACK);

				// 文字大小，默认15
			} else if (attr == R.styleable.FlymeTabStrip_indicatorTextSize) {
				textSize = typedArray.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
										TypedValue.COMPLEX_UNIT_SP, 15, dm)) / 3;

				// 选中项的文字大小，默认18
			} else if (attr == R.styleable.FlymeTabStrip_selectedIndicatorTextSize) {
				selectedTextSize = typedArray.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
										TypedValue.COMPLEX_UNIT_SP, 18, dm)) / 3;

			} else {
			}
		}
		// typedArray回收
		typedArray.recycle();

		expandedTabLayoutParams = new LinearLayout.LayoutParams(0,
				LayoutParams.MATCH_PARENT, 1.0f);
		// 初始化画笔
		paint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 如果指示器个数为0，直接结束绘画
		if (tabCount == 0) {
			return;
		}
		// 获取onMeasure后的高
		final int height = getHeight();
		/*
		 * 画指示器下方的线
		 */
		// 设置颜色
		paint.setColor(indicatorColor);
		// 当前指示tab位置
		View currentTab = container.getChildAt(currentPosition);
		// 当前tab的左边相对父容器的左边距
		float leftPadding = currentTab.getLeft();
		// 当前tab的右边相对于父容器左边距
		float rightPadding = currentTab.getRight();
		float tempPadding = 20;
		// 如果出现位移

		float centerPosition = 0.0f;

		if (currentPositionOffset >= 0f && currentPosition < tabCount - 1) {
			View nextTab = container.getChildAt(currentPosition + 1);
			final float nextTabLeft = nextTab.getLeft();
			final float nextTabRight = nextTab.getRight();
			// 位置是在滑动过程中不断变化的
			leftPadding = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * leftPadding);
			rightPadding = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * rightPadding);
		}
		centerPosition = (rightPadding - leftPadding)/2 + leftPadding;
		float left = centerPosition - tempPadding - formatPercent(currentPositionOffset)*tempPadding;
		float right = centerPosition + tempPadding + formatPercent(currentPositionOffset)*tempPadding;

		// 绘制
		canvas.drawRect(left, height - indicatorHeight, right, height, paint);

	}

	/**
	 * 设置ViewPager
	 *
	 * @param viewPager
	 */
	public void setViewPager(ViewPager viewPager) {
		this.viewPager = viewPager;
		if (viewPager.getAdapter() == null) {
			throw new IllegalStateException(
					"ViewPager does not has a adapter instance");
		} else {
			pagerAdapter = viewPager.getAdapter();
		}
		viewPager.addOnPageChangeListener(pagerStateChangeListener);
		update();
	}

	/**
	 * 更新界面
	 */
	private void update() {
		// 指示器容器移除所有子view
		container.removeAllViews();
		// 获取指示器个数
		tabCount = pagerAdapter.getCount();
		// 逐个添加指示器
		for (int i = 0; i < tabCount; i++) {
			addTab(i, pagerAdapter.getPageTitle(i));
		}
		// 更新Tab样式
		updateTabStyle();
		getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				getViewTreeObserver().removeOnGlobalLayoutListener(this);
				currentPosition = viewPager.getCurrentItem();
				scrollToChild(currentPosition, 0);
			}
		});
	}

	/**
	 * 滑动ScrollView
	 *
	 * @param position
	 * @param offset
	 */
	private void scrollToChild(int position, int offset) {
		if (tabCount == 0) {
			return;
		}
		int newScrollX = container.getChildAt(position).getLeft() + offset;
		if (newScrollX != lastScrollX) {
			lastScrollX = newScrollX;
			scrollTo(newScrollX, 0);
		}
	}

	/**
	 * 添加指示器
	 *
	 * @param position
	 * @param title
	 */
	private void addTab(final int position, CharSequence title) {
		TextView tvTab = new TextView(context);
		tvTab.setText(title);
		tvTab.setTextColor(textColor);
		tvTab.setTextSize(textSize);
		tvTab.setGravity(Gravity.CENTER);
		tvTab.setSingleLine();
		tvTab.setFocusable(true);
		tvTab.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewPager.setCurrentItem(position);
			}
		});
		tvTab.setPadding(indicatorMargin, 0, indicatorMargin, 0);
		container.addView(tvTab, position, expandedTabLayoutParams);
	}

	/**
	 * 更新指示器样式
	 */
	private void updateTabStyle() {
		for (int i = 0; i < tabCount; i++) {
			TextView tab = (TextView) container.getChildAt(i);
			if (i == selectedPosition) {
				// 设置选中的指示器文字颜色和大小
				tab.setTextColor(indicatorColor);
				tab.setTextSize(selectedTextSize);
			} else {
				tab.setTextColor(textColor);
				tab.setTextSize(textSize);
			}
		}
	}

	/**
	 * 对偏移百分比进行格式化 保证是一个小到大 然后到小的一个驼峰过程
	 * 这样才能保证滚动指示器是一个长度在随着滚动偏移不断变化的样子
	 * @param percent
	 * @return
     */
	private float formatPercent(float percent){

		float adsP = (float) Math.abs(percent - 0.5f);

		float valueP = Math.abs(0.5f - adsP);
		return valueP;
	}

	/**
	 * viewPager状态改变监听
	 *
	 */
	private class PagerStateChangeListener implements OnPageChangeListener {

		/**
		 * viewpager状态监听
		 * @param state
         */
		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_IDLE) {  // 0 空闲状态  pager处于空闲状态
				scrollToChild(viewPager.getCurrentItem(), 0);
			}else if(state == ViewPager.SCROLL_STATE_SETTLING){ // 2 正在自动沉降，相当于松手后，pager恢复到一个完整pager的过程

			}else if(state == ViewPager.SCROLL_STATE_DRAGGING){  // 1 viewpager正在被滑动,处于正在拖拽中

			}
		}

		/**
		 * viewpager正在滑动，会回调一些偏移量
		 * 滚动时，只要处理指示器下方横线的滚动
		 * @param position 当前页面
		 * @param positionOffset  当前页面偏移的百分比
		 * @param positionOffsetPixels 当前页面偏移的像素值
		 */
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			currentPosition = position;
			currentPositionOffset = positionOffset;
			// 处理指示器下方横线的滚动,scrollToChild会不断调用ondraw方法，绘制在重绘下划线，这就是移动动画效果
			scrollToChild(position, (int) (positionOffset * container.getChildAt(position).getWidth()));
			invalidate();
		}

		/**
		 * page滚动结束
		 * @param position  滚动结束后选中的页面
		 */
		@Override
		public void onPageSelected(int position) {
			// 滚动结束后的未知
			selectedPosition = position;
			// 更新指示器状态
			updateTabStyle();
		}

	}
}
