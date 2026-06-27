package com.ced.Feedback.Communication.Service.service;

import com.ced.Feedback.Communication.Service.domain.ProjectInfo;
import com.ced.Feedback.Communication.Service.repository.ProjectInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectInfoService {

    private final ProjectInfoRepository projectInfoRepository;

    @Transactional
    public void upsert(UUID projectId, UUID clientId, UUID projectManagerId) {
        ProjectInfo info = projectInfoRepository.findById(projectId)
                .orElse(ProjectInfo.builder().projectId(projectId).build());
        info.setClientId(clientId);
        info.setProjectManagerId(projectManagerId);
        projectInfoRepository.save(info);
    }

    public UUID findClientId(UUID projectId) {
        return projectInfoRepository.findById(projectId).map(ProjectInfo::getClientId).orElse(null);
    }

    public UUID findProjectManagerId(UUID projectId) {
        return projectInfoRepository.findById(projectId).map(ProjectInfo::getProjectManagerId).orElse(null);
    }
}
