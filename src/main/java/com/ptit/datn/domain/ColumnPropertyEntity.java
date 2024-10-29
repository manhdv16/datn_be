package com.ptit.datn.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "column_property", schema = "defaultdb", catalog = "")
public class ColumnPropertyEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;
    @Basic
    @Column(name = "key_name")
    private String keyName;
    @Basic
    @Column(name = "key_title")
    private String keyTitle;
    @Basic
    @Column(name = "entity_type")
    private Integer entityType;
    @Basic
    @Column(name = "data_type")
    private Integer dataType;
    @Basic
    @Column(name = "is_active")
    private Boolean isActive;
    @Basic
    @Column(name = "visible")
    private Boolean visible;
    @Basic
    @Column(name = "key_name_search")
    private String keyNameSearch;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyTitle() {
        return keyTitle;
    }

    public void setKeyTitle(String keyTitle) {
        this.keyTitle = keyTitle;
    }

    public Integer getEntityType() {
        return entityType;
    }

    public void setEntityType(Integer entityType) {
        this.entityType = entityType;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getKeyNameSearch() {
        return keyNameSearch;
    }

    public void setKeyNameSearch(String keyNameSearch) {
        this.keyNameSearch = keyNameSearch;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        ColumnPropertyEntity that = (ColumnPropertyEntity) object;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (keyName != null ? !keyName.equals(that.keyName) : that.keyName != null) return false;
        if (keyTitle != null ? !keyTitle.equals(that.keyTitle) : that.keyTitle != null) return false;
        if (entityType != null ? !entityType.equals(that.entityType) : that.entityType != null) return false;
        if (dataType != null ? !dataType.equals(that.dataType) : that.dataType != null) return false;
        if (isActive != null ? !isActive.equals(that.isActive) : that.isActive != null) return false;
        if (visible != null ? !visible.equals(that.visible) : that.visible != null) return false;
        if (keyNameSearch != null ? !keyNameSearch.equals(that.keyNameSearch) : that.keyNameSearch != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (keyName != null ? keyName.hashCode() : 0);
        result = 31 * result + (keyTitle != null ? keyTitle.hashCode() : 0);
        result = 31 * result + (entityType != null ? entityType.hashCode() : 0);
        result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
        result = 31 * result + (isActive != null ? isActive.hashCode() : 0);
        result = 31 * result + (visible != null ? visible.hashCode() : 0);
        result = 31 * result + (keyNameSearch != null ? keyNameSearch.hashCode() : 0);
        return result;
    }
}
