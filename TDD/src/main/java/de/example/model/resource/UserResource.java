package de.example.model.resource;


public class UserResource {
	private final Long id;
	private final String name;
	
	public UserResource(Long id, String name) {
		this.id = id;
		this.name = name;
	}

    public Long getId() {
    	return id;
    }

	
    public String getName() {
    	return name;
    }

	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((name == null) ? 0 : name.hashCode());
	    return result;
    }

	@Override
    public boolean equals(Object obj) {
	    if (this == obj) {
		    return true;
	    }
	    if (obj == null) {
		    return false;
	    }
	    if (getClass() != obj.getClass()) {
		    return false;
	    }
	    UserResource other = (UserResource)obj;
	    if (name == null) {
		    if (other.name != null) {
			    return false;
		    }
	    } else if (!name.equals(other.name)) {
		    return false;
	    }
	    return true;
    }

}
