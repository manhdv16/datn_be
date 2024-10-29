package com.ptit.datn.repository;

import com.ptit.datn.domain.ColumnPropertyEntity;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface ColumnPropertyRepository extends JpaRepository<ColumnPropertyEntity, Integer> {
    @Query("select c from ColumnPropertyEntity c where c.entityType = :entityType and c.isActive = true")
    List<ColumnPropertyEntity> findByEntityTypeAndIsActiveTrue(@Param("entityType") Integer entityType);

    default Map<String, ColumnPropertyEntity> findByEntityTypeAndIsActiveTrueMap(Integer entityType){
        return findByEntityTypeAndIsActiveTrue(entityType).stream()
            .collect(Collectors.toMap(ColumnPropertyEntity::getKeyName, Function.identity()));
    }
}
