import javax.inject.Named;

@Named("aService")
public class AService {
	private static final Integer FIELD1;
	
	private final Integer field2;
	
	private Integer field3;
	
	public static final Integer field4;
	
	public final Integer field5;
	
	// Noncompliant@+1 [[startColumn=6;endLine=+0;endColumn=10;effortToFix=4]] {{Remove this misleading mutable service instance fields or make it \"static\" and/or \"final\"}}
	public Integer field6;

}
