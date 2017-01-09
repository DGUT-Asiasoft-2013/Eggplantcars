package inputcells.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.rotk.eggplantcars.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;
import api.Server;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddPicNews extends Activity {

	private ImageButton btn_back, btn_send;
	private EditText newstitle;
	private EditText newsText;
	private GridView gridView; // ������ʾ����ͼ
	private final int IMAGE_OPEN = 0x000002; // ��ͼƬ���
	private static final int TAKE_PICTURE = 0x000001; // ����
	private String pathImage; // ѡ��ͼƬ·��
	private Bitmap bmp; // ������ʱͼƬ

	private ArrayList<HashMap<String, Bitmap>> imageItem; // ͼƬ����
	private SimpleAdapter simpleAdapter; // ������
	private File tempfile;
	byte[] pngData;

	private View parentView; // ������
	private PopupWindow pop = null; // ѡ��ͼƬ��������
	private LinearLayout poplayout; // ���洰�ڵ�layout

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		parentView = getLayoutInflater().inflate(R.layout.activity_addnews_manypic, null);
		setContentView(parentView);

		newstitle = (EditText) findViewById(R.id.editText1);
		newsText = (EditText) findViewById(R.id.editText2);

		btn_back = (ImageButton) findViewById(R.id.btn_back);
		btn_send = (ImageButton) findViewById(R.id.btn_next);

		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(0, R.anim.slide_out_bottom);
			}
		});

		btn_send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendNews();
			}
		});

		Init();
	}

	// ����gridview���
	private void Init() {
		// TODO Auto-generated method stub
		pop = new PopupWindow(AddPicNews.this);
		View view = getLayoutInflater().inflate(R.layout.example_popupwindows_item, null);

		poplayout = (LinearLayout) view.findViewById(R.id.ll_popup);

		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(view);

		RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
		Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera); // ����
		Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo); // ���
		Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel); // ȡ��

		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pop.dismiss();
				poplayout.clearAnimation();
			}
		});

		bt1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				photo();
				pop.dismiss();
				poplayout.clearAnimation();
			}
		});

		bt2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// ѡ��ͼƬ
				Intent intent = new Intent(Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, IMAGE_OPEN);
				overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
				pop.dismiss();
				poplayout.clearAnimation();
			}
		});

		bt3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pop.dismiss();
				poplayout.clearAnimation();
			}
		});

		// gridView
		gridView = (GridView) findViewById(R.id.noScrollgridview);

		/*
		 * ����Ĭ��ͼƬ���ͼƬ�Ӻ� ͨ��������ʵ�� SimpleAdapter����imageItemΪ����Դ
		 * R.layout.griditem_addpicΪ����
		 */
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_focused);// ���ѡ��ͼƬ�ļӺ�
		imageItem = new ArrayList<HashMap<String, Bitmap>>();
		HashMap<String, Bitmap> map = new HashMap<String, Bitmap>();
		map.put("itemImage", bmp);
		imageItem.add(map);
		simpleAdapter = new SimpleAdapter(this, imageItem, R.layout.example_griditem_addpic,
				new String[] { "itemImage" }, new int[] { R.id.imageView1 });

		simpleAdapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data, String textRepresentation) {
				// TODO Auto-generated method stub
				if (view instanceof ImageView && data instanceof Bitmap) {
					ImageView i = (ImageView) view;
					i.setImageBitmap((Bitmap) data);
					return true;
				}
				return false;
			}
		});

		gridView.setAdapter(simpleAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				if (position == 0) {
					if (position == 0) { // ���ͼƬλ��Ϊ+ 0��Ӧ0��ͼƬ
						if (imageItem.size() == 4) { // ��һ��ΪĬ��ͼƬ
							Toast.makeText(AddPicNews.this, "Max = 3", Toast.LENGTH_SHORT).show();
						}
						if (imageItem.size() < 4) {
							// ���������
							InputMethodManager imm = (InputMethodManager) getSystemService(
									Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

							// ����pop����
							poplayout.startAnimation(
									AnimationUtils.loadAnimation(AddPicNews.this, R.anim.activity_translate_in));
							pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);

						}
					}
				} else {
					dialog(position);
				}
			}
		});

	}

	/*
	 * Dialog�Ի�����ʾ�û�ɾ������ positionΪɾ��ͼƬλ��
	 */
	protected void dialog(final int position) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new Builder(AddPicNews.this);
		builder.setMessage("ȷ���Ƴ������ͼƬ��");
		builder.setTitle("��ʾ");
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				imageItem.remove(position);
				simpleAdapter.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	/*
	 * ����
	 */
	protected void photo() {
		// TODO Auto-generated method stub
		File DatalDir = Environment.getExternalStorageDirectory();
		File myDir = new File(DatalDir, "/DCIM/Camera");
		myDir.mkdirs();
		String mDirectoryname = DatalDir.toString() + "/DCIM/Camera";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hhmmss", Locale.SIMPLIFIED_CHINESE);
		tempfile = new File(mDirectoryname, sdf.format(new Date()) + ".jpg");
		if (tempfile.isFile())
			tempfile.delete();
		Uri Imagefile = Uri.fromFile(tempfile);

		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Imagefile);
		startActivityForResult(cameraIntent, TAKE_PICTURE);
	}

	/*
	 * ����������Ϣ
	 */
	protected void sendNews() {
		// TODO Auto-generated method stub
		String title = newstitle.getText().toString();
		String text = newsText.getText().toString();
		String articleImgName = new SimpleDateFormat("MMddHHmmss").format(new Date());//ͼƬ����
		if (title == null || title.isEmpty()) {
			new AlertDialog.Builder(this).setMessage("���������!").setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton("OK", null).show();
			return;
		}
		if (text == null || text.isEmpty()) {
			new AlertDialog.Builder(this).setMessage("����������!").setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton("OK", null).show();
			return;
		}
		
		
		OkHttpClient client = Server.getsharedClient();

		MultipartBody.Builder requestBuilder = new MultipartBody.Builder()
				.addFormDataPart("title", title)
				.addFormDataPart("text", text)
				.addFormDataPart("avatarName", articleImgName);
		
		//�����ϴ�ͼƬ
		for(int i= 1;i<imageItem.size();i++){
			Bitmap bitmap = imageItem.get(i).get("itemImage");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, 100, baos);
			pngData = baos.toByteArray();
			requestBuilder.addFormDataPart("newsavatar", "newsavatar"+i,RequestBody.create(MediaType.parse("image/png"), pngData));
		}
		
		Request request = Server.requestBuilderWithApi("news")
				.method("post", null)
				.post(requestBuilder.build())
				.build();
		
		client.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				try {
					final String rString = arg1.body().string();
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							AddPicNews.this.onResponse(arg0,rString);
						}
					});
				} catch (Exception e) {
					// TODO: handle exception
					AddPicNews.this.onFailure(arg0, e);
				}
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				AddPicNews.this.onFailure(arg0, arg1);
			}
		});
		
		
	}

	// ˢ��ͼƬ
	@Override
	protected void onResume() {
		super.onResume();
		if (!TextUtils.isEmpty(pathImage)) {
			Bitmap addbmp = BitmapFactory.decodeFile(pathImage);
			HashMap<String, Bitmap> map = new HashMap<String, Bitmap>();
			map.put("itemImage", addbmp);
			imageItem.add(map);
			Log.d("123", imageItem.toString());
			for (int i = 0; i < imageItem.size(); i++) {
				Log.d("bb", imageItem.get(i).values().toString());
			}
			simpleAdapter = new SimpleAdapter(this, imageItem, R.layout.example_griditem_addpic,
					new String[] { "itemImage" }, new int[] { R.id.imageView1 });
			simpleAdapter.setViewBinder(new ViewBinder() {
				@Override
				public boolean setViewValue(View view, Object data, String textRepresentation) {
					// TODO Auto-generated method stub
					if (view instanceof ImageView && data instanceof Bitmap) {
						ImageView i = (ImageView) view;
						i.setImageBitmap((Bitmap) data);
						return true;
					}
					return false;
				}
			});
			gridView.setAdapter(simpleAdapter);
			simpleAdapter.notifyDataSetChanged();
			// ˢ�º��ͷŷ�ֹ�ֻ����ߺ��Զ����
			pathImage = null;
		}
	}

	// ��ȡͼƬ·�� ��ӦstartActivityForResult
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case IMAGE_OPEN:
			if (resultCode == RESULT_OK) {
				Uri uri = data.getData();
				if (!TextUtils.isEmpty(uri.getAuthority())) {
					// ��ѯѡ��ͼƬ
					Cursor cursor = getContentResolver().query(uri, new String[] { MediaStore.Images.Media.DATA }, null,
							null, null);
					// ���� û�ҵ�ѡ��ͼƬ
					if (null == cursor) {
						return;
					}
					// ����ƶ�����ͷ ��ȡͼƬ·��
					cursor.moveToFirst();
					pathImage = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
					// Log.d("lujing", pathImage);
				}
			}
			break;

		case TAKE_PICTURE:
			if (resultCode == RESULT_OK) {
				pathImage = tempfile.getPath();
				// Log.d("lujing", pathImage);
			}
			break;

		default:
			break;
		}
	}

	
	void onResponse(Call arg0, String responseBody) {
		new AlertDialog.Builder(this)
		.setTitle("�����ɹ�")
		.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
//				overridePendingTransition(0,R.anim.slide_out_right);
			}
		})
		.show();
	}

	void onFailure(Call arg0, Exception arg1) {
		new AlertDialog.Builder(this)
		.setTitle("����ʧ��")
		.setMessage(arg1.getLocalizedMessage())
		.setNegativeButton("OK", null)
		.show();
}
	
	
	
	
}
