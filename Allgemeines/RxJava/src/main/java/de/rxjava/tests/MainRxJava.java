package de.rxjava.tests;

import de.rxjava.tests.service.impl.AsyncHTTPClient;
import de.rxjava.tests.service.impl.FlatMapTestImpl;

public class MainRxJava {

	public static void main(String[] args) {
		new AsyncHTTPClient().getPages();
		new FlatMapTestImpl().getWords();
	}
}
