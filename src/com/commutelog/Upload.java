package com.commutelog;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.commutelog.json.CommuteUploadResponse;
import com.commutelog.json.StartUploadResponse;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Upload extends Activity {

    private Uploader uploader;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
        uploader = new Uploader(getIntent().getDataString());
    }

    public void startUpload(View v) {
        setContentView(R.layout.upload_progress);
        uploader.execute();
    }

    class Uploader extends AsyncTask<Void, String, Void> {

    	private final String url;
		public Uploader(String url) {
			// We use the upload URI so HTC phones can scan (thanks Apple)
			this.url = url.replaceFirst("^upload:", "http:");
    	}

		@Override
		protected Void doInBackground(Void... params) {
			HttpClient client = new DefaultHttpClient();

			try {
				StartUploadResponse session = client.execute(new HttpPost(url), StartUploadResponse.HANDLER);
				// TODO: This is where we actually upload
				for (int i = 0; i < 5; i++) {
					publishProgress("file_" + i + ".json... ");
					CommuteUploadResponse response = client.execute(new HttpPost(session.getUploadUrl()), CommuteUploadResponse.HANDLER);
					if (response.hasError()) {
						publishProgress("Error!\n");
					} else {
						publishProgress("Done.\n");
						// TODO: Delete file
						try {
							Thread.sleep(4000);
						} catch (InterruptedException e) {}
					}
				}

				publishProgress("Finished");
				client.execute(new HttpPost(session.getCompleteUrl()));
			} catch (ClientProtocolException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			TextView fileList = (TextView) findViewById(R.id.file_list);
			fileList.setText(fileList.getText() + values[0]);
		}

		@Override
		protected void onPostExecute(Void result) {
			findViewById(R.id.file_upload_progress).setVisibility(View.INVISIBLE);
		}
    }
}