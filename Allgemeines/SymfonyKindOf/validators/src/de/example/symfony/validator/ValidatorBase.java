package de.example.symfony.validator;


public abstract class ValidatorBase {	
	private final boolean required;
	private final String requiredRule;
	private final String invalidRule;
	
	
	public ValidatorBase(boolean required, String requiredRule, String invalidRule) {
		this.required = required;
		this.requiredRule = requiredRule;
		this.invalidRule = invalidRule;
	}
	
	public void doValidate(String value) {
		if (isBlank(value)) {
			if (this.isRequired()) {
				// HTTP 422 unprocessable entity.
		    	throw new RuntimeException(this.requiredRule);
			}
			
			return;
		}
		
		this.validate(value);
	}
	
    public boolean isRequired() {
    	return required;
    }
	
    public String getInvalidRule() {
    	return invalidRule;
    }
	
    public String getRequiredRule() {
    	return requiredRule;
    }
    
    protected abstract void validate(String value);
    
    /**
     * <p>Checks if a String is whitespace, empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is null, empty or whitespace
     * @since 2.0
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }
}
