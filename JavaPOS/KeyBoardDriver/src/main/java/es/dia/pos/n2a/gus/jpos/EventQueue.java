package es.dia.pos.n2a.gus.jpos;




/**
 * Instance factory to get the JPOS device services
 * @author
 */
public class EventQueue extends Thread
{
	//Esto se ejectua en el constructor que hay por defecto (y que no se ve en este caso, 
	 //si quisiera podria hacerlo tambien en ese constructor pero así queda mas ordenado)
	 private boolean isEnabled = false;
	 
	 
	 public void run() {

		 
	 }	 
	 
	 /*private void enable ()
	 {
		 threadEventSpool = new Thread(this);
		 threadEventSpool.start();
		 threadEventSpool.setName("Thread-GUSPOSKeyboard");
	 }*/
}
