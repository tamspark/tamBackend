package com.sparklab.TAM.repositories;

import com.sparklab.TAM.model.MinStayRule;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MinStayRepository extends JpaRepository<MinStayRule,Long> {

    MinStayRule findByDay(int day);

    List<MinStayRule> findByUserId(Long parseUserId, Sort sortByDay);
}
