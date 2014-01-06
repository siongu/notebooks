package com.android.note.views;

import com.xzwxn.notes.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

public class DividerEditText extends EditText {
	private final int LINES = 20;
	private final int OFFSET = 8;
	// 获取行高
	private int lineHeight;
	private boolean flag = true;
	private int num;
	private int dy;
	private int lineCount;
	private int baseline;
	private int width;
	private int padleft;
	private int padright;
	private Bitmap bm;
	private Paint mPaint = new Paint();
	private Rect r = new Rect();

	public DividerEditText(Context context) {
		super(context);
	}

	public DividerEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint.setColor(Color.BLACK);
		padleft = getPaddingLeft();
		padright = getPaddingRight();
		bm = BitmapFactory.decodeResource(super.getResources(),
				R.drawable.dash_line);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 获取第一行基准线在垂直方向上的偏移位置
		baseline = getLineBounds(0, r);
		//获取当前内容的行数
		lineCount = getLineCount();
		// 获取当前View的宽度
		width = getWidth();
		while (flag) {
			flag = false;
			setText(getText().toString().trim() + "\n");
			// 计算的出行高，计算方式为先通过换行符增加一行，然后通过这两行的基准线baseline（即每行的垂直偏移量）
			//的差进行计算得出
			lineHeight = getLineBounds(1, r) - baseline;
			// System.out.println("lineHeight="+lineHeight);
		}
		/**
		 * 如果当前文本内容的行数(num)小于LINES行的话就按默认的LINES进行绘制分割线的条数
		 * 否则就按当前的行数绘制分割线的条数
		 */
		num = lineCount > LINES ? lineCount : LINES;
		for (int m = 0; m < num; m++) {
			// System.out.println(getLineBounds(m,r));
			// System.out.println(getLineHeight());
			/**
			 *通过getLineHeight()获得行高和通过baseline计算的出的行高是有点小的误差的
			 *通过baseline获得行高与与实际的 行高是一致的
			 */
			dy = baseline + OFFSET + m * lineHeight;
			/**
			 * 绘制bitmap图片作为分割线，bitmap的高为1pixels，由(dy，dy+1)决定
			 */
			canvas.drawBitmap(bm, null, new Rect(padleft, dy, width - padright,
					dy + 1), mPaint);
			/**
			 *绘制一条直线
			 */
			// canvas.drawLine(padleft, baseline+10, width-padright,
			// baseline+10, mPaint);
		}
	}
}
