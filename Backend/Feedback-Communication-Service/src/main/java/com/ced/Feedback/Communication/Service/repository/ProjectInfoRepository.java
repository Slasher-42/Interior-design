package com.ced.Feedback.Communication.Service.repository;

import com.ced.Feedback.Communication.Service.domain.ProjectInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectInfoRepository extends JpaRepository<ProjectInfo, UUID> {
}
