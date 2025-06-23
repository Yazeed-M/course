package com.project.course.components.course.services;

import java.util.Optional;

import static org.apache.kafka.common.requests.FetchMetadata.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.KafkaException;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.project.course.components.course.dtos.MessageDto;
import com.project.course.components.course.dtos.UserNotificationsDto;
import com.project.course.components.course.entities.Course;
import com.project.course.components.course.entities.UserCourse;
import com.project.course.components.course.repositories.CourseRepository;
import com.project.course.components.course.repositories.UserCourseRepository;

@Service
public class CourseService {

    @Autowired
    private final CourseRepository courseRepo;
    private final UserCourseRepository userCourseRepository;
    private final JobScheduler jobScheduler;
    private final LockService lockService;
    private final KafkaTemplate<String, UserNotificationsDto> kafkaTemplate;
    private final String TOPIC = "user-added";

    public CourseService(CourseRepository courseRepo, JobScheduler jobScheduler, UserCourseRepository userCourseRepository, StringRedisTemplate redisTemplate, LockService lockService, KafkaTemplate<String, UserNotificationsDto> kafkaTemplate) {
        this.courseRepo = courseRepo;
        this.userCourseRepository = userCourseRepository;
        this.jobScheduler = jobScheduler;
        this.lockService = lockService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "user-created", groupId = "course")
    public void createCourse(String message) {
        try {
            MessageDto dto = new ObjectMapper().readValue(message, MessageDto.class);

        if (dto == null) {
            log.warn("Received null message from Kafka");
            return;
        }
        //adds the user if the course exists in db
        Optional<Course> courseOnDb = courseRepo.findCourseByCourseName(dto.getCourseName());
        if (courseOnDb.isPresent()) {
            handleExistentCourse(courseOnDb, dto);
            return;
        }

        // from here forward lock the logic for 24 hours
        String redisKey = "lock:create:course:daily";
        boolean isLocked = lockService.createLock(redisKey);
        if (isLocked) {
            Course course = new Course();
            course.setCourseCode(dto.getCourseCode());
            course.setCourseName(dto.getCourseName());
            Course saved = courseRepo.save(course);

            jobScheduler.enqueue(() -> this.createRecord(dto.getUserId(), saved.getId(),dto.getCourseName()));
        } else {
            log.info("the lock is active");
        }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void createRecord(Long userId, Long courseId, String courseName) {

        UserCourse userCourseRecord = new UserCourse();
        userCourseRecord.setUserId(userId);
        userCourseRecord.setCourseId(courseId);

        UserCourse saved = this.userCourseRepository.save(userCourseRecord);
        jobScheduler.enqueue(() -> this.createUserNotificationRecord(userId, courseName));

    }

    private void handleExistentCourse(Optional<Course> course, MessageDto message) {
        try {
            Optional<UserCourse> checkIfRecordExists = userCourseRepository.findByUserIdAndCourseId(message.getUserId(), course.get().getId());
            if (checkIfRecordExists.isEmpty()) {
                jobScheduler.enqueue(() -> this.createRecord(message.getUserId(), course.get().getId(), course.get().getCourseName()));
            } else {
                throw new IllegalArgumentException("student already registered");
            }
        }catch (KafkaException e){
            e.printStackTrace();
        }
    }

    public void createUserNotificationRecord(Long userId, String courseName) {
        UserNotificationsDto payload = new UserNotificationsDto();
        payload.setCourse_name(courseName);
        payload.setUser_id(userId);

        kafkaTemplate.send(TOPIC, payload);
    }
}
