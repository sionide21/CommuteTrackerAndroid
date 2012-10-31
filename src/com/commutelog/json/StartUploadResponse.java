package com.commutelog.json;

import org.apache.http.client.ResponseHandler;

public class StartUploadResponse {

	private String upload_url;
	private String complete_url;

	public String getUploadUrl() {
		return upload_url;
	}

	public String getCompleteUrl() {
		return complete_url;
	}

	public static ResponseHandler<StartUploadResponse> HANDLER = new GsonHandler<StartUploadResponse>(StartUploadResponse.class);
}
