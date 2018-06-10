package com.qiushengbao.sms.sms_action;

import java.util.ArrayList;
import java.util.List;

public class GettedContectsByService {
	public static List<ContactInfo> contactList = new ArrayList<>();

	public static List<ContactInfo> getContactList() {
		return contactList;
	}

	public static void setContactList(List<ContactInfo> contactList) {
		GettedContectsByService.contactList = contactList;
	}
	
}
