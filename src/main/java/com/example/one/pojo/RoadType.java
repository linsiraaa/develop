package com.example.one.pojo;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "road_type")
public class RoadType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer typeId;

    @Column(unique = true)
    private String typeName;

    // Getters and Setters
    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoadType roadType = (RoadType) o;
        return Objects.equals(typeId, roadType.typeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeId);
    }
}