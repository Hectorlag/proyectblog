package com.proyecto.blog.repository;

import com.proyecto.blog.model.UserSec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserSecRepository extends JpaRepository<UserSec, Long> {

}
