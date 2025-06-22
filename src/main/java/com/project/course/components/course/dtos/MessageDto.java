package com.project.course.components.course.dtos;

public class MessageDto {
        private String courseCode;
        private String courseName;
        private Long userId;

        public MessageDto(){}   

        public String getCourseCode() {
            return courseCode;
        }

        public void setCourseCode(String courseCode) {
            this.courseCode = courseCode;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

    public boolean isValid() {
        return courseCode != null && courseName != null && userId != null;
    }
}
