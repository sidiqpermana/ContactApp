package com.nbs.ppm.contactapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nbs.ppm.contactapp.model.ItemContact;
import com.nbs.ppm.contactapp.util.AppUrl;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;


public class FormCreateUpdateActivity extends ActionBarActivity implements Observer{

    private EditText edtName, edtEmail, edtPhone;
    private Button btnSave;
    private Toolbar toolbar;

    private String url = null;
    public static String KEY_ITEM = "item";
    public static String KEY_POSITION = "position";
    private ItemContact itemContact = null;
    private int position;
    private boolean isUpdateForm = false;

    private ContactAppApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_create_update);

        edtEmail = (EditText)findViewById(R.id.edt_email);
        edtName = (EditText)findViewById(R.id.edt_name);
        edtPhone = (EditText)findViewById(R.id.edt_phone);
        btnSave = (Button)findViewById(R.id.btn_save);
        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        itemContact = (ItemContact) getIntent().getSerializableExtra(KEY_ITEM);
        position = getIntent().getIntExtra(KEY_POSITION, 0);

        application = (ContactAppApplication)getApplication();
        application.getItemContact().addObserver(this);

        String actionBarTitle = null;
        if (itemContact != null){
            isUpdateForm = true;
            actionBarTitle = "Update Contact";
            edtPhone.setText(itemContact.getPhone());
            edtName.setText(itemContact.getName());
            edtEmail.setText(itemContact.getEmail());
        }else{
            actionBarTitle = "Create Contact";
        }

        getSupportActionBar().setTitle(actionBarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();

                if (name.equals("") || email.equals("") || phone.equals("")){
                    Toast.makeText(FormCreateUpdateActivity.this, "All fields are mandatory", Toast.LENGTH_LONG).show();
                }else{
                    RequestParams params = new RequestParams();
                    params.add("name", name);
                    params.add("email", email);
                    params.add("mobile", phone);

                    if (isUpdateForm){
                        params.add("id", itemContact.getId());
                    }

                    postRequest(params);
                }
            }
        });
    }

    private void postRequest(RequestParams params) {
        final ProgressDialog dialog = new ProgressDialog(FormCreateUpdateActivity.this);
        dialog.setMessage("Please wait...");
        dialog.show();

        String url = isUpdateForm ? AppUrl.getUrl(AppUrl.ApiAction.UPDATE) : AppUrl.getUrl(AppUrl.ApiAction.CREATE);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(FormCreateUpdateActivity.this, url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                String response = new String(responseBody);
                parseResponse(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(FormCreateUpdateActivity.this, "Failed to post data", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void parseResponse(String response) {
        try {
            JSONObject object = new JSONObject(response);
            if (object.optString("status").equals("Success")){
                application.getItemContact().onItemChanged();
                Toast.makeText(FormCreateUpdateActivity.this, "Success to post data", Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(FormCreateUpdateActivity.this, "Failed to post data", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Toast.makeText(FormCreateUpdateActivity.this, "Failed to post data", Toast.LENGTH_LONG).show();
        }
    }

    public static void toFormCreateUpdateActivity(Activity activity){
        Intent intent = new Intent(activity, FormCreateUpdateActivity.class);
        activity.startActivity(intent);
    }

    public static void toFormCreateUpdateActivity(Activity activity, ItemContact itemContact, int position){
        Intent intent = new Intent(activity, FormCreateUpdateActivity.class);
        intent.putExtra(KEY_ITEM, itemContact);
        intent.putExtra(KEY_POSITION, position);
        activity.startActivityForResult(intent, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_form_create_update, menu);
        MenuItem item = menu.findItem(R.id.action_delete);
        if (!isUpdateForm){
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            if (isUpdateForm){
                showDeleteAlert(itemContact);
            }
            return true;
        }

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void update(Observable observable, Object o) {

    }

    public void showDeleteAlert(final ItemContact itemContact){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FormCreateUpdateActivity.this);
        alertDialogBuilder.setTitle("Contact App");
        alertDialogBuilder
                .setMessage("Are you sure want to delete this item?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        deleteRequest(itemContact);
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void deleteRequest(final ItemContact item) {
        final ProgressDialog dialog = new ProgressDialog(FormCreateUpdateActivity.this);
        dialog.setTitle("Delete Item");
        dialog.setMessage("Please wait...");
        dialog.show();

        String deleteUrl = AppUrl.getUrl(AppUrl.ApiAction.DELETE);

        RequestParams params = new RequestParams();
        params.put("id", item.getId());

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(FormCreateUpdateActivity.this, deleteUrl, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();

                String response = new String(responseBody);
                parseDeleteResponse(response, item);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(FormCreateUpdateActivity.this, "Failed to delete data", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void parseDeleteResponse(String response, ItemContact itemContact) {
        try {
            JSONObject object = new JSONObject(response);
            if (object.optString("status").equals("Success")){
                application.getItemContact().setPosition(position);
                application.getItemContact().onDeletedItem();
                Toast.makeText(FormCreateUpdateActivity.this, "Success to delete data", Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(FormCreateUpdateActivity.this, "Failed to delete data", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Toast.makeText(FormCreateUpdateActivity.this, "Failed to delete data", Toast.LENGTH_LONG).show();
        }
    }

}
