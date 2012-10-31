package com.commutelog.json;

import org.apache.http.client.ResponseHandler;

public class CommuteUploadResponse {

	private String error;
	private Integer commute;

	public boolean hasError() {
		return error != null;
	}

	public String getError() {
		return error;
	}

	public Integer getCommuteId() {
		return commute;
	}

	public static ResponseHandler<CommuteUploadResponse> HANDLER = new GsonHandler<CommuteUploadResponse>(CommuteUploadResponse.class);
}
