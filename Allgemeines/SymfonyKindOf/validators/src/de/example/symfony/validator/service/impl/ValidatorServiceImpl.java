package de.example.symfony.validator.service.impl;

import java.util.Map;

import de.example.symfony.validator.ValidatorNumber;
import de.example.symfony.validator.ValidatorString;
import de.example.symfony.validator.service.ValidatorService;


public class ValidatorServiceImpl extends ValidatorService {
	private static final String FROMID = "fromId";
	private static final String TOID = "toId";
	private static final String DESCRIPTION = "description";
	
	private String fromId;
	private String toId;
	private String description;

	public ValidatorServiceImpl(Map<String, String> params) {
	    super(params);
    }

	@Override
    protected void configureValidators() {	
		validators.put(FROMID, new ValidatorNumber.Builder().setRequired(true).setMin(5).build());
		validators.put(TOID, new ValidatorNumber.Builder().setRequired(true).setMax(100).build());
		validators.put(DESCRIPTION, new ValidatorString.Builder().setRequired(false).setMaxLength(70).setMinLength(5).build()); 
    }

	@Override
    protected void configurePostValidators() {
		
		postValidators.put(FROMID, value ->
		{
			fromId = value;
	        System.out.println("fromId: " + fromId); 
		});
		
		postValidators.put(TOID, value ->
		{
			toId = value;
	        System.out.println("toId: " + toId);

		});
		
		postValidators.put(DESCRIPTION, value ->
		{
			description = value;
	        System.out.println("description: " + description);

		});  
    }

}
