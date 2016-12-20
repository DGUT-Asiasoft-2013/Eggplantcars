package com.rotk.eggplantcars;




import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailsActivity extends Activity{
	TextView text;
	TextView title;
	TextView name;
	TextView carmodel;
	TextView price;
	TextView buydate;
	TextView traveldistance;
	AvatarView dealavatar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		text=(TextView) findViewById(R.id.text);
		title=(TextView) findViewById(R.id.title);
		name=(TextView) findViewById(R.id.sellername);
		carmodel=(TextView) findViewById(R.id.carmodel);
		price=(TextView) findViewById(R.id.price);
		buydate=(TextView) findViewById(R.id.buydate);
		traveldistance=(TextView) findViewById(R.id.traveldistance);
		
		dealavatar=(AvatarView) findViewById(R.id.avatar);
		
		text.setText("���ҽ��ܣ�"+(String) getIntent().getExtras().get("text"));
		title.setText((String) getIntent().getExtras().get("title"));
		name.setText("���ң�"+(String) getIntent().getExtras().get("name"));
		carmodel.setText("���ֳ��ͺţ�"+(String) getIntent().getExtras().get("carmodel"));
		price.setText("�ۼۣ�"+(String) getIntent().getExtras().get("price"));
		buydate.setText("��Ʒ����ʹ�ã�"+(String) getIntent().getExtras().get("buydate")+"��");
		traveldistance.setText("��ʻ��̣�"+(String) getIntent().getExtras().get("traveldistance"));
		
		
		
	}
}
