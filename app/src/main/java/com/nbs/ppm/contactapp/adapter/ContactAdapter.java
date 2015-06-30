package com.nbs.ppm.contactapp.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nbs.ppm.contactapp.FormCreateUpdateActivity;
import com.nbs.ppm.contactapp.R;
import com.nbs.ppm.contactapp.model.ItemContact;

import java.util.ArrayList;

/**
 * Created by Sidiq on 18/06/2015.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.PersonViewHolder> {

    Activity activity;
    ArrayList<ItemContact> listItem;

    public ContactAdapter(Activity activity, ArrayList<ItemContact> listItem){
        this.activity = activity;
        this.listItem = listItem;
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contact, viewGroup, false);
        PersonViewHolder personViewHolder = new PersonViewHolder(view);

        return personViewHolder;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, final int i) {
        personViewHolder.txtName.setText(listItem.get(i).getName());
        personViewHolder.txtPhone.setText(listItem.get(i).getPhone());
        personViewHolder.txtEmail.setText(listItem.get(i).getEmail());

        personViewHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FormCreateUpdateActivity.toFormCreateUpdateActivity(activity, listItem.get(i), i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }


    public static class PersonViewHolder extends RecyclerView.ViewHolder{
        CardView item;
        TextView txtName, txtEmail, txtPhone;

        PersonViewHolder(View viewItem){
            super(viewItem);

            item = (CardView)viewItem.findViewById(R.id.item);
            txtName = (TextView)viewItem.findViewById(R.id.txt_item_name);
            txtEmail = (TextView)viewItem.findViewById(R.id.txt_item_email);
            txtPhone = (TextView)viewItem.findViewById(R.id.txt_item_phone);
        }
    }


}
