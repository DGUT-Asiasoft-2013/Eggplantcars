package inputcells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class MailLineView extends View {
	// ��ɫ��Ŀ�ȣ��ɶ����XML���ԣ���ɫֵҲ���Զ���ΪXML���ԣ������д���
	private int colorWidth = 7; // �հ׿�Ŀ��
	private int emptyWidth = 1;

	public MailLineView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public MailLineView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MailLineView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// ��ȡView���
		int viewHeight = getHeight();
		// ������ɵĳ���
		int drawLength = 0;
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		int count = 0;
		// ʹ��canvasѭ��������ɫ��
		while (drawLength < getWidth()) {
			drawLength += emptyWidth * viewHeight;
			count++;
			// �л���ɫ
			if (count % 2 == 1) {
				paint.setColor(Color.rgb(255, 134, 134));
			} else {
				paint.setColor(Color.rgb(134, 194, 255));
			}
			// ʹ��·������һ������
			Path path = new Path();
			path.moveTo(drawLength, viewHeight);
			// �˵�Ϊ����ε����
			path.lineTo(drawLength + colorWidth * viewHeight - viewHeight, viewHeight);
			path.lineTo(drawLength + colorWidth * viewHeight, 0);
			path.lineTo(drawLength + viewHeight, 0);
			path.close(); // ʹ��Щ�㹹�ɷ�յĶ����
			canvas.drawPath(path, paint);
			drawLength += colorWidth * viewHeight;
		}
	}
}