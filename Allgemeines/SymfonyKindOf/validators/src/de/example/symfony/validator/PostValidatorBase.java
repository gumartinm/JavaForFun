package de.example.symfony.validator;


public abstract class PostValidatorBase {

	public void doPostValidate(String value) {
		this.postValidate(value);
	}
	
	protected abstract void postValidate(String value);
}
