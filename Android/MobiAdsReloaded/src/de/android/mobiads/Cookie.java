package de.android.mobiads;

public class Cookie {
	public static String cookie;
	
	public static void setCookie (String cookie) {
		Cookie.cookie = cookie;
	}
	
	public static String getCookie () {
		return Cookie.cookie;
	}
}
