package com.artgr.learner.data.mappers;

import com.artgr.learner.data.entity.UserList;
import com.artgr.learner.data.enums.EnrollmentStatus;
import com.artgr.learner.data.enums.LanguageCode;
import com.artgr.learner.data.presentation.Enrollment;
import com.artgr.learner.service.ProgressService;
import org.springframework.stereotype.Component;

// Shared UserList -> Enrollment DTO mapping for ProgressController (/me/home)
// and EnrollmentController (/enrollments*) - both need the same derived
// fields (wordsMastered, sessions summary, lastActiveAt), computed by
// ProgressService.
@Component
public class EnrollmentMapper {

    private final ProgressService progressService;

    public EnrollmentMapper(ProgressService progressService) {
        this.progressService = progressService;
    }

    public Enrollment toDto(UserList enrollment) {
        Long userId = enrollment.getId().getUserId();
        Long listId = enrollment.getId().getListId();
        return new Enrollment(
                listId,
                enrollment.getList().getName(),
                LanguageCode.valueOf(enrollment.getBaseLang().getCode()),
                LanguageCode.valueOf(enrollment.getList().getTargetLang().getCode()),
                EnrollmentStatus.valueOf(enrollment.getStatus()),
                progressService.wordsMastered(userId, listId),
                progressService.totalWords(listId),
                progressService.exercisesCompleted(userId, listId),
                progressService.exercisesNeeded(listId),
                new Enrollment.SessionsSummary(
                        progressService.sessionsDone(userId, listId),
                        progressService.estimatedTotalSessions(listId)
                ),
                progressService.lastActiveAt(enrollment)
        );
    }
}
