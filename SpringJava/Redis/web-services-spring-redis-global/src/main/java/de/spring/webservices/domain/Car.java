package de.spring.webservices.domain;

import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Car {

    @NotNull
    private Long id;
    
    @NotNull
    @Size(max = 1024)
    private String content;

    // Required by Jackson :/
    protected Car() {

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
      return Objects.hash(getId(), getContent());
    }

    @Override
    public boolean equals(Object object) {
      if (!(object instanceof Car)) {
        return false;
      }

      final Car other = (Car) object;
      return Objects.equals(getId(), other.getId())
    		  && Objects.equals(getContent(), other.getContent());
    }

}
