package com.rotk.eggplantcars;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import api.Server;
import api.YeServer;
import entity.Money;
import entity.OrderForm;
import inputcells.AvatarNewsView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class OrderDetailsActivity extends Activity {

	OrderForm order;
	ImageButton btn_back;
	Button btn_get;
	Button btn_rejected;
	LinearLayout deald;
	Money buyermoney;
	Money sellermoney;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_orderdetails);

		order = (OrderForm)getIntent().getSerializableExtra("order");
		btn_back = (ImageButton)findViewById(R.id.btn_back);
		deald = (LinearLayout)findViewById(R.id.deald);
		btn_get = (Button)findViewById(R.id.btn_get);
		btn_rejected = (Button)findViewById(R.id.btn_rejected);//�˿�

		//����
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});

		//�����˿�
		btn_rejected.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String mess = null;
				if(order.getType().equals("������µ�")){
					mess = "�Ƿ������˿�";
				}
				if(order.getType().equals("��������˿�")){
					mess = "�Ƿ�ȡ���˵�";
				}
				new AlertDialog.Builder(OrderDetailsActivity.this)
				.setTitle("��ʾ")
				.setMessage(mess)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if(order.getType().equals("��������˿�")){
							rejectedtype("������µ�");
						}
						if(order.getType().equals("������µ�")){
							rejectedtype("��������˿�");
						}
					}

				})
				.setNegativeButton("����", null)
				.show();

			}
		});

		//�ջ�
		btn_get.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				recipient();//�ջ�����
			}
		});

		deald.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(OrderDetailsActivity.this,DetailsActivity.class);
				intent.putExtra("data", order.getDeal());
				startActivity(intent);
			}
		});
	}

	private void rejectedtype(final String type) {
		// TODO Auto-generated method stub
		int orderid = order.getId();
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.addFormDataPart("orderid", String.valueOf(orderid))
				.addFormDataPart("type", type);

		Request request = YeServer.requestBuilderWithApi("changeorder")
				.method("post", null)
				.post(requestBodyBuilder.build())
				.build();
		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {

				final boolean result = new ObjectMapper().readValue(arg1.body().string(), Boolean.class);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if(result){
							getorderbyid();//�õ�����������Ϣ
							if(type.equals("������µ�")){
								Toast.makeText(OrderDetailsActivity.this,"ȡ���˵��ɹ����ȴ����ҽ��ն���", Toast.LENGTH_LONG).show();
							}
							if(type.equals("��������˿�")){
								Toast.makeText(OrderDetailsActivity.this,"�����˿�ɹ����ȴ�����ȡ������", Toast.LENGTH_LONG).show();
							}
							
						}
						else{
							new AlertDialog.Builder(OrderDetailsActivity.this)
							.setMessage("ʧ��")
							.show();
						}
					}

				});				
			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {
				// TODO Auto-generated method stub

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(OrderDetailsActivity.this)
						.setMessage(arg1.getMessage())
						.show();
					}
				});				
			}
		});	
	}

	//�ջ�����
	private void recipient() {

		if(order.getType().equals("������")){
			changetype("�������");
		}else {
			return ;
		}
	}

	private void changetype(String type) {

		int orderid = order.getId();
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.addFormDataPart("orderid", String.valueOf(orderid))
				.addFormDataPart("type", type);

		Request request = YeServer.requestBuilderWithApi("changeorder")
				.method("post", null)
				.post(requestBodyBuilder.build())
				.build();
		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {

				final boolean result = new ObjectMapper().readValue(arg1.body().string(), Boolean.class);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if(result){

							final EditText et = new EditText(OrderDetailsActivity.this);
							et.setBackgroundColor(Color.WHITE);
							//��Activity������passowrd
							et.setTransformationMethod(PasswordTransformationMethod.getInstance());
							new AlertDialog.Builder(OrderDetailsActivity.this).setTitle("����������")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setView(et)
							.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {

									runOnUiThread(new Runnable() {
										public void run() {
											//���仯
											String password = String.valueOf(et.getText());
											password = MD5.getMD5(password);
											getmoney(password);
										}
									});
								}
							})
							.setNegativeButton("ȡ��", null)
							.show();

						}
						else{
							new AlertDialog.Builder(OrderDetailsActivity.this)
							.setMessage("�ջ�ʧ��")
							.show();
						}
					}

				});				
			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {


				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(OrderDetailsActivity.this)
						.setMessage(arg1.getMessage())
						.show();
					}
				});				
			}
		});	
	}

	//����
	private void getmoney(String password) {
		// TODO Auto-generated method stub
		//���������͵ķ���String.valueOf(����)
		int cash = sellermoney.getCash()+ Integer.valueOf(order.getDeal().getPrice());
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.addFormDataPart("cash", String.valueOf(cash))
				.addFormDataPart("sellerid", String.valueOf(order.getDeal().getSellerId()))
				.addFormDataPart("password", password);

		Request request = YeServer.requestBuilderWithApi("CashOrder")
				.method("post", null)
				.post(requestBodyBuilder.build())
				.build();
		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {

				final boolean result = new ObjectMapper().readValue(arg1.body().string(), Boolean.class);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						if(result){
							saverecord();//�������Ѽ�¼
						}
						else {
							Toast.makeText(OrderDetailsActivity.this,"�ջ�ʧ��!", Toast.LENGTH_LONG).show();
						}
					}
				});				
			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {
				// TODO Auto-generated method stub

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(OrderDetailsActivity.this)
						.setMessage(arg1.getMessage())
						.show();
					}
				});				
			}
		});	
	}

	//�õ�����������Ϣ
	private void getorderbyid() {

		Request request = YeServer.requestBuilderWithApi(String.valueOf(order.getId())+"/getorderbyid").get().build();
		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				try {
					String a = arg1.body().string();
					final OrderForm data = new ObjectMapper().readValue(a, OrderForm.class);
					OrderDetailsActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							order = data;
							onResume();
						}
					});
				} catch (final Exception e) {

					OrderDetailsActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							new AlertDialog.Builder(OrderDetailsActivity.this).setMessage(e.getMessage()).show();

						}
					});
				}

			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						new AlertDialog.Builder(OrderDetailsActivity.this).setMessage(arg1.getMessage()).show();

					}
				});

			}
		});
	}

	//�������Ѽ�¼
	private void saverecord() {
		// TODO Auto-generated method stub
		String record_type = "����";
		String text = "�����������";
		int my_cash = sellermoney.getCash() + Integer.valueOf(order.getDeal().getPrice());
		int record_cash =Integer.valueOf(order.getDeal().getPrice());
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.addFormDataPart("record_type", record_type)
				.addFormDataPart("text", text)
				.addFormDataPart("my_cash", String.valueOf(my_cash))
				.addFormDataPart("record_cash", String.valueOf(record_cash))
				.addFormDataPart("sellerid", order.getSeller().getId());

		Request request = YeServer.requestBuilderWithApi("sellerrecordsave")
				.method("post", null)
				.post(requestBodyBuilder.build())
				.build();
		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {


				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						getorderbyid();//�õ�����������Ϣ
					}
				});				
			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {
				// TODO Auto-generated method stub

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(OrderDetailsActivity.this)
						.setMessage(arg1.getMessage())
						.show();
					}
				});				
			}
		});	
	}


	//�õ����ҵ����
	private void getselletmoney() {
		// TODO Auto-generated method stub
		Request request = YeServer.requestBuilderWithApi(order.getDeal().getSellerId()+"/Moneys")
				.method("get", null)
				.build();

		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {
				// TODO Auto-generated method stub
				try {
					final String responseString = arg1.body().string();
					final Money data = new ObjectMapper().readValue(responseString, Money.class);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							sellermoney = data;
						}	
					});	
				} catch (Exception e) {
					e.printStackTrace();
				}


			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(OrderDetailsActivity.this,"��ȡ����ʧ��", Toast.LENGTH_LONG).show();
					}
				});		
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		getselletmoney();

		TextView order_name = (TextView)findViewById(R.id.order_name);
		order_name.setText(order.getDeal().getTitle());

		TextView order_money = (TextView)findViewById(R.id.order_money);
		order_money.setText("��"+order.getDeal().getPrice());

		TextView order_date = (TextView)findViewById(R.id.order_date);
		String dateStr = DateFormat.format("yyyy-MM-dd", order.getCreateDate()).toString();
		order_date.setText(dateStr);

		TextView order_type = (TextView)findViewById(R.id.order_type);
		order_type.setText(order.getType());

		AvatarNewsView avatar = (AvatarNewsView)findViewById(R.id.avatar);
		avatar.load(Server.serverAddress+order.getDeal().getDealAvatar());

		TextView buyer_type = (TextView)findViewById(R.id.buyer_type);

		if(order.getType().equals("������")){
			btn_get.setVisibility(View.VISIBLE);
			btn_get.setClickable(true);
			btn_rejected.setVisibility(View.INVISIBLE);
			btn_rejected.setClickable(false);
			buyer_type.setText("������������ȴ�������ϵ");
		}
		if(order.getType().equals("�������")){
			btn_get.setVisibility(View.INVISIBLE);
			btn_get.setClickable(false);
			btn_rejected.setVisibility(View.INVISIBLE);
			btn_rejected.setClickable(false);
			buyer_type.setText("�����ȷ���ջ����������");
		}
		if(order.getType().equals("��������˿�")){
			btn_get.setVisibility(View.INVISIBLE);
			btn_get.setClickable(false);
			btn_rejected.setVisibility(View.VISIBLE);
			btn_rejected.setClickable(true);
			btn_rejected.setText("ȡ���˵�");
			buyer_type.setText("�������˿�ȴ�����ȡ������");
		}
		if(order.getType().equals("������µ�")){
			btn_get.setVisibility(View.INVISIBLE);
			btn_get.setClickable(false);
			btn_rejected.setVisibility(View.VISIBLE);
			btn_rejected.setClickable(true);
			btn_rejected.setText("�����˿�");
			buyer_type.setText("���µ������ȴ�������ϵ");
		}
		if(order.getType().equals("������ȡ��")){
			btn_get.setVisibility(View.INVISIBLE);
			btn_get.setClickable(false);
			btn_rejected.setVisibility(View.INVISIBLE);
			btn_rejected.setClickable(false);
			buyer_type.setText("������ȡ��������������˿�");
		}
	}

}
