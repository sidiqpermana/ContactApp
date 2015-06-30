package com.nbs.ppm.contactapp.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by Sidiq on 18/06/2015.
 */
public class ItemContact extends Observable implements Serializable {
    String id;
    String name;
    String email;
    String phone;
    ItemContact itemContact;
    int position;

    public static String NEED_TO_REFRESH = "needToRefresh";
    public static String NEED_TO_DELETE = "needToDelete";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public static ArrayList<ItemContact> getListContact(String response){
        ArrayList<ItemContact> list = null;
        try {
            JSONObject object = new JSONObject(response);
            if (object.optString("status").equals("Success")){
                JSONArray items = object.optJSONArray("contacts");
                if (items.length() > 0){
                    list = new ArrayList<>();
                    ItemContact itemContact = null;
                    for (int i = 0; i < items.length(); i++){
                        JSONObject contactObj = items.optJSONObject(i);

                        itemContact = new ItemContact();
                        itemContact.setId(contactObj.optString("id"));
                        itemContact.setName(contactObj.optString("name"));
                        itemContact.setEmail(contactObj.optString("email"));
                        itemContact.setPhone(contactObj.optString("mobile"));

                        list.add(itemContact);
                    }
                }
            }
        }catch (Exception e){

        }
        return list;
    }

    public ItemContact getItem() {
        return itemContact;
    }

    public void setItem(ItemContact itemContact) {
        this.itemContact = itemContact;
    }

    public void onItemChanged(){
        setChanged();
        notifyObservers(NEED_TO_REFRESH);
    }

    public void onDeletedItem(){
        setChanged();;
        notifyObservers(NEED_TO_DELETE);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
