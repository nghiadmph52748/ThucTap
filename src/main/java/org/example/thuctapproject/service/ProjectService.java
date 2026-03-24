package org.example.thuctapproject.service;

import org.example.thuctapproject.entity.ProjectEntity;
import org.example.thuctapproject.exception.ApiException;
import org.example.thuctapproject.model.request.ProjectRequest;
import org.example.thuctapproject.model.response.ProjectResponse;
import org.example.thuctapproject.repository.ProjectRepository;
import org.example.thuctapproject.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    public List<ProjectResponse> getAllProject(){
        return projectRepository.findAll().stream().map(ProjectResponse::new).toList();
    }

    public ProjectResponse getProjectById(Integer id){
        if (id == null) throw new ApiException("Project id must not be null", "400");
        return projectRepository.findById(id).map(ProjectResponse::new)
                .orElseThrow(() -> new ApiException("Project not found", "404"));
    }

    public ProjectResponse createProject(ProjectRequest request){
        ProjectEntity projectEntity = MapperUtils.map(request, ProjectEntity.class);
        return new ProjectResponse(projectRepository.save(projectEntity));
    }

    public void updateProject(Integer id, ProjectRequest request){
        if (id == null) throw new ApiException("Project id must not be null", "400");
        ProjectEntity projectEntity = projectRepository.findById(id)
                .orElseThrow(() -> new ApiException("Project not found", "404"));
        projectEntity.setName(request.getName());
        projectRepository.save(projectEntity);
    }

    public void deleteProject(Integer id){
        if (id == null) throw new ApiException("Project id must not be null", "400");
        projectRepository.deleteById(id);
    }
}
