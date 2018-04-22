package de.spring.webservices.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class LocationHierarchy {
    private Long id;
    private Long parentId;
    private Long childId;
    private Integer levelId;

    /**
     * Required constructor for MyBatis
     */
    protected LocationHierarchy() {

    }

    public LocationHierarchy(Long id, Long parentId, Long childId, Integer levelId) {
        this.id = id;
        this.parentId = parentId;
        this.childId = childId;
        this.levelId = levelId;
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

    public Long getChildId() {
        return childId;
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

        final LocationHierarchy other = (LocationHierarchy) object;

        final EqualsBuilder eqb = new EqualsBuilder();
        eqb.append(this.getId(), other.getId());
        result = eqb.isEquals();
        return result;
    }

}
