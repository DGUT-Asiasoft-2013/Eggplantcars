package com.rotk.eggplantcars;

import java.io.IOException;
import entity.User;
import inputcells.AvatarNewsView;
import inputcells.SimpleTextInputCellFragment;

import com.fasterxml.jackson.databind.ObjectMapper;

import api.Server;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
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

	SimpleTextInputCellFragment fragPassword;
	EditText account;
	User u;
	AvatarNewsView avatar;
	ImageView no_avatar;
	CheckBox remember;
	CheckBox selflogin;
	SharedPreferences sp; 
	
	
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
			Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����",
					Toast.LENGTH_SHORT).show();
			// ����handler�ӳٷ��͸���״̬��Ϣ
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
		account=(EditText) findViewById(R.id.account);
		avatar=(AvatarNewsView) findViewById(R.id.my_avatar);
		no_avatar=(ImageView) findViewById(R.id.no_avatar);
		avatar.setVisibility(View.GONE);
		no_avatar.setVisibility(View.VISIBLE);
		remember=(CheckBox) findViewById(R.id.remember);
		selflogin=(CheckBox) findViewById(R.id.selflogin);
		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE); 
		
		

		findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goRegister();
			}
		});

		findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goLogin();
			}
		});

		findViewById(R.id.forgot_password).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goRecoverPassword();
			}
		});
		
		account.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(account.hasFocus()==false){
					if(account.getText().toString()!=null && !account.getText().toString().isEmpty()){
						getAvatar();
					}else{
						return;
					}
				}
			}
		});

		fragPassword = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_password);
		
		if(sp.getBoolean("ISCHECK", true))  
        {  
          //����Ĭ���Ǽ�¼����״̬  
          remember.setChecked(true);  
          account.setText(sp.getString("USER_NAME", ""));  
          fragPassword.setText(sp.getString("PASSWORD", ""));
          //�ж��Զ���½��ѡ��״̬  
          if(sp.getBoolean("AUTO_ISCHECK", true))  
          {  
                 //����Ĭ�����Զ���¼״̬  
                 selflogin.setChecked(true);  
                //��ת����  
                goLogin();
                  
          }  
        }  
		
		 remember.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
	            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {  
	                if (remember.isChecked()) {  
	                      
	                    System.out.println("��ס������ѡ��");  
	                    sp.edit().putBoolean("ISCHECK", true).commit();  
	                      
	                }else {  
	                      
	                    System.out.println("��ס����û��ѡ��");  
	                    sp.edit().putBoolean("ISCHECK", false).commit();  
	                      
	                }  
	  
	            }  
	        });  
	          
	        //�����Զ���¼��ѡ���¼�  
	        selflogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
	            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {  
	                if (selflogin.isChecked()) {  
	                    System.out.println("�Զ���¼��ѡ��");  
	                    sp.edit().putBoolean("AUTO_ISCHECK", true).commit();  
	  
	                } else {  
	                    System.out.println("�Զ���¼û��ѡ��");  
	                    sp.edit().putBoolean("AUTO_ISCHECK", false).commit();  
	                }  
	            }  
	        });
	}

	void getAvatar() {
		// TODO Auto-generated method stub
		
		OkHttpClient client = Server.getsharedClient();
		Request request = Server.requestBuilderWithApi("user/"+account.getText().toString()+"/getavatar")
				.method("get", null)
				.build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {
				try {
					final User user = new ObjectMapper().readValue(arg1.body().bytes(), User.class);
					runOnUiThread(new Runnable() {
						public void run() {
							u = user;
							no_avatar.setVisibility(View.GONE);
							avatar.setVisibility(View.VISIBLE);
							avatar.load(Server.serverAddress+u.getAvatar());
						}
					});					
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
						}
					});
				}
			}

			@Override
			public void onFailure(final Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {
					public void run() {
					}
				});
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		getAvatar();
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
		
		final String account = LoginActivity.this.account.getText().toString();
		final String password = MD5.getMD5(fragPassword.getText());
		
		if(account.length() == 0 || password.length() == 0){
			new AlertDialog.Builder(LoginActivity.this)
			.setMessage("�û��������벻�ܿ�")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setNegativeButton("��", null)
			.show();
			return ;
}
		
		MultipartBody requestBody = new MultipartBody.Builder()
				.addFormDataPart("account", account)
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
								if(remember.isChecked())  
			                    {  
			                     //��ס�û��������롢  
			                      Editor editor = sp.edit();  
			                      editor.putString("USER_NAME", account);  
			                      editor.putString("PASSWORD",fragPassword.getText());  
			                      editor.commit();  
			                    }  
								new AlertDialog.Builder(LoginActivity.this)
								.setTitle("��¼�ɹ�")
								.setMessage("��ӭ�û���"+user.getAccount())
								.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

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
