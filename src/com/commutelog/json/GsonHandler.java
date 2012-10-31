package com.commutelog.json;

import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

import com.google.gson.Gson;

public class GsonHandler<T> implements ResponseHandler<T> {
	private final Gson gson = new Gson();
	private final Class<T> cls;

	public GsonHandler(Class<T> cls) {
		this.cls = cls;
	}

	@Override
	public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new ClientProtocolException(response.getStatusLine().getReasonPhrase());
		}
		return gson.fromJson(new InputStreamReader(response.getEntity().getContent()), cls);
	}
}