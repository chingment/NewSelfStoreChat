package com.hyphenate.chatuidemo.fanju.model;

import com.hyphenate.easeui.domain.EaseUser;

import java.util.List;

public class ContactInfosResultBean {

    public ContactInfosResultBean(){

    }

    private List<EaseUser> Contacts;

    public List<EaseUser> getContacts() {
        return Contacts;
    }

    public void setContacts(List<EaseUser> contacts) {
        Contacts = contacts;
    }
}
