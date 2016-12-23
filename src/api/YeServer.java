package api;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class YeServer {

	// ͨ�� ���� getsharedClient���ṩ��������ʹ��
	public static OkHttpClient getsharedClient() {
		return Server.getsharedClient();
	}

	// ���� ʡȥ���������úܳ���ǰ�沿��
	public static Request.Builder requestBuilderWithApi(String api) {
		return new Request.Builder().url(Server.serverAddress + "ye/" + api);
	}
}