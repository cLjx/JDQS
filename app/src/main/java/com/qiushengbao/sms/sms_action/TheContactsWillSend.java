package com.qiushengbao.sms.sms_action;

import java.util.List;

public class TheContactsWillSend {
	static List<ContactInfo> contacts = null;

	public static List<ContactInfo> getContacts() {
		return contacts;
	}

	public static void setContacts(List<ContactInfo> contacts) {
		TheContactsWillSend.contacts = contacts;
	}

}
