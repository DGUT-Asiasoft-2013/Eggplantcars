package inputcells.utils;

import android.os.Handler;
import android.os.Message;
import inputcells.utils.PullToRefreshLayout.OnRefreshListener;

public class MyListener implements OnRefreshListener {

	@Override
	public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
		// TODO Auto-generated method stub
		// ����ˢ�²���
				new Handler()
				{
					@Override
					public void handleMessage(Message msg)
					{
						// ǧ������˸��߿ؼ�ˢ�������Ŷ��
						//pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
					}
				}.sendEmptyMessageDelayed(0, 5000);
		
	}

	@Override
	public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
		// TODO Auto-generated method stub
		// ���ز���
				new Handler()
				{
					@Override
					public void handleMessage(Message msg)
					{
						// ǧ������˸��߿ؼ����������Ŷ��
						//pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
					}
				}.sendEmptyMessageDelayed(0, 5000);
	}

}
