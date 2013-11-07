package com.example.videogallery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener {

	Cursor cursor;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ListView listView = (ListView) findViewById(R.id.lv);
		
		String[] thumbCols = { MediaStore.Video.Thumbnails.DATA,
				MediaStore.Video.Thumbnails.VIDEO_ID };

		String[] videoCols = { MediaStore.Video.Media._ID,
				MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE,
				MediaStore.Video.Media.MIME_TYPE };

		cursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
				videoCols, null, null, null);

		ArrayList<VideoInfo> videos = new ArrayList<VideoInfo>();

		if (cursor != null && cursor.moveToFirst()) {

			do {
				VideoInfo vf = new VideoInfo();
				int id = cursor.getInt(cursor
						.getColumnIndex(MediaStore.Video.Media._ID));
				
				//查询缩略图
				Cursor thumbCursor = managedQuery(
						MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
						thumbCols, MediaStore.Video.Thumbnails.VIDEO_ID + "="
								+ id, null, null);
				
				if (thumbCursor.moveToFirst()) {

					vf.thumbPath = thumbCursor.getString(thumbCursor
							.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
					Log.v("Debug thumPath:", vf.thumbPath);
				}

				vf.videoPath = cursor.getString(cursor
						.getColumnIndex(MediaStore.Video.Media.DATA));
				vf.title = cursor.getString(cursor
						.getColumnIndex(MediaStore.Video.Media.TITLE));
				vf.mimeType = cursor.getString(cursor
						.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));

				videos.add(vf);

			} while (cursor.moveToNext());

			listView.setAdapter(new VideoGalleryAdapter(this, videos));
			listView.setOnItemClickListener(this);

		}
	}

	private class VideoInfo {
		String videoPath;
		String thumbPath;
		String mimeType;
		String title;
	}

	private class VideoGalleryAdapter extends BaseAdapter {

		private Context context;
		private List<VideoInfo> videos;

		public VideoGalleryAdapter(Context _context,
				List<VideoInfo> _videos) {
			context = _context;
			videos = _videos;
		}

		@Override
		public int getCount() {
			return videos.size();
		}

		@Override
		public Object getItem(int position) {
			return videos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			//复用同一个View
			if(convert == null)a {

				LayoutInflater inflater;
				// 绑定一个layout xml文件
				inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View row = inflater.inflate(R.layout.list_view, null);
				ImageView thumb = (ImageView) row.findViewById(R.id.imgView);

				if (videos.get(position).thumbPath != null) {
					thumb.setImageURI(Uri.parse(videos.get(position).thumbPath));
				}
				TextView title = (TextView) row.findViewById(R.id.tv);
				title.setText(videos.get(position).title);

				return row;
			} else {

				ImageView thumb = (ImageView) convertView.findViewById(R.id.imgView);

				if (videos.get(position).thumbPath != null) {
					thumb.setImageURI(Uri.parse(videos.get(position).thumbPath));
				}
				TextView title = (TextView) convertView.findViewById(R.id.tv);
				title.setText(videos.get(position).title);
				return convertView;
			}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		if (cursor.moveToPosition(arg2)) {
			int filePathIdx = cursor
					.getColumnIndex(MediaStore.Video.Media.DATA);
			int mimeTypeIdx = cursor
					.getColumnIndex(MediaStore.Video.Media.MIME_TYPE);
			String filePath = cursor.getString(filePathIdx);
			String mimeType = cursor.getString(mimeTypeIdx);

			Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
			File file = new File(filePath);
			intent.setDataAndType(Uri.fromFile(file), mimeType);

			startActivity(intent);
		}
	}

}
