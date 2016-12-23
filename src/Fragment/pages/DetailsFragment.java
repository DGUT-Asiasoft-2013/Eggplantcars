package Fragment.pages;

import com.rotk.eggplantcars.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import entity.Deal;

public class DetailsFragment extends Fragment{
	TextView seller;
	TextView carmodel;
	TextView traveldistance;
	TextView buydate;
	TextView text;
	Deal deal;
	View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(view==null){
			view=inflater.inflate(R.layout.fragment_deal_details, null);
			deal=(Deal) getActivity().getIntent().getSerializableExtra("data");
			carmodel=(TextView) view.findViewById(R.id.carmodel);
			seller=(TextView) view.findViewById(R.id.seller);
			traveldistance=(TextView) view.findViewById(R.id.traveldistance);
			buydate=(TextView) view.findViewById(R.id.buydate);
			text=(TextView) view.findViewById(R.id.text);
			
			seller.setText("���ң�"+deal.getSellerName());
			carmodel.setText("�������ͣ�"+deal.getCarModel());
			traveldistance.setText("����ʻ��̣�"+deal.getTravelDistance());
			buydate.setText("�ѱ�ʹ�ã�"+deal.getBuyDate()+"��");
			text.setText("���ҽ��ܣ�"+deal.getText());
		}
		return view;
	}
}
