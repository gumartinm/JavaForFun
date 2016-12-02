package de.rxjava.tests;

import de.rxjava.tests.service.impl.AsyncHTTPClient;

public class MainRxJava {

	public static void main(String[] args) {
		AsyncHTTPClient asyncHTTPClient = new AsyncHTTPClient();
		
		asyncHTTPClient.getPages();
	}
}
