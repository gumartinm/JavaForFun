package de.spring.webservices.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class LocationType {
	private Integer id;
	private String name;

    /**
     * Required constructor for MyBatis
     */
    protected LocationType() {

    }

	public LocationType(Integer id, String name) {
        this.id = id;
		this.name = name;
    }

	public Integer getId() {
        return id;
    }

	public String getName() {
		return name;
    }


    @Override
    public int hashCode() {
        final HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(this.getId());
        return hcb.toHashCode();
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;

        if (object == null) { return false; }
        if (object == this) { return true; }
        if (object.getClass() != getClass()) {
          return false;
        }

        final LocationType other = (LocationType) object;

        final EqualsBuilder eqb = new EqualsBuilder();
        eqb.append(this.getId(), other.getId());
        result = eqb.isEquals();
        return result;
    }

}
