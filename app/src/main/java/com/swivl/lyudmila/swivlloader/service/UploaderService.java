/**
 * Copyright 2015 Alex Yanchenko
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.swivl.lyudmila.swivlloader.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.swivl.lyudmila.swivlloader.utils.Utils;
import com.swivl.lyudmila.swivlloader.data.ListData;
import com.swivl.lyudmila.swivlloader.events.BaseEvent;
import com.swivl.lyudmila.swivlloader.events.ErrorEvent;
import com.swivl.lyudmila.swivlloader.events.ResultEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.greenrobot.event.EventBus;

/**
 * Created by Lyudmila on 06.09.2015.
 */
public class UploaderService extends Service {

	private double mSessionId;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		double sessionId = intent.getDoubleExtra(BaseEvent.EXTRA_SESSION_ID, BaseEvent.UNDEFINED_SESSION_ID);

		if (sessionId == BaseEvent.UNDEFINED_SESSION_ID) {
			throw new IllegalArgumentException("Invalid session id!");
		}
		mSessionId = sessionId;

		new generateResultThread().start();
		return Service.START_REDELIVER_INTENT;
	}

	class generateResultThread extends Thread {

		public void run() {
			try {
				ListData[] listData = getResponse();
				EventBus.getDefault().postSticky(new ResultEvent(mSessionId, listData));
			} catch (Exception e) {
				e.printStackTrace();
				EventBus.getDefault().postSticky(new ErrorEvent(UploaderService.this, mSessionId, e));
			}
			super.run();
		}
	}

	public ListData[] getResponse() throws IOException {
		URL url = new URL(Utils.URL_DATA_USERS);
		HttpURLConnection  urlConnection = (HttpURLConnection) url.openConnection();
		InputStream in = new BufferedInputStream(urlConnection.getInputStream());

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder result = new StringBuilder();

		String sResponse;
		while ((sResponse = reader.readLine()) != null) {
			result.append(sResponse);
		}

		String response = result.toString();
		return Utils.JSONParserMethod(response);
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
