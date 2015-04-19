package de.example.symfony.validator.service.impl;

import java.util.Map;

import de.example.symfony.validator.PostValidatorBaseCriteria;
import de.example.symfony.validator.ValidatorNumber;
import de.example.symfony.validator.ValidatorString;
import de.example.symfony.validator.service.ValidatorService;


public class ValidatorServiceImpl extends ValidatorService {
	public static final String FROMID = "fromId";
	public static final String TOID = "toId";
	public static final String DESCRIPTION = "description";
	
	private Long fromId;
	private Long toId;
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
	
		postValidators.put(FROMID, new PostValidatorBaseCriteria() {

			@Override
            protected void postValidate(String value) {
				fromId = Long.valueOf(value);
		        System.out.println("fromId: " + fromId); 
            }
			
		});
		
		postValidators.put(TOID, new PostValidatorBaseCriteria() {

			@Override
            protected void postValidate(String value) {
				toId = Long.valueOf(value);
		        System.out.println("toId: " + toId); 
            }
			
		});
		
		postValidators.put(DESCRIPTION, new PostValidatorBaseCriteria() {

			@Override
            protected void postValidate(String value) {
				description = value;
		        System.out.println("description: " + description); 
            }
			
		});
    }

}
