package com.rotk.eggplantcars;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import api.YeServer;
import entity.Money;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;
//��ֵҳ��
public class DepositActivity extends Activity {

	EditText edit;
	EditText deposit_password;
	Button deposit_1;
	Button deposit_10;
	Button deposit_100;

	Button go_deposit;

	Money money;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deposit);

		edit = (EditText)findViewById(R.id.edit);
		deposit_1 = (Button)findViewById(R.id.deposit_1);
		deposit_10 = (Button)findViewById(R.id.deposit_10);
		deposit_100 = (Button)findViewById(R.id.deposit_100);
		go_deposit = (Button)findViewById(R.id.go_deposit);
		deposit_password = (EditText)findViewById(R.id.deposit_password);

		money = (Money)getIntent().getSerializableExtra("money");

		deposit_1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edit.setText(deposit_1.getText());
			}
		});
		deposit_10.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edit.setText(deposit_10.getText());
			}
		});
		deposit_100.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edit.setText(deposit_100.getText());
			}
		});

		//��ֵ�¼�����
		go_deposit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String edittext = String.valueOf(edit.getText());
				String passwordtext = String.valueOf(deposit_password.getText());

				if(edittext == null || edittext.isEmpty()) {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(DepositActivity.this,"����Ϊ��!", Toast.LENGTH_SHORT).show();
						}
					});	
				}
				else if(passwordtext == null || passwordtext.isEmpty()){
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(DepositActivity.this,"���벻��Ϊ��!", Toast.LENGTH_SHORT).show();
						}
					});			
				}
				else if(Integer.valueOf(edittext).intValue()>1000000 || Integer.valueOf(edittext).intValue()<0){
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(DepositActivity.this,"���ܳ���һ����", Toast.LENGTH_SHORT).show();
						}
					});	
				}
				else{
					DepositActivity.this.ondeposit();
				}


			}
		});
	}


	//��ֵ����ʵ��
	private void ondeposit() {
		// TODO Auto-generated method stub
		int cash = Integer.valueOf(edit.getText().toString()).intValue() + money.getCash();

		String password = MD5.getMD5(String.valueOf(deposit_password.getText()));

		//���������͵ķ���String.valueOf(����)
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.addFormDataPart("cash", String.valueOf(cash))
				.addFormDataPart("password", password);

		Request request = YeServer.requestBuilderWithApi("CashDeposit")
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
							new AlertDialog.Builder(DepositActivity.this)
							.setTitle("��ʾ")
							.setMessage("��ֵ�ɹ�")
							.setPositiveButton("ȷ��", new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									saverecord();
									finish();
									overridePendingTransition(0, R.anim.slide_out_bottom);
								}							
							})
							.show();
						}
						else {
							Toast.makeText(DepositActivity.this,"��ֵʧ��,�������!", Toast.LENGTH_LONG).show();
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
						new AlertDialog.Builder(DepositActivity.this)
						.setMessage(arg1.getMessage())
						.show();
					}
				});				
			}
		});	
	}
	
	//�����ֵ��¼
	private void saverecord() {
		// TODO Auto-generated method stub
		String record_type = "��ֵ";
		String text = "�˻���ֵ";
		int my_cash = Integer.valueOf(edit.getText().toString()).intValue() + money.getCash();
		int record_cash = Integer.valueOf(edit.getText().toString()).intValue();
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.addFormDataPart("record_type", record_type)
				.addFormDataPart("text", text)
				.addFormDataPart("my_cash", String.valueOf(my_cash))
				.addFormDataPart("record_cash", String.valueOf(record_cash));

		Request request = YeServer.requestBuilderWithApi("recordsave")
				.method("post", null)
				.post(requestBodyBuilder.build())
				.build();
		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {

				
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

					}
				});				
			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {
				// TODO Auto-generated method stub

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(DepositActivity.this)
						.setMessage(arg1.getMessage())
						.show();
					}
				});				
			}
		});	
	}

}
