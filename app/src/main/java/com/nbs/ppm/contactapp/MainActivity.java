package com.nbs.ppm.contactapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nbs.ppm.contactapp.adapter.ContactAdapter;
import com.nbs.ppm.contactapp.model.ItemContact;
import com.nbs.ppm.contactapp.util.AppUrl;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


public class MainActivity extends ActionBarActivity implements Observer {

    private ContactAppApplication application;

    private RecyclerView rvItem;
    private ProgressBar indicator;
    private Toolbar toolbar;
    private FloatingActionButton fabAdd;

    private ArrayList<ItemContact> listItem;
    private ContactAdapter adapter;

    private String url = AppUrl.getUrl(AppUrl.ApiAction.VIEW);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        rvItem = (RecyclerView)findViewById(R.id.rv_item);
        rvItem.setHasFixedSize(true);

        LinearLayoutManager lnLayoutManager = new LinearLayoutManager(MainActivity.this);
        rvItem.setLayoutManager(lnLayoutManager);

        indicator = (ProgressBar)findViewById(R.id.pb_progress);
        fabAdd = (FloatingActionButton)findViewById(R.id.fab_add);

        application = (ContactAppApplication)getApplication();
        application.getItemContact().addObserver(this);

        listItem = new ArrayList<>();

        viewRequest();

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FormCreateUpdateActivity.toFormCreateUpdateActivity(MainActivity.this);
            }
        });

    }

    private void viewRequest() {
        rvItem.setVisibility(View.GONE);
        indicator.setVisibility(View.VISIBLE);

        if (listItem.size() > 0){
            listItem.clear();
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(MainActivity.this, url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                rvItem.setVisibility(View.VISIBLE);
                indicator.setVisibility(View.GONE);

                String response = new String(responseBody);
                parseViewResponse(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                rvItem.setVisibility(View.GONE);
                indicator.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Failed to load data", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void parseViewResponse(String response) {
        listItem = ItemContact.getListContact(response);
        if (listItem != null && listItem.size() > 0){
            adapter = new ContactAdapter(MainActivity.this, listItem);
            rvItem.setAdapter(adapter);
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        if (o.equals(ItemContact.NEED_TO_REFRESH)){
            viewRequest();
        }

        if (o.equals(ItemContact.NEED_TO_DELETE)){
            int i = application.getItemContact().getPosition();
            listItem.remove(i);
            adapter.notifyItemRemoved(i);
        }

    }
}
