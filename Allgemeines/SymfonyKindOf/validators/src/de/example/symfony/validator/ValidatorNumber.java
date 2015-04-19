package de.example.symfony.validator;



public class ValidatorNumber extends ValidatorBase {
	private static final String REQUIRED_DEFAULT_RULE = "validator.number.required";
	private static final String INVALID_DEFAULT_RULE = "validator.number.invalid";
	private static final String MAX_DEFAULT_RULE = "validator.number.max";
	private static final String MIN_DEFAULT_RULE = "validator.number.min";
	
	private final Integer max;
	private final String maxRule;
	private final Integer min;
	private final String minRule;
	
	
	public ValidatorNumber(Integer max, String maxRule, Integer min, String minRule,
							boolean required, String requiredRule, String invalidRule) {
		super(required, requiredRule, invalidRule);
	    this.max = max;
	    this.maxRule = maxRule;
	    this.min = min;
	    this.minRule = minRule;
    }
	
	@Override
    protected void validate(String value) {
	    if (!isNumeric(value)) {
	    	// Wrong syntax: HTTP 400 bad request.
	    	throw new RuntimeException(this.getInvalidRule());
	    }
	    
	    double clean = Double.valueOf(value);
	    
	    if (this.max != null && clean > this.max) {
	    	// Wrong semantic: HTTP 422 unprocessable entity.
	    	throw new RuntimeException(this.maxRule);
	    }
	    
	    if (this.min != null && clean < this.min) {
	    	// Wrong semantic: HTTP 422 unprocessable entity.
	    	throw new RuntimeException(this.minRule);
	    }
    }

	public static class Builder {
		private Integer mMax;
		private String mMaxRule = MAX_DEFAULT_RULE;
		private Integer mMin;
		private String mMinRule = MIN_DEFAULT_RULE;
		private boolean mRequired = true;
		private String mRequiredRule = REQUIRED_DEFAULT_RULE;
		private String mInvalidRule = INVALID_DEFAULT_RULE;
		
		
        public Builder setMax(int max) {
        	this.mMax = max;
        	return this;
        }
		
        public Builder setMaxRule(String maxRule) {
        	this.mMaxRule = maxRule;
        	return this;
        }
        
        public Builder setMin(int min) {
        	this.mMin = min;
        	return this;
        }
        
        public Builder setMinRule(String minRule) {
        	this.mMinRule = minRule;
        	return this;
        }
		
        public Builder setRequired(boolean required) {
        	this.mRequired = required;
        	return this;
        }

        public Builder setRequiredRule(String requiredRule) {
        	this.mRequiredRule = requiredRule;
        	return this;
        }
		
        public Builder setInvalidRule(String invalidRule) {
        	this.mInvalidRule = invalidRule;
        	return this;
        }
		
        public ValidatorNumber build() {
        	return new ValidatorNumber(mMax, mMaxRule, mMin, mMinRule,
        			mRequired, mRequiredRule, mInvalidRule);
        }
	}
	
	private boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
		}
		catch(NumberFormatException exception) {
			return false;
		}
		
		return true;
	}
}
