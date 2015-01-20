package com.jinr.core.gift;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import model.AssetsInfo;
import model.BaseModel;
import model.GiftList;
import model.GiftListContent;

import org.apache.http.Header;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.base.BaseFragment;
import com.jinr.core.config.MessageWhat;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.security.DealPasswdActivity;
import com.jinr.core.security.RealNameActivity;
import com.jinr.core.trade.getCash.FragmentAssets;
import com.jinr.core.trade.purchase.PurchaseFirstActivity;
import com.jinr.core.trade.purchase.PurchaseSecondActivity;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.TextAdjustUtil;
import com.jinr.core.utils.ToastUtil;
import com.jinr.pulltorefresh.PullToRefreshBase;
import com.jinr.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.jinr.pulltorefresh.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class GiftFragment extends BaseFragment implements OnClickListener {

	private ImageView mLeftIv;
	private ImageView mRightTv;

	private View mNotList;
	private ImageView mNotIV;
	private TextView mNotTv;
	private Button mNotBt;

	private String mUserId = "";
	private String mNetError="";

	// 是上拉还是下拉
	private boolean isUp = true;

	private int currentPage = 1;
	private int mTotalPage=1;

	private int mPageNum = 8;
	
	
	private String mPageUrl="http://www.jinr.com/Public/upload/ueditor/image/20141210/14182152276254.jpg";
	private String mShareUrl="http://www.jinr.com";
	private String mShareContent="鲸鱼宝双十二巨献！红包活动重磅来袭！100%获得千元红包！见者有份哦！";

	private BaseModel<GiftList> mResult;

	// 下拉刷新ListView
	private PullToRefreshListView mPullList;
	// ListView
	private ListView mList;
	
	private GiftFragmentAdapter adpter;
	
	private List<GiftListContent> mListData;

	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageWhat.GIFT_OK:

//				mNotList.setVisibility(View.GONE);
				Bundle result = (Bundle) msg.obj;
				GiftList giftList = (GiftList) result.getSerializable("gift");
				
				if(isUp){
					
//					Bundle result = (Bundle) msg.obj;
//					GiftList giftList = (GiftList) result.getSerializable("gift");
					mListData=giftList.getDetal();
					adpter=new GiftFragmentAdapter(getActivity(), mListData);
					mList.setAdapter(adpter);
					adpter.notifyDataSetChanged();
					// 下拉加载完成
					mPullList.onPullDownRefreshComplete();

					// 上拉刷新完成
					mPullList.onPullUpRefreshComplete();
					
				}else{
					
					mListData.addAll(giftList.getDetal());
					adpter=new GiftFragmentAdapter(getActivity(), mListData);
					mList.setAdapter(adpter);
					adpter.notifyDataSetChanged();

					// 下拉加载完成
					mPullList.onPullDownRefreshComplete();
					// 上拉刷新完成
					mPullList.onPullUpRefreshComplete();
					
					
					
				}

				break;
			case MessageWhat.NO_CARD:
				mNotList.setVisibility(View.VISIBLE);
				mNotIV.setImageResource(R.drawable.new_pic);
				mNotTv.setText("绑定银行卡,红包好大啊");
				mNotBt.setText("去绑定银行卡");

				mNotBt.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

						MainActivity.instance.changeBankView(1);

					}
				});

				break;
			case MessageWhat.NO_MONEY:
				mNotList.setVisibility(View.VISIBLE);

				mNotIV.setImageResource(R.drawable.new_pic);
				mNotTv.setText("只要转入过百,红包马上就来");
				mNotBt.setText("去转入");

				mNotBt.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

						Intent intent_deal = new Intent(getActivity(),
								PurchaseFirstActivity.class);
						getActivity().startActivity(intent_deal);

					}
				});

				break;
			case MessageWhat.NO_INVITE:
				mNotList.setVisibility(View.VISIBLE);
				mNotBt.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

						GiftFragment.this.showShare();
					}
				});

				break;
			}
		}
	};

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initImagePath();
		View view = inflater.inflate(R.layout.fragment_gift, container, false);
		initData();
		findViewById(view);
		initUI();
		setListener();
		return view;
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

		if (isAdded()) {
			mUserId = PreferencesUtils.getValueFromSPMap(getActivity(),
					PreferencesUtils.Keys.UID);

		}
		send(currentPage);

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (isAdded()) {
			mNetError = getResources().getString(R.string.default_error);

		}
	}

	@Override
	protected void findViewById(View view) {
		// TODO Auto-generated method stub
		mLeftIv = (ImageView) view.findViewById(R.id.new_title_left_img);
		mRightTv = (ImageView) view.findViewById(R.id.new_title_right_text);
		mPullList = (PullToRefreshListView) view.findViewById(R.id.gift_lv);

		mNotList = (View) view.findViewById(R.id.not_list);
		mNotIV = (ImageView) view.findViewById(R.id.gift_not_iv);
		mNotTv = (TextView) view.findViewById(R.id.gift_not_tv);
		mNotBt = (Button) view.findViewById(R.id.gift_not_bt);

	}

	@Override
	protected void initUI() {
		// TODO Auto-generated method stub
		mPullList.setPullLoadEnabled(true);
		mPullList.setScrollLoadEnabled(true);
		mList = mPullList.getRefreshableView();
		mList.setCacheColorHint(Color.TRANSPARENT);

	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		mLeftIv.setOnClickListener(this);
		mRightTv.setOnClickListener(this);

		mPullList.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				isUp = true;
				currentPage = 1;
				send(currentPage);
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub

				isUp = false;
				if (currentPage < mTotalPage) {
					currentPage += 1;
					send(currentPage);
				} else {
					// 下拉加载完成
					mPullList.onPullDownRefreshComplete();
//					// 上拉刷新完成
					mPullList.onPullUpRefreshComplete();
					// 设置是否有更多的数据
					mPullList.setHasMoreData(false);
				}

			}

		});
		
		
		mList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Object obj = mList.getItemAtPosition(position);
				if(obj instanceof GiftListContent){
					Bundle bundle = new Bundle();
					Intent intent = new Intent(getActivity(),
							GiftDetailActivity.class);
					bundle.putString("id", ((GiftListContent) obj).getId());
					bundle.putInt("type", ((GiftListContent) obj).getType());
					bundle.putString("url", mShareUrl);
					intent.putExtras(bundle);
					startActivity(intent);
					
					
				}
				
			}});

	}

	private void showShare() {
		ShareSDK.initSDK(getActivity());
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字
		oks.setNotification(R.drawable.ic_launcher,
				getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(mShareContent+ mShareUrl);
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(mShareUrl);
//		oks.setImageUrl(imageUrl);
//		oks.setImageUrl(mPageUrl);
		// text是分享文本，所有平台都需要这个字段
		oks.setText(mShareContent + mShareUrl);
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setUrl(mShareUrl);
		oks.setImagePath(TEST_IMAGE);
//		oks.setImagePath("/sdcard/test.jpg");// 确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		
		
//		oks.setUrl(mShareUrl);
		
		
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
//		oks.setComment("");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(mShareUrl);

		// 启动分享GUI
		oks.show(getActivity());
	}

	private void send(int page) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put(UrlConfig.APP_KEY, UrlConfig.APP_VALUE);
		params.put("u_id", mUserId);
		params.put("p", page);
		params.put("pagesize", mPageNum);
		// params.put("passwords", submit_password);
		MyhttpClient.get(UrlConfig.GIFT_LIST, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1, arg2, arg3);
						// loadingdialog.dismiss();
						ToastUtil.show(getActivity(),
								"网络错误,请重试");

					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						// TODO Auto-generated method stub
						super.onSuccess(arg0, arg1, arg2);
						// loadingdialog.dismiss();

						try {
							String response = new String(arg2, "UTF-8");
							MyLog.i("红包列表返回", response);
							response = response.substring(response.indexOf("{"));
							mResult = new Gson().fromJson(response,
									new TypeToken<BaseModel<GiftList>>() {
									}.getType());
							if (mResult.isSuccess()) {
								mShareUrl=mResult.getData().getUrl();

								Message msg = Message.obtain();
								mTotalPage=mResult.getData().getCountpage();
								switch (mResult.getData().getUser_type()) {

								case 1:
									msg.what = MessageWhat.NO_CARD;
									handler.sendEmptyMessage(msg.what);

									break;
								case 2:
									msg.what = MessageWhat.NO_MONEY;
									handler.sendEmptyMessage(msg.what);

									break;
								case 3:
									msg.what = MessageWhat.NO_INVITE;
									handler.sendEmptyMessage(msg.what);

									break;
								case 4:
									Bundle bundle = new Bundle();
									bundle.putSerializable("gift",
											mResult.getData());

									msg.what = MessageWhat.GIFT_OK;
									msg.obj = bundle;
									handler.sendMessage(msg);

									break;
								default:
									break;

								}

							} else {

								ToastUtil.show(getActivity(), mResult.getMsg());
							}

						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							MyLog.i("json解析错误", "解析错误");
						} catch (NumberFormatException e) {

						} catch (IndexOutOfBoundsException e) {

						}

					}

				});

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		switch (arg0.getId()) {
		case R.id.new_title_left_img:
			MainActivity.instance.showLeftMenu();
			break;

		case R.id.new_title_right_text:
			// 活动细则
			Intent intent = new Intent(getActivity(), RuleActivity.class);
			startActivity(intent);

			break;

		default:
			break;
		}

	}
	private static final String FILE_NAME = "/share_pic.jpg";
	public static String TEST_IMAGE;
	private void initImagePath() {
		try {
			if (Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())
					&& Environment.getExternalStorageDirectory().exists()) {
				TEST_IMAGE = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + FILE_NAME;
			} else {
				TEST_IMAGE = getActivity().getApplication().getFilesDir().getAbsolutePath()
						+ FILE_NAME;
			}
			// 创建图片文件夹
			File file = new File(TEST_IMAGE);
			if (!file.exists()) {
				file.createNewFile();
				Bitmap pic = BitmapFactory.decodeResource(getResources(),
						R.drawable.gift);
				FileOutputStream fos = new FileOutputStream(file);
				pic.compress(CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
			}
		} catch (Throwable t) {
			t.printStackTrace();
			TEST_IMAGE = null;
		}
	}
	
	private String getShareURL(){
		return "http://www.jinr.com/user/register?invite_uid=" + mUserId;
	}
	
}
