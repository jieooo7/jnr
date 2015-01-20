package com.jinr.core.gift;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import model.BaseModel;
import model.RedEnvelopesDetailed;

import org.apache.http.Header;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.R;
import com.jinr.core.base.Base2Activity;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class GiftDetailActivity  extends Base2Activity implements OnClickListener{
	
	private ImageView title_left_img; // title左边图片
	private TextView title_center_text; // title标题
	
	private ImageView mBanner;
	private TextView mRuleTv;
	private Button mShareBt;
	
	private int statue = 0;
	private TextView gift_time;
	private TextView gift_content;
	
	private String mPageUrl="http://www.jinr.com/Public/upload/ueditor/image/20141210/14182152276254.jpg";
	private String mShareUrl="http://www.jinr.com";
	private String mShareContent="鲸鱼宝双十二巨献！红包活动重磅来袭！100%获得千元红包！见者有份哦！";
	
	
	private String userID = "";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		statue = getIntent().getExtras().getInt("type");
		switch (statue) {
		case 1:
			setContentView(R.layout.gift_detail_a);
			break;
		case 2:
		case 3:
			setContentView(R.layout.gift_detail);
			break;
		default:
			break;
		}
		gift_time = (TextView) findViewById(R.id.gift_time);
		gift_content = (TextView) findViewById(R.id.gift_detail_tv);
		

		initData();
		findViewById();
		initUI();
		setListener();
	}


	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		mShareUrl=getIntent().getExtras().getString("url");
		send(getIntent().getExtras().getString("id"));
		
		userID = PreferencesUtils.getValueFromSPMap(this,
				PreferencesUtils.Keys.UID);
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		title_left_img = (ImageView) findViewById(R.id.title_left_img);
		title_center_text = (TextView) findViewById(R.id.title_center_text);
		mBanner=(ImageView) findViewById(R.id.gift_detail_banner);
		mRuleTv=(TextView) findViewById(R.id.gift_detail_tv);
		mShareBt=(Button) findViewById(R.id.gift_detail_bt);

	}

	@Override
	protected void initUI() {
		// TODO Auto-generated method stub
		title_center_text.setText("红包");
//		mRuleTv.setText(getResources().getString(
//				R.string.gift_rule_one));
		
//		mBanner.setImageResource(R.drawable.gift_banner1);
		initImagePath();
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		title_left_img.setOnClickListener(this);
		mShareBt.setOnClickListener(this);
	}
	
	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字
		oks.setNotification(R.drawable.ic_launcher,
				getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(mShareContent + mShareUrl);
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(mShareUrl);
		// text是分享文本，所有平台都需要这个字段
		oks.setText(mShareContent + mShareUrl);
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		
		oks.setImagePath(TEST_IMAGE);
		oks.setUrl(mShareUrl);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
//		oks.setComment("");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(mShareUrl);

		// 启动分享GUI
		oks.show(this);
		
		
	
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.title_left_img:
			finish();
			break;
		case R.id.gift_detail_bt:

			showShare();
			//分享
			break;

		default:
			break;
		}

	}
	private void send(String id) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();  
		params.put(UrlConfig.APP_KEY, UrlConfig.APP_VALUE);
		params.put("id", id);

		MyhttpClient.get(UrlConfig.GET_RED_DETAILED, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1, arg2, arg3);
//				loadingdialog.dismiss();
				ToastUtil.show(GiftDetailActivity.this,getResources().getString(R.string.default_error));
		
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0, arg1, arg2);
		//		loadingdialog.dismiss();
				
				try {
					String response=new String(arg2,"UTF-8");
					response=response.substring(response.indexOf("{"));
					MyLog.i("红包详情", response);
					BaseModel<RedEnvelopesDetailed> result=new Gson().fromJson(response,
							new TypeToken<BaseModel<RedEnvelopesDetailed>>() {
					}.getType());
					if(result.isSuccess()){
						String s_time = result.getData().getS_time();
						String e_time = result.getData().getE_time();
						String content = result.getData().getContent();
//						s_time = s_time.substring(0,s_time.indexOf(" "));
//						e_time = e_time.substring(0,e_time.indexOf(" "));
						
						String time = "有效日期至 " + e_time;
						MyLog.i(time,content);
						
						gift_time.setText(time);
						gift_content.setText(Html.fromHtml(content));
						
					}else{
						ToastUtil.show(getApplication(), result.getMsg());
					}
					
					
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (JsonSyntaxException e) {
					// TODO Auto-generated catch block
					MyLog.i("json解析错误", "解析错误");
				}catch(NumberFormatException e){
					
					
				}catch(IndexOutOfBoundsException e){
					
					
				}
				
				
			}
			
			
		});
		
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
				TEST_IMAGE = getApplication().getFilesDir().getAbsolutePath()
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
	
	
//	private String getShareURL(){
//		return "http://www.jinr.com/user/register?invite_uid="+userID;
//	}
	
	
}
