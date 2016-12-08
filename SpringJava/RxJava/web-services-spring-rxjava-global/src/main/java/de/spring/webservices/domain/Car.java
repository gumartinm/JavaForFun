package de.spring.webservices.domain;

public class Car {

    private final Long id;
    private final String content;

    // Required by Jackson :/
    public Car() {
    	this.id = null;
    	this.content = null;
    }
    
    public Car(Long id, String content) {
        this.id = id;
        this.content = content;
    }

    
	public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Car other = (Car) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
