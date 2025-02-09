package com.akshay.messenger;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class ChatListActivity extends ListActivity
{
	private ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
	private ProgressDialog progress;
	private ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_list_item);

		list = (ListView) findViewById(android.R.id.list);

		new AsyncTask<Void, Void, Void>()
		{
			@Override
			protected void onPreExecute()
			{
				Log.i(Common.TAG, "ChatListActivity Inside preExecute");

				progress = new ProgressDialog(ChatListActivity.this);
				progress.setMessage("Loading your chat...");
				progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progress.setIndeterminate(true);
				progress.show();

				Log.i(Common.TAG, "ChatListActivity Inside preExecute ended.");
			}

			@Override
			protected Void doInBackground(Void... params)
			{
				Log.i(Common.TAG, "ChatListActivity Inside doInBackground");

				String textURI = "mongodb://akshaysoam8:Akshay24@ds035250.mongolab.com:35250/akshay";
				MongoClientURI uri = new MongoClientURI(textURI);

				try
				{
					MongoClient client = new MongoClient(uri);

					DB db = client.getDB("akshay");
					DBCollection collection = db.getCollection("profile");

					DBCursor cursor = collection.find();

					while (cursor.hasNext())
					{
						if (data.isEmpty())
							Log.i(Common.TAG, "ChatListActivity data list is empty");

						HashMap<String, String> map = new HashMap<String, String>();

						DBObject object = cursor.next();

						Log.i(Common.TAG, "ChatListActivity object found name : " + object.get("name").toString());

						map.put("name", object.get("name").toString());

						Log.i(Common.TAG, "ChatListActivity object found email : " + object.get("email").toString());

						map.put("email", object.get("email").toString());

						data.add(map);

						if (!data.isEmpty())
							Log.i(Common.TAG, "ChatListActivity data list not empty");
					}
				}

				catch (UnknownHostException e)
				{
					e.printStackTrace();
				}

				Log.i(Common.TAG, "ChatListActivity doInBackground ended.");

				return null;
			}

			@Override
			protected void onPostExecute(Void params)
			{
				Log.i(Common.TAG, "ChatListActivity Inside onPostExecute");

				progress.dismiss();

				String[] from = { "name", "email" };
				int[] to = { android.R.id.text1, android.R.id.text2 };

				SimpleAdapter adapter = new SimpleAdapter(ChatListActivity.this, data, android.R.layout.simple_list_item_2, from, to);
				list.setAdapter(adapter);

				Log.i(Common.TAG, "ChatListActivity onPostExecute ended.");
			}
		}.execute();

		list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				TextView email = (TextView) findViewById(android.R.id.text2);
				Constants.RECEIVER_EMAIL = email.getText().toString();

				Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onPause()
	{
		super.onPause();
		if (progress != null)
			progress.dismiss();
	}
}
