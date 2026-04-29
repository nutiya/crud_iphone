package org.example.crudstudent.repository;

import org.example.crudstudent.entity.Iphone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IphoneRepository extends JpaRepository<Iphone, Long> {
    boolean existsById(Long id);
}
