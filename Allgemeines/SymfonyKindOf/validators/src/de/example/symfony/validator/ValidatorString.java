package de.example.symfony.validator;


public class ValidatorString extends ValidatorBase {
	private static final String REQUIRED_DEFAULT_RULE = "validator.string.required";
	private static final String INVALID_DEFAULT_RULE = "validator.string.invalid";
	private static final String MAX_LENGTH_DEFAULT_RULE = "validator.string.length.max";
	private static final String MIN_LENGTH_DEFAULT_RULE = "validator.string.length.min";
	
	private final Integer maxLength;
	private final String maxLenghtRule;
	private final Integer minLength;
	private final String minLengthRule;
	
	public ValidatorString(Integer maxLength, String maxLenghtRule, Integer minLength, String minLengthRule,
			boolean required, String requiredRule, String invalidRule) {
		super(required, requiredRule, invalidRule);
		this.maxLength = maxLength;
		this.maxLenghtRule = maxLenghtRule;
		this.minLength = minLength;
		this.minLengthRule = minLengthRule;
	}
	
	@Override
    protected void validate(String value) {
	    int length = value.codePointCount(0, value.length());
	    
	    if (this.maxLength != null && length > this.maxLength) {
	    	// Wrong semantic: HTTP 422 unprocessable entity.
	    	throw new RuntimeException(this.maxLenghtRule);
	    }
	    
	    if (this.minLength != null && length < this.minLength) {
	    	// Wrong semantic: HTTP 422 unprocessable entity.
	    	throw new RuntimeException(this.minLengthRule);
	    }
    }

	public static class Builder {
		private Integer mMaxLength;
		private String mMaxLenghtRule = MAX_LENGTH_DEFAULT_RULE;
		private Integer mMinLength;
		private String mMinLengthRule = MIN_LENGTH_DEFAULT_RULE;
		private boolean mRequired = true;
		private String mRequiredRule = REQUIRED_DEFAULT_RULE;
		private String mInvalidRule = INVALID_DEFAULT_RULE;
		
        public Builder setMaxLength(Integer maxLength) {
        	this.mMaxLength = maxLength;
        	return this;
        }
		
        public Builder setMaxLenghtRule(String maxLenghtRule) {
        	this.mMaxLenghtRule = maxLenghtRule;
        	return this;
        }
		
        public Builder setMinLength(Integer minLength) {
        	this.mMinLength = minLength;
        	return this;
        }
		
        public Builder setMinLengthRule(String minLengthRule) {
        	this.mMinLengthRule = minLengthRule;
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
		
        public ValidatorString build() {
        	return new ValidatorString(mMaxLength, mMaxLenghtRule, mMinLength, mMinLengthRule,
        			mRequired, mRequiredRule, mInvalidRule);
        }
	}
}
