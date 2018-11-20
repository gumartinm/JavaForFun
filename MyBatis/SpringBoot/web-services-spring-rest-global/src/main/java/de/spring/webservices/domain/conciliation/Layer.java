package de.spring.webservices.domain.conciliation;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Layer {
    private Long id;
	private String code;
	private String name;

    /**
     * Required constructor for MyBatis
     */
    protected Layer() {

    }

	public Layer(Long id, String code, String name) {
        this.id = id;
		this.code = code;
		this.name = name;
    }

    public Long getId() {
        return id;
    }

	public String getCode() {
		return code;
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

        final Layer other = (Layer) object;

        final EqualsBuilder eqb = new EqualsBuilder();
        eqb.append(this.getId(), other.getId());
        result = eqb.isEquals();
        return result;
    }

}
