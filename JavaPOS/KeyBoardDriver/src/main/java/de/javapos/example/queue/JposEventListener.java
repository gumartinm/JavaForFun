package de.javapos.example.queue;

public interface JposEventListener {
	
	public void inputAvailable(int input);
	
	public void errorOccurred(int error);
	
	public void statusUpdateOccurred(int status);
}