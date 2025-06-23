package com.project.course.components.course.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.course.components.course.entities.UserCourse;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {

    Optional<UserCourse> findByUserIdAndCourseId(Long userId, Long courseId);
}
