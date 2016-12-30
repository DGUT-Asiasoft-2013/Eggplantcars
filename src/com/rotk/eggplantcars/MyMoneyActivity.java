package com.rotk.eggplantcars;

import java.io.IOException;

import com.cloudage.membercenter.entity.Record;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import api.YeServer;
import entity.Money;
import entity.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class MyMoneyActivity extends Activity {
	//Ǯ��ҳ��
	TextView name;
	TextView mycash;
	LinearLayout address; //�ҵ��ջ���ַ
	Money money;
	User user;
	LinearLayout deposit;
	LinearLayout record;
	int cash = 0;
	int change = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mymoney);

		name = (TextView)findViewById(R.id.money_name);
		mycash = (TextView)findViewById(R.id.mycash);
		deposit = (LinearLayout)findViewById(R.id.deposit);
		address = (LinearLayout) findViewById(R.id.address);
		record = (LinearLayout)findViewById(R.id.record);
		
		user = (User)getIntent().getSerializableExtra("user");
		
		deposit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//��ֵ
				godeposit();
			}
		});
		
		address.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyMoneyActivity.this,MyAddress.class);
				intent.putExtra("user", user);
				startActivity(intent);
				
			}
		});
		
		record.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyMoneyActivity.this,RecordActivity.class);
				intent.putExtra("user", user);
				startActivity(intent);
			}
		});
	}
	//��ֵ����ʵ��
	private void godeposit() {
		Intent intent = new Intent(this,DepositActivity.class);
		intent.putExtra("money", money);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		checkMoneyExsists();
	}

	private void checkMoneyExsists() {
		// TODO Auto-generated method stub
		Request request = YeServer.requestBuilderWithApi(user.getId()+"/isMoneyed").get().build();
		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try{
					final String responseString = arg1.body().string();
					final Boolean result = new ObjectMapper().readValue(responseString, Boolean.class);

					if(result){
						//��ȡ��ǰ�û���Ǯ������
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								//								new AlertDialog.Builder(MyMoneyActivity.this).setMessage(responseString)
								//								.show();
								getMyMoneyAccount();
							}
						});

					}
					else{
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								new AlertDialog.Builder(MyMoneyActivity.this)
								.setTitle("��ʾ")
								.setMessage("�û���û��ͨǮ�����ܣ����ȷ����ͨ")
								.setNegativeButton("����", new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										finish();
									}
								})
								.setPositiveButton("ȷ��", new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {

										//�����û�Ǯ��
										MyMoneyActivity.this.createMyMoneyAccount();
										checkMoneyExsists();
									}
								})
								.show();

							}
						});				
					}

				}catch(final Exception e){
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							new AlertDialog.Builder(MyMoneyActivity.this).setMessage(e.getMessage())
							.show();
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, final IOException e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(MyMoneyActivity.this).setMessage(e.getMessage())
						.show();
					}
				});				
			}
		});
	}

	//�õ��˻���Ǯ����Ϣ
	private void getMyMoneyAccount() {
		Request request = YeServer.requestBuilderWithApi(user.getId()+"/Moneys")
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
							money = data;
							onResponse(arg0,data);

							//							new AlertDialog.Builder(MyMoneyActivity.this).setMessage(responseString)
							//							.show();
						}
						private void onResponse(Call arg0, Money data) {
							// TODO Auto-generated method stub
							name.setText("�û�����"+data.getUser().getName());
							mycash.setText(String.valueOf(data.getCash())+".00");
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
						Toast.makeText(MyMoneyActivity.this,"��ȡ����ʧ��", Toast.LENGTH_LONG).show();
					}
				});		
			}
		});

	}


	//�����ͻ�Ǯ����Ϣ
	private void createMyMoneyAccount() {
		// TODO Auto-generated method stub
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.addFormDataPart("cash", String.valueOf(cash));//���������͵ķ���String.valueOf(����)

		Request request = YeServer.requestBuilderWithApi("creatMoneyUser")
				.method("post", null)
				.post(requestBodyBuilder.build())
				.build();
		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				// TODO Auto-generated method stub

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(MyMoneyActivity.this,"��ͨ�ɹ�", Toast.LENGTH_LONG).show();
					}
				});				
			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {
				// TODO Auto-generated method stub

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(MyMoneyActivity.this).setMessage(arg1.getMessage())
						.show();
					}
				});				

			}
		});	
	}
}
