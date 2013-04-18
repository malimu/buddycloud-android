package com.buddycloud;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.buddycloud.model.ModelCallback;
import com.buddycloud.model.SyncModel;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class SubscribedChannelsFragment extends Fragment {

	public final static String CHANNEL = "com.buddycloud.CHANNEL"; 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_subscribed, container, false);
		
		OnItemClickListener channelItemListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View arg1, int position,
					long arg3) {
				String channelJid = (String) adapterView.getItemAtPosition(position);
				selectChannel(channelJid);
			}
		};
		
		final SubscribedChannelsAdapter subscribed = new SubscribedChannelsAdapter(getActivity());
		final PersonalChannelAdapter personal = new PersonalChannelAdapter(getActivity());
		
		SyncModel.getInstance().refresh(getActivity(), new ModelCallback<JSONObject>() {
			@Override
			public void success(JSONObject response) {
				subscribed.syncd();
				personal.syncd();
			}
			
			@Override
			public void error(Throwable throwable) {
				// TODO Auto-generated method stub
			}
		});
		
		final ListView subscribedChannelsView = (ListView) view.findViewById(R.id.subscribedListView);
		subscribedChannelsView.setEmptyView(view.findViewById(R.id.subscribedProgress));
		subscribedChannelsView.setAdapter(subscribed);
		subscribedChannelsView.setOnItemClickListener(channelItemListener);
		
		subscribedChannelsView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollStateChanged(AbsListView arg0, int scrollState) {
				if (scrollState != 0) {
					subscribed.isScrolling = true;
				} else {
					subscribed.isScrolling = false;
					subscribed.notifyDataSetChanged();
				}
			}
		});
		
		ListView personalChannelView = (ListView) view.findViewById(R.id.personalListView);
		personalChannelView.setEmptyView(view.findViewById(R.id.subscribedProgress));
		personalChannelView.setAdapter(personal);
		personalChannelView.setOnItemClickListener(channelItemListener);
		
		return view;
	}
	
	private void selectChannel(String channelJid) {
		showChannelFragment(channelJid);
		hideMenu();
		SyncModel.getInstance().reset(getActivity(), channelJid);
	}

	private void hideMenu() {
		SlidingFragmentActivity activity = (SlidingFragmentActivity) getActivity();
		if (activity.getSlidingMenu().isMenuShowing()) {
			activity.getSlidingMenu().showContent();
		}
	}
	
	private void showChannelFragment(String channelJid) {
		Fragment frag = new ChannelStreamFragment();
		Bundle args = new Bundle();
		args.putString(CHANNEL, channelJid);
		frag.setArguments(args);
		MainActivity activity = (MainActivity) getActivity();
		activity.setLeftFragment(frag);
	}

}
