package com.example.blackjack;

import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new GameFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public static class GameFragment extends Fragment implements OnClickListener{
		   Button okbutton;
		   EditText login1,login2;
	       public View onCreateView(LayoutInflater inflater, ViewGroup container,   Bundle savedInstanceState) {
	                  View rootView = inflater.inflate(R.layout.fragment2,container,false);
	                  okbutton=(Button)rootView.findViewById(R.id.button1);
	                  login1=(EditText)rootView.findViewById(R.id.editText1);
	                  login2=(EditText)rootView.findViewById(R.id.editText2);
	      			  okbutton.setOnClickListener(this);
	                return rootView;
	     }
         
		@Override
		public void onClick(View v) {
			MyDBHelper dbHelper = new MyDBHelper(this.getActivity());
			SQLiteDatabase db = dbHelper.getWritableDatabase();		
			String content = login1.getText().toString();
			String content2 = login2.getText().toString();

      Cursor c=db.rawQuery( 
     "SELECT Name, Password FROM SystemUser WHERE  Name=? and Password=?", new String[]{content,content2});
      int total = c.getCount();
          if(total>0)
          {
        	  Toast.makeText(v.getContext(), content+"老會員歡迎",
        			     Toast.LENGTH_LONG).show();
          }
	      if(total==0)
	      {
			ContentValues values = new ContentValues();
			values.put("Name",content); 
			values.put("Password",content2);
			db.insert("SystemUser",  null, values); 
      	  Toast.makeText(v.getContext(), content+"新會員註冊成功",
 			     Toast.LENGTH_LONG).show();
	      }
			db.close();
			dbHelper.close();
			
			MainActivity parent = (MainActivity) this.getActivity();
			parent.switchFragment();
		}

	}
	public static class mapFragment extends Fragment implements OnClickListener,LocationListener,OnMapClickListener,OnInfoWindowClickListener{
		   Button okbutton;
		   MapView m;
		   GoogleMap map;
		   LocationManager lm; 
		   String best;
		   HttpClient client;
	       public View onCreateView(LayoutInflater inflater, ViewGroup container,   Bundle savedInstanceState) {
	                  View rootView = inflater.inflate(R.layout.mapfragment,container,false);
	                  okbutton=(Button)rootView.findViewById(R.id.button1);
	      			  okbutton.setOnClickListener(this);
	      			m = (MapView) rootView.findViewById(R.id.mapview);
	      			m.onCreate(savedInstanceState);
	      			this.setUpMapIfNeeded();

	      			lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
					this.setUpMap();
	      			Criteria crit = new Criteria();
	      			crit.setAccuracy(Criteria.ACCURACY_FINE);
	      			best = lm.getBestProvider(crit, true);
			        lm.requestLocationUpdates(best, 100, 0, this);
					map.setOnMapClickListener(this);
					map.setOnInfoWindowClickListener(this);
			        client = (HttpClient) new DefaultHttpClient();

	                return rootView;
	     }
	       
      
		@Override
		public void onClick(View v) {
			
			MainActivity parent = (MainActivity) this.getActivity();
			parent.switchFragment2();
		}
		@Override
		public void onResume() {
		        super.onResume();
		        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, this);   //做定位時用
		        m.onResume(); 
		        this.setUpMapIfNeeded();

		 }
		@Override
		public void onPause() {
		        super.onPause();
		        m.onPause();
		        lm.removeUpdates((android.location.LocationListener) this);
		}
		@Override
		public void onDestroy() {
		        super.onDestroy();
		        m.onDestroy();
		}
		@Override
		public void onLowMemory() {
		        super.onLowMemory();
		        m.onLowMemory();
		}

		@Override
		public void onLocationChanged(Location location) {
	        String locationContent = "緯度：" + location.getLatitude() + 
	                                                        "\n經度：" + location.getLongitude() +
	                                                        "\n精度：" + location.getAccuracy() +
	                                                        "\n標高：" + location.getAltitude() +
	                                                        "\n時間：" + location.getTime() +
	                                                        "\n速度：" + location.getSpeed() + 
	                                                        "\n方位：" + location.getBearing();
  			
	        Toast.makeText(getActivity(), locationContent, Toast.LENGTH_SHORT).show();
	   }
		public void setUpMapIfNeeded() {
	        if (map == null) {
	            map =  m.getMap();
	            // Check if we were successful in obtaining the map.
	            if (map != null) {
	                 configureMap();
	            }
	        }
	}
	public void configureMap() {
	    if (map == null)
	        return; // Google Maps not available
	    try {
	        MapsInitializer.initialize(getActivity());
	    }
	    catch (Exception e) {
	        return;
	     }
	     map.setMyLocationEnabled(true);   //顯示目前使用者的方位

	}
	private void setUpMap() {
        /*if (map != null) {
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                map.addMarker(new MarkerOptions().position(loc).title("Marker"));
	                CameraUpdate center= CameraUpdateFactory.newLatLngZoom(loc, 15);
	                 if (center != null)  {
		                map.moveCamera(center);
	                 }
        }*/
		LatLng loc = new LatLng(25.033611, 121.565000);
        MarkerOptions markerOpt = new MarkerOptions();
        markerOpt.position(new LatLng(25.033611, 121.565000));
        markerOpt.title("台北101");
        markerOpt.snippet("於1999年動工，2004年12月31日完工啟用，樓高509.2公尺。");
        markerOpt.draggable(false);
        markerOpt.visible(true);
        markerOpt.anchor(0.5f, 0.5f);//設為圖片中心
        markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher));

        map.addMarker(markerOpt);
        CameraUpdate center= CameraUpdateFactory.newLatLngZoom(loc, 10);
        if (center != null)  {
        	map.moveCamera(center);
        }
}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onMapClick(LatLng point) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);
        markerOptions.snippet("Tap here to remove this marker");
        markerOptions.title("Marker Demo");

        map.addMarker(markerOptions);

		
	}


	@Override
	public void onInfoWindowClick(Marker marker) {
        marker.remove();
		
	}


		



	}
	public static class PlaceholderFragment extends Fragment implements OnClickListener {
        TextView nameTextView,playerTextView,conQuitTextView;
        EditText inputname;
        Button okbutton,hitbutton,staybutton,continuebutton,quitbutton;
        ArrayList<ImageView> dealercards,playercards;
        Blackjack game;

		public PlaceholderFragment() {
		}
		public int getIndentifierByString(String str){
			int id = getActivity().getResources().getIdentifier(str,"id",getActivity().getPackageName());
			return id;
		}
		public int getCardDrawableByString(String suit,String face){
			game.money=500;
			game.de=50;
			int id = getActivity().getResources().getIdentifier(suit+"_"+face,"drawable",getActivity().getPackageName());
			return id;
		}
		public void onClick(View v){
			if(v==okbutton)
			{
				InputMethodManager imm=(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(inputname.getWindowToken(),0);
				nameTextView.setVisibility(View.INVISIBLE);
				inputname.setVisibility(View.INVISIBLE);
				okbutton.setVisibility(View.INVISIBLE);
				hitbutton.setVisibility(View.VISIBLE);
				staybutton.setVisibility(View.VISIBLE);
				playerTextView.setVisibility(View.VISIBLE);
				conQuitTextView.setVisibility(View.VISIBLE);
				
				game = new Blackjack(inputname.getText().toString());
		        Animation animFadein;
		        animFadein = AnimationUtils.loadAnimation(getActivity(), R.anim.myanim);
				for(int i=0;i<2;i++)
				{
					dealercards.get(i).setVisibility(View.VISIBLE);		
					Card card = game.player.card(i);
					ImageView cardView = playercards.get(i);
					cardView.setVisibility(View.VISIBLE);
					cardView.startAnimation(animFadein);
					cardView.setImageResource(getCardDrawableByString(card.suit(),card.face()));				
				}
				playerTextView.setText(inputname.getText().toString()+"' cards:"+game.player.point()+"points"+game.player.state());
			}
			if(v==hitbutton)
			{
		        Animation animFadein;
		        animFadein = AnimationUtils.loadAnimation(getActivity(), R.anim.myanim);

				int i=game.player.cardCount();
				if(i>=10)
					return;
				ImageView cardView=playercards.get(i);
				game.hit();
				Card card =game.player.card(i);
				cardView.setVisibility(View.VISIBLE);
				cardView.startAnimation(animFadein);
				cardView.setImageResource(getCardDrawableByString(card.suit(),card.face()));
				playerTextView.setText(inputname.getText().toString()+"' cards:"+game.player.point()+"points "+game.player.state());
				if(game.player.point()>=21)
					v=staybutton;
			}
			if(v==staybutton)
			{
		        Animation animFadein;
		        animFadein = AnimationUtils.loadAnimation(getActivity(), R.anim.myanim);
				int m=game.dealer.cardCount();
				while(game.dealer.point()<18)
				{
					game.dealer.deal(game.dealer);
				}
				for(int i=0;i<m;i++)
				{
					Card card = game.dealer.card(i);
					ImageView cardView = dealercards.get(i);
					cardView.setVisibility(View.VISIBLE);
					cardView.startAnimation(animFadein);
					cardView.setImageResource(getCardDrawableByString(card.suit(),card.face()));				
				}
				conQuitTextView.setText("dealer' cards:"+game.dealer.point()+"points "+game.dealer.state());
				hitbutton.setVisibility(View.INVISIBLE);
				staybutton.setVisibility(View.INVISIBLE);
				continuebutton.setVisibility(View.VISIBLE);
				quitbutton.setVisibility(View.VISIBLE);
				nameTextView.setVisibility(View.VISIBLE);
				if(game.compete()==-1)
				{
					game.money=game.money-game.de;
					nameTextView.setText("player lose! money:"+game.money);
				}
				if(game.compete()==1)
				{
					game.money=game.money+game.de;
					nameTextView.setText("player win! money:"+game.money);
				}
			}
			if(v==continuebutton)
			{
		        Animation animFadein;
		        animFadein = AnimationUtils.loadAnimation(getActivity(), R.anim.myanim);
				InputMethodManager imm=(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(inputname.getWindowToken(),0);
				nameTextView.setVisibility(View.INVISIBLE);
				inputname.setVisibility(View.INVISIBLE);
				okbutton.setVisibility(View.INVISIBLE);
				hitbutton.setVisibility(View.VISIBLE);
				staybutton.setVisibility(View.VISIBLE);
				continuebutton.setVisibility(View.INVISIBLE);
				quitbutton.setVisibility(View.INVISIBLE);
				playerTextView.setVisibility(View.VISIBLE);
				conQuitTextView.setVisibility(View.VISIBLE);
				
				game = new Blackjack(inputname.getText().toString());
				
				for(int i=0;i<2;i++)
				{
					dealercards.get(i).setVisibility(View.VISIBLE);		
					Card card = game.player.card(i);
					ImageView cardView = playercards.get(i);
					cardView.setVisibility(View.VISIBLE);
					cardView.startAnimation(animFadein);
					cardView.setImageResource(getCardDrawableByString(card.suit(),card.face()));				
				}
				playerTextView.setText(inputname.getText().toString()+"' cards:"+game.player.point()+"points"+game.player.state());
			}
			if(v==quitbutton)
			{
				nameTextView.setVisibility(View.VISIBLE);
				inputname.setVisibility(View.VISIBLE);
				okbutton.setVisibility(View.VISIBLE);
				playerTextView.setVisibility(View.INVISIBLE);
				conQuitTextView.setVisibility(View.INVISIBLE);
				continuebutton.setVisibility(View.INVISIBLE);
				quitbutton.setVisibility(View.INVISIBLE);
				nameTextView.setText("name:");
				for(int i=0;i<game.dealer.cardCount();i++)
				    dealercards.get(i).setVisibility(View.INVISIBLE);
				for(int i=0;i<game.player.cardCount();i++)
				    playercards.get(i).setVisibility(View.INVISIBLE);	
			}
		}
		@Override

        
		
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			nameTextView=(TextView)rootView.findViewById(R.id.textView2);
			playerTextView=(TextView)rootView.findViewById(R.id.textView3);
			conQuitTextView=(TextView)rootView.findViewById(R.id.TextView01);
			inputname=(EditText)rootView.findViewById(R.id.editText1);
			okbutton=(Button)rootView.findViewById(R.id.button1);
			hitbutton=(Button)rootView.findViewById(R.id.button2);
			staybutton=(Button)rootView.findViewById(R.id.button3);
			continuebutton=(Button)rootView.findViewById(R.id.button4);
			quitbutton=(Button)rootView.findViewById(R.id.button5);
			dealercards = new ArrayList<ImageView>();
			playercards = new ArrayList<ImageView>();
			for(int i=1;i<=10;i++)
			{
				int id1=getIndentifierByString("ImageView"+i);
				int id2;
				if(i<10)
					id2 = getIndentifierByString("imageView0"+i);
				else
					id2 = getIndentifierByString("imageView"+i);
				
				ImageView v1=(ImageView)rootView.findViewById(id1);
				ImageView v2=(ImageView)rootView.findViewById(id2);
				v1.setVisibility(View.INVISIBLE);
				v2.setVisibility(View.INVISIBLE);
				dealercards.add(v1);
				playercards.add(v2);
			}
			inputname = (EditText) rootView.findViewById(R.id.editText1);
			MyDBHelper dbHelper = new MyDBHelper(this.getActivity());
			    SQLiteDatabase db = dbHelper.getReadableDatabase();
			    Cursor cursor = 
			            db.query("SystemUser", // a. table
			             new String[] {"ID", "Name"}, // b. column names
			             null, // c. selections 
			             null, // d. selections args
			             null, // e. group by
			             null, // f. having
			             "ID desc", // g. order by
			             null); // h. limit
			   
			    if (cursor != null && cursor.getCount() > 0) {
			        cursor.moveToFirst();
			        inputname.setText(cursor.getString(1));
			}
			    ContentValues values = new ContentValues();
			    values.put("Name", inputname.toString()); 
			     db.insert("SystemUser",  null, values); 

			db.close();
			dbHelper.close();

			okbutton.setOnClickListener(this);
			hitbutton.setOnClickListener(this);
			staybutton.setOnClickListener(this);
			continuebutton.setOnClickListener(this);
			quitbutton.setOnClickListener(this);
			hitbutton.setVisibility(View.INVISIBLE);
			staybutton.setVisibility(View.INVISIBLE);
			continuebutton.setVisibility(View.INVISIBLE);
			quitbutton.setVisibility(View.INVISIBLE);
			playerTextView.setVisibility(View.INVISIBLE);
			conQuitTextView.setVisibility(View.INVISIBLE);

			return rootView;
		}
	}
	 public void switchFragment() {		
		    mapFragment newFragment = new mapFragment();  //新增新頁面的實體
	        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
	        transaction.replace(R.id.container, newFragment);
	        transaction.addToBackStack(null);
	        transaction.commit();		
		
	}
	 public void switchFragment2() {		
		    PlaceholderFragment newFragment = new PlaceholderFragment();  //新增新頁面的實體
	        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
	        transaction.replace(R.id.container, newFragment);
	        transaction.addToBackStack(null);
	        transaction.commit();		
	}
	
	}




