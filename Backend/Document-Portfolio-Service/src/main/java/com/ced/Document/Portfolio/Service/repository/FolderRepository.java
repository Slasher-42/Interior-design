package com.ced.Document.Portfolio.Service.repository;

import com.ced.Document.Portfolio.Service.domain.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FolderRepository extends JpaRepository<Folder, UUID> {

    List<Folder> findByProjectId(UUID projectId);
}
