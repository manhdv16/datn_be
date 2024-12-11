package com.ptit.datn.repository;

import com.ptit.datn.domain.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long>, JpaSpecificationExecutor<Request> {
}
