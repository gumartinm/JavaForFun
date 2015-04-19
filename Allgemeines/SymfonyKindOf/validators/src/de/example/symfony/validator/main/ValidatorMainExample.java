package de.example.symfony.validator.main;

import java.util.HashMap;
import java.util.Map;

import de.example.symfony.validator.service.impl.ValidatorServiceImpl;


public class ValidatorMainExample {
	private static final String FROMID = "fromId";
	private static final String TOID = "toId";
	private static final String DESCRIPTION = "description";

	public static void main(String[] args) {
		Map<String, String> params = new HashMap<>();
		params.put(FROMID, "50");
		params.put(TOID, "75");
		params.put(DESCRIPTION, "Snake Eyes");
		
		ValidatorServiceImpl validatorService = new ValidatorServiceImpl(params);
		validatorService.doValidate();
		
		validatorService.doPostValidate();
	}

}
