package com.project.course.components.course.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.course.components.course.entities.UserCourse;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {
}
