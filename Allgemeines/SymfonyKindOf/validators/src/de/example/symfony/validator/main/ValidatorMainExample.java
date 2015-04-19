package de.example.symfony.validator.main;

import java.util.HashMap;
import java.util.Map;

import de.example.symfony.validator.service.impl.ValidatorServiceImpl;


public class ValidatorMainExample {

	public static void main(String[] args) {
		Map<String, String> params = new HashMap<>();
		params.put(ValidatorServiceImpl.FROMID, "50");
		params.put(ValidatorServiceImpl.TOID, "75");
		params.put(ValidatorServiceImpl.DESCRIPTION, "Snake Eyes");
		
		ValidatorServiceImpl validatorService = new ValidatorServiceImpl(params);
		
		validatorService.doValidate();
		
		validatorService.doPostValidate();
	}

}
