package com.rotk.eggplantcars;

import java.io.IOException;
import java.util.List;

import entity.User;
import inputcells.SimpleTextInputCellFragment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rotk.eggplantcars.api.Server;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends Activity {
	SimpleTextInputCellFragment fragAccount,fragPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goRegister();
			}
		});

		findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goLogin();
			}
		});

		findViewById(R.id.btn_forgot_password).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goRecoverPassword();
			}
		});

		fragAccount = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_account);
		fragPassword = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_password);
	}

	@Override
	protected void onResume() {
		super.onResume();

		fragAccount.setLabelText("�˻���");
		fragAccount.setHintText("�������˻���");
		fragPassword.setLabelText("����");
		fragPassword.setHintText("����������");
		fragPassword.setIsPassword(true);
	}

	void goRegister(){
		Intent itnt = new Intent(this,RegisterActivity.class);
		startActivity(itnt);
	}

	void goLogin(){
		OkHttpClient client = Server.getsharedClient();

		final String password = MD5.getMD5(fragPassword.getText());
		
		MultipartBody requestBody = new MultipartBody.Builder()
				.addFormDataPart("account", fragAccount.getText())
				.addFormDataPart("passwordHash", password)
				.build();

		Request request = Server.requestBuilderWithApi("login")
				.method("post", null)
				.post(requestBody)
				.build();

		final ProgressDialog dlg = new ProgressDialog(this);
		dlg.setCancelable(false);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setMessage("���ڵ�½...");
		dlg.show();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try {
					final String responseString = arg1.body().string();
					ObjectMapper mapper = new ObjectMapper();
					final User user = mapper.readValue(responseString, User.class);
					if(user != null){
						dlg.dismiss();
						runOnUiThread(new Runnable() {
							public void run() {
								dlg.dismiss();
								new AlertDialog.Builder(LoginActivity.this)
								.setTitle("��¼�ɹ�")
								.setMessage("��ӭ�û���"+user.getAccount())
								.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										//finish();
										Intent iten = new Intent(LoginActivity.this,HelloWorldActivity.class);
										startActivity(iten);
									}
								})
								.show();
							}
						});

					}
					else{
						runOnUiThread(new Runnable() {
							public void run() {
								dlg.dismiss();
								new AlertDialog.Builder(LoginActivity.this)
								.setTitle("��ʾ")
								.setMessage("��¼ʧ��!�������")
								.setPositiveButton("ȷ��",null)
								.show();
							}
						});

					}
				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							dlg.dismiss();
							new AlertDialog.Builder(LoginActivity.this)
							.setTitle("��¼ʧ��!")
							.setMessage("�û������ڻ��������")
							.setPositiveButton("ȷ��",null)
							.show();
						}
					});
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {
					public void run() {
						dlg.dismiss();

						Toast.makeText(LoginActivity.this, arg1.getLocalizedMessage(), Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}

	void goRecoverPassword(){
		Intent itnt = new Intent(this, PasswordRecoverActivity.class);
		startActivity(itnt);
	}
}
