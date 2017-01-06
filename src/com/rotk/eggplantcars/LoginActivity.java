package com.rotk.eggplantcars;

import java.io.IOException;
import java.util.List;

import entity.User;
import inputcells.SimpleTextInputCellFragment;

import com.fasterxml.jackson.databind.ObjectMapper;
import api.Server;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
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
	
	private static boolean isExit = false;

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void exit() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(getApplicationContext(), "再按一次退出程序",
					Toast.LENGTH_SHORT).show();
			// 利用handler延迟发送更改状态信息
			mHandler.sendEmptyMessageDelayed(0, 3000);
		} else {
			finish();
			System.exit(0);
		}
	}

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

		fragAccount.setLabelText("账户");
		fragAccount.setHintText("请输入账户名");
		fragPassword.setLabelText("密码");
		fragPassword.setHintText("请输入密码");
		fragPassword.setIsPassword(true);
	}

	void goRegister(){
		Intent itnt = new Intent(this,RegisterActivity.class);
		startActivity(itnt);
	}

	void goLogin(){
		OkHttpClient client = Server.getsharedClient();
		
		String account = fragAccount.getText();
		final String password = MD5.getMD5(fragPassword.getText());
		
		if(account.length() == 0 || password.length() == 0){
			new AlertDialog.Builder(LoginActivity.this)
			.setMessage("用户名和密码不能空")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setNegativeButton("好", null)
			.show();
			return ;
}
		
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
		dlg.setMessage("正在登陆...");
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
								.setTitle("登录成功")
								.setMessage("欢迎用户："+user.getAccount())
								.setPositiveButton("确定", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										//finish();
										Intent iten = new Intent(LoginActivity.this,HelloWorldActivity.class);
										startActivity(iten);
										finish();
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
								.setTitle("提示")
								.setMessage("登录失败!密码错误")
								.setPositiveButton("确定",null)
								.show();
							}
						});

					}
				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							dlg.dismiss();
							new AlertDialog.Builder(LoginActivity.this)
							.setTitle("登录失败!")
							.setMessage("用户不存在或密码错误")
							.setPositiveButton("确定",null)
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
