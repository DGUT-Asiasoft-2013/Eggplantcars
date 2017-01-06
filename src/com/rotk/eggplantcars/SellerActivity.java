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

public class SellerActivity extends Activity {

	OrderForm order;
	ImageButton btn_back;
	Button btn_delivery;
	Button btn_rejected;
	LinearLayout deald;
	Money buyermoney;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_seller);

		order = (OrderForm)getIntent().getSerializableExtra("order");

		btn_back = (ImageButton)findViewById(R.id.btn_back);
		btn_delivery =(Button)findViewById(R.id.btn_delivery);
		deald = (LinearLayout)findViewById(R.id.deald);
		btn_rejected = (Button)findViewById(R.id.btn_rejected);


		//����
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				 
				finish();
			}
		});

		//�����¼�����
		btn_delivery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				 
				delivery();//����
			}
		});

		deald.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				 
				Intent intent = new Intent(SellerActivity.this,DetailsActivity.class);
				intent.putExtra("data", order.getDeal());
				startActivity(intent);
			}
		});

		//�˿�
		btn_rejected.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final EditText et = new EditText(SellerActivity.this);
				et.setBackgroundColor(Color.WHITE);
				//��Activity������passowrd
				et.setTransformationMethod(PasswordTransformationMethod.getInstance());
				new AlertDialog.Builder(SellerActivity.this).setTitle("�Ƿ�ȷ��ȡ������")
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
								rejected(password);
							}
						});
					}
				})
				.setNegativeButton("ȡ��", null)
				.show();
			}
		});
	}

	//����
	private void delivery() {
		 
		if(order.getType().equals("������")){
			return ;
		}else {
			changetype("������");
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
							getorderbyid();//�õ�����������Ϣ
						}
						else{
							new AlertDialog.Builder(SellerActivity.this)
							.setMessage("����ʧ��")
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
						new AlertDialog.Builder(SellerActivity.this)
						.setMessage(arg1.getMessage())
						.show();
					}
				});				
			}
		});	
	}


	private void getorderbyid() {
		 
		Request request = YeServer.requestBuilderWithApi(String.valueOf(order.getId())+"/getorderbyid").get().build();
		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				try {
					String a = arg1.body().string();
					final OrderForm data = new ObjectMapper().readValue(a, OrderForm.class);
					SellerActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							order = data;
							onResume();
						}
					});
				} catch (final Exception e) {

					SellerActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							new AlertDialog.Builder(SellerActivity.this).setMessage(e.getMessage()).show();

						}
					});
				}

			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						new AlertDialog.Builder(SellerActivity.this).setMessage(arg1.getMessage()).show();

					}
				});

			}
		});
	}

	//�˿�
	private void rejected(String password) {

		//���������͵ķ���String.valueOf(����)
		int cash = buyermoney.getCash() + Integer.valueOf(order.getDeal().getPrice());
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.addFormDataPart("cash", String.valueOf(cash))
				.addFormDataPart("sellerid", String.valueOf(order.getBuyer().getId()))
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
							
							saverejectedrecord();//�������Ѽ�¼
							changetype("������ȡ��");
						}
						else {
							Toast.makeText(SellerActivity.this,"�������", Toast.LENGTH_LONG).show();
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
						new AlertDialog.Builder(SellerActivity.this)
						.setMessage(arg1.getMessage())
						.show();
					}
				});				
			}
		});	
	}

	//�����˿����Ѽ�¼
	private void saverejectedrecord() {
		 
		String record_type = "�˿�";
		String text = "����ȡ�����������";
		int my_cash = buyermoney.getCash() + Integer.valueOf(order.getDeal().getPrice());
		int record_cash =Integer.valueOf(order.getDeal().getPrice());
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.addFormDataPart("record_type", record_type)
				.addFormDataPart("text", text)
				.addFormDataPart("my_cash", String.valueOf(my_cash))
				.addFormDataPart("record_cash", String.valueOf(record_cash))
				.addFormDataPart("buyerid", order.getBuyer().getId());

		Request request = YeServer.requestBuilderWithApi("buyerrecordsave")
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
				 

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(SellerActivity.this)
						.setMessage(arg1.getMessage())
						.show();
					}
				});				
			}
		});	
	}

	//��ҽ��
	private void getbuyermoney() {
		 
		Request request = YeServer.requestBuilderWithApi(order.getBuyer().getId()+"/Moneys")
				.method("get", null)
				.build();

		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {
				 
				try {
					final String responseString = arg1.body().string();
					final Money data = new ObjectMapper().readValue(responseString, Money.class);

					SellerActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							buyermoney = data;
						}	
					});	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				 
				SellerActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(SellerActivity.this,"��ȡ����ʧ��", Toast.LENGTH_LONG).show();
					}
				});		
			}
		});
	}

	@Override
	protected void onResume() {
		 
		super.onResume();
		getbuyermoney();
		
		TextView order_name = (TextView)findViewById(R.id.order_name);
		order_name.setText(order.getDeal().getTitle());

		TextView order_money = (TextView)findViewById(R.id.order_money);
		order_money.setText("��"+order.getDeal().getPrice());

		TextView order_date = (TextView)findViewById(R.id.order_date);
		String dateStr = DateFormat.format("yyyy-MM-dd", order.getCreateDate()).toString();
		order_date.setText(dateStr);

		TextView order_type = (TextView)findViewById(R.id.order_type);
		order_type.setText(order.getType());

		TextView buyer_name = (TextView)findViewById(R.id.buyer_name);
		buyer_name.setText(order.getAddress().getName());

		TextView buyer_phone = (TextView)findViewById(R.id.buyer_phone);
		buyer_phone.setText(" "+order.getAddress().getPhoneNumber());

		TextView buyer_address = (TextView)findViewById(R.id.buyer_address);
		buyer_address.setText(order.getAddress().getText());

		AvatarNewsView avatar = (AvatarNewsView)findViewById(R.id.avatar);
		avatar.load(Server.serverAddress+order.getDeal().getDealAvatar());

		if(order.getType().equals("������")){
			btn_delivery.setText(order.getType());
			TextView seller_type =(TextView)findViewById(R.id.seller_type); 
			seller_type.setText("������������֪ͨ��Ұ���ת������");
			btn_delivery.setVisibility(View.INVISIBLE);
			btn_delivery.setEnabled(false);
			btn_rejected.setVisibility(View.INVISIBLE);
			btn_rejected.setClickable(false);
		}

		if(order.getType().equals("�������")){
			btn_delivery.setText(order.getType());
			TextView seller_type =(TextView)findViewById(R.id.seller_type); 
			seller_type.setText("������ջ������ѯ����");
			btn_delivery.setVisibility(View.INVISIBLE);
			btn_delivery.setEnabled(false);
			btn_rejected.setVisibility(View.INVISIBLE);
			btn_rejected.setClickable(false);
		}
		if(order.getType().equals("��������˿�")){
			btn_delivery.setText(order.getType());
			TextView seller_type =(TextView)findViewById(R.id.seller_type); 
			seller_type.setText("��������˿��ȡ������");
			btn_rejected.setVisibility(View.VISIBLE);
			btn_rejected.setEnabled(true);
			btn_delivery.setVisibility(View.INVISIBLE);
			btn_delivery.setClickable(false);
		}
		
		if(order.getType().equals("������ȡ��")){
			btn_delivery.setText(order.getType());
			TextView seller_type =(TextView)findViewById(R.id.seller_type); 
			seller_type.setText("������ȡ��������ѷ���");
			btn_rejected.setVisibility(View.INVISIBLE);
			btn_rejected.setClickable(false);
			btn_delivery.setVisibility(View.INVISIBLE);
			btn_delivery.setClickable(false);
		}
	}

}
