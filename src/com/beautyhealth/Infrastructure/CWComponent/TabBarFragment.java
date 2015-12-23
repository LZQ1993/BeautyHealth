package com.beautyhealth.Infrastructure.CWComponent;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;

import com.beautyhealth.MainActivity;
import com.beautyhealth.R;
import com.beautyhealth.MembersCenter.MembersCenterAcitivity;
import com.beautyhealth.PersonHealth.PersonHealthActivity;
import com.beautyhealth.PrivateDoctors.PrivateDoctorsActivity;
import com.beautyhealth.PrivateDoctors.Activity.DoctorBrieflyActivity;
import com.beautyhealth.PrivateDoctors.Activity.SearchHospitalActivity;
import com.beautyhealth.SafeGuardianship.SafeGuardianshipActivity;
import com.beautyhealth.UserCenter.MeActivity;

@SuppressWarnings("deprecation")
public class TabBarFragment extends Fragment implements OnCheckedChangeListener {
	private RadioButton mainpage, healthpage, doctorpage, safepage;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.activity_tabbar, container, false);
		mainpage = (RadioButton) view.findViewById(R.id.radio_button0);
		mainpage.setOnCheckedChangeListener(this);
		healthpage = (RadioButton) view.findViewById(R.id.radio_button1);
		healthpage.setOnCheckedChangeListener(this);
		doctorpage = (RadioButton) view.findViewById(R.id.radio_button2);
		doctorpage.setOnCheckedChangeListener(this);
		safepage = (RadioButton) view.findViewById(R.id.radio_button3);
		safepage.setOnCheckedChangeListener(this);
		// mainpage.setChecked(true);
		return view;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			Intent intent = new Intent();
			switch (buttonView.getId()) {
			case R.id.radio_button0:
				if (!(getActivity().getLocalClassName().toString()
						.equals("MainActivity"))) {
					
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setClass(getActivity(), MainActivity.class);
					// tag = 0;
					getActivity().startActivity(intent);
					getActivity().overridePendingTransition(R.anim.slide_up_in,
							R.anim.slide_down_out);
					getActivity().finish();
				}
				break;
			case R.id.radio_button1:
				if (!(getActivity().getLocalClassName().toString()
						.equals("MembersCenter.MembersCenterAcitivity"))) {
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					// intent.putExtra("positionTag", "1");
					intent.setClass(getActivity().getApplicationContext(),
							MembersCenterAcitivity.class);
					// tag = 1;
					getActivity().startActivity(intent);
					getActivity().overridePendingTransition(R.anim.slide_up_in,
							R.anim.slide_down_out);
					getActivity().finish();
				}
				break;
			case R.id.radio_button2:
				if (!(getActivity().getLocalClassName().toString()
						.equals("PrivateDoctors.Activity.SearchHospitalActivity"))) {
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					// intent.putExtra("positionTag", 2);
					intent.setClass(getActivity().getApplicationContext(),
							SearchHospitalActivity.class);
					// tag = 2;
					getActivity().startActivity(intent);
					getActivity().overridePendingTransition(R.anim.slide_up_in,
							R.anim.slide_down_out);
					getActivity().finish();

				}
				break;
			case R.id.radio_button3:
				if (!(getActivity().getLocalClassName().toString()
						.equals("UserCenter.MeActivity"))) {
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setClass(getActivity().getApplicationContext(),
							MeActivity.class);

					getActivity().startActivity(intent);
					getActivity().overridePendingTransition(R.anim.slide_up_in,
							R.anim.slide_down_out);
					getActivity().finish();
				}
				break;
			}
		}

	}

}
