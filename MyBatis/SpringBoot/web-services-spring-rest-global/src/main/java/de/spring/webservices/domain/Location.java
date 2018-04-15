package de.spring.webservices.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Location {
    private Long id;
    private Long parentId;
    private Integer levelId;
    private String description;

    /**
     * Required constructor for MyBatis
     */
    protected Location() {

    }

    public Location(Long id, Long parentId, Integer levelId, String description) {
        this.id = id;
        this.parentId = parentId;
        this.levelId = levelId;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public Long getParentId() {
        return parentId;
    }

    public Integer getLevelId() {
        return levelId;
    }

    public String getDescription() {
        return description;
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

        final Location other = (Location) object;

        final EqualsBuilder eqb = new EqualsBuilder();
        eqb.append(this.getId(), other.getId());
        result = eqb.isEquals();
        return result;
    }

}
