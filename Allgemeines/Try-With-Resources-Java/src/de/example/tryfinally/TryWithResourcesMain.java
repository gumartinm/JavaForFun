package de.example.tryfinally;

public class TryWithResourcesMain {

	public static void main(final String[] args) {
		System.out.println("BEGIN FIRST EXAMPLE");
		System.out.flush();

        try (final ResourceFirst resourceOne = new ResourceFirst();
        	 final ResourceSecond resourceTwo = new ResourceSecond())
        {
            resourceTwo.doSomething();
            resourceOne.doSomething();
        } catch (final Exception e) {
        	System.out.println("Exception from some resource: ");
        	System.out.flush();
        	e.printStackTrace();
        	System.out.flush();
        }    
        
        System.out.println("END FIRST EXAMPLE");
        System.out.flush();

	}
	
	public static class ResourceFirst implements AutoCloseable
	{
		public ResourceFirst() {
			
		}
		
		public void doSomething() {
			System.out.println("ResourceFirst: doSomething");
			System.out.flush();
			throw new RuntimeException("ResourceFirst doSomething RuntimeException!!!");		
		}

		@Override
		public void close() throws Exception {
			System.out.println("I am the close of ResourceFirst");
			System.out.flush();
            throw new Exception("ResourceFirst close Exception!!!");		
		}	
	}
	
	
	public static class ResourceSecond implements AutoCloseable
	{
		public ResourceSecond() {
			
		}
		
		public void doSomething() {
			System.out.println("ResourceSecond: doSomething");
			System.out.flush();
		}

		@Override
		public void close() throws Exception {
			System.out.println("I am the close of ResourceSecond");
			System.out.flush();
            throw new Exception("ResourceSecond close Exception!!!");
		}	
	}
}
