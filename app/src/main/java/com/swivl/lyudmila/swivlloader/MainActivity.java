package com.swivl.lyudmila.swivlloader;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.swivl.lyudmila.swivlloader.adapters.ListAdapter;
import com.swivl.lyudmila.swivlloader.data.ListData;
import com.swivl.lyudmila.swivlloader.events.BaseEvent;
import com.swivl.lyudmila.swivlloader.events.ResultEvent;
import com.swivl.lyudmila.swivlloader.service.UploaderService;
import com.swivl.lyudmila.swivlloader.utils.Utils;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {

    private static final String LIST_DATA = "listData";

    private double mSessionId = BaseEvent.UNDEFINED_SESSION_ID;
    private ListAdapter adapter;
    private ListView listView;
    private ListData[] listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI(savedInstanceState);
    }

    @SuppressWarnings("UnusedDeclaration")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void onEventMainThread(final ResultEvent event) {
        if (event.sessionId == mSessionId)
            return;
        listData = event.listData;
        setAdapter();

        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().removeStickyEvent(ResultEvent.class);
        mSessionId = BaseEvent.UNDEFINED_SESSION_ID;
    }

    private void startUploaderService() {
        EventBus.getDefault().registerSticky(this);
        Intent intentUploader = new Intent(this, UploaderService.class);
        intentUploader.putExtra(BaseEvent.EXTRA_SESSION_ID, BaseEvent.getNewSessionId());
        startService(intentUploader);
    }

    private void initUI(final Bundle savedInstanceState) {
        final TextView txtCheckConn = (TextView) findViewById(R.id.checkConnection);
        if (Utils.checkConnection(this) || savedInstanceState != null) {
            initListView(savedInstanceState);
        } else {
            txtCheckConn.setVisibility(View.VISIBLE);
        }
        txtCheckConn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.checkConnection(MainActivity.this)) {
                    txtCheckConn.setVisibility(View.GONE);
                    initListView(savedInstanceState);
                }
            }
        });
    }

    private void initListView(Bundle savedInstanceState) {
        listView = (ListView) findViewById(R.id.listView);
        if (savedInstanceState != null) {
            listData = (ListData[]) savedInstanceState.getParcelableArray(LIST_DATA);
            if (listData != null)
                setAdapter();
        } else {
            startUploaderService();
        }
    }

    private void setAdapter() {
        adapter = new ListAdapter(this, listData);
        listView.setAdapter(adapter);
        (findViewById(R.id.progressBar)).setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray(LIST_DATA, listData);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }
}
