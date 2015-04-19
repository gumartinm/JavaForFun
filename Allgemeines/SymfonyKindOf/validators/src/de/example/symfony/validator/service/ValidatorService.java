package de.example.symfony.validator.service;

import java.util.HashMap;
import java.util.Map;

import de.example.symfony.validator.PostValidatorBase;
import de.example.symfony.validator.ValidatorBase;


public abstract class ValidatorService {
	protected final Map<String, String> params;
	protected final Map<String, ValidatorBase> validators = new HashMap<>();
	protected final Map<String, PostValidatorBase> postValidators = new HashMap<>();
	
	
	public ValidatorService(Map<String, String> params) {
	    this.params = params; 
    }

	public void doValidate() {
		this.configureValidators();
		
		for (Map.Entry<String, ValidatorBase> validator : validators.entrySet()) {
			validator.getValue().doValidate(params.get(validator.getKey()));
		}
		
	}
	
	public void doPostValidate() {
		this.configurePostValidators();
		
		for (Map.Entry<String, PostValidatorBase> postValidator : postValidators.entrySet()) {
			postValidator.getValue().doPostValidate(params.get(postValidator.getKey()));
		}
		
	}
	
	protected abstract void configureValidators();
	
	protected abstract void configurePostValidators();
}
