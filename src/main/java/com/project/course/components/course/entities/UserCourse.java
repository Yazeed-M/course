package com.project.course.components.course.entities;


import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class UserCourse {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    private Long user_id;
    private Long course_id;
    private LocalDateTime enrolled_at= LocalDateTime.now();;
    
    public UserCourse() {
    }
    
    public UserCourse(Long id, Long user_id, Long course_id, LocalDateTime enrolled_At) {
        this.id = id;
        this.user_id = user_id;
        this.course_id = course_id;
        this.enrolled_at = enrolled_At;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return user_id;
    }

    public void setUserId(Long user_id) {
        this.user_id = user_id;
    }

    public Long getCourseId() {
        return course_id;
    }

    public void setCourseId(Long course_id) {
        this.course_id = course_id;
    }

    public LocalDateTime getEnrolled_At() {
        return enrolled_at;
    }

    public void setEnrolled_At(LocalDateTime enrolled_At) {
        this.enrolled_at = enrolled_At;
    }
    
}
