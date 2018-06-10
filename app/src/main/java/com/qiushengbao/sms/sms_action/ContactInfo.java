package com.qiushengbao.sms.sms_action;

import java.io.Serializable;

public class ContactInfo implements Serializable {
    private String name;
    private String number;
    private Boolean is_check;

    public ContactInfo(String name, String number, Boolean is_check) {
        this.name = name;
        this.number = number;
        this.is_check = is_check;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public Boolean getIs_check() {
        return is_check;
    }

    public void setIs_check(Boolean is_check) {
        this.is_check = is_check;
    }
}
