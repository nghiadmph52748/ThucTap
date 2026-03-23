package org.example.thuctapproject.service;

import org.example.thuctapproject.entity.TaskEntity;
import org.example.thuctapproject.entity.TaskStatus;
import org.example.thuctapproject.entity.UserEntity;
import org.example.thuctapproject.entity.ProjectEntity;
import org.example.thuctapproject.exception.ApiException;
import org.example.thuctapproject.model.request.TaskRequest;
import org.example.thuctapproject.model.response.TaskResponse;
import org.example.thuctapproject.repository.ProjectRepository;
import org.example.thuctapproject.repository.TaskRepository;
import org.example.thuctapproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;

    public List<TaskResponse> getAllTask(){
        return taskRepository.findAll().stream().map(TaskResponse::new).toList();
    }

    public List<TaskResponse> getTaskByProjectId(Integer projectId){
        if (projectId == null) throw new ApiException("Project id must not be null", "400");
        return taskRepository.findAllByProject_Id(projectId).stream().map(TaskResponse::new).toList();
    }

    public List<TaskResponse> getTaskByUserId(Integer userId){
        if (userId == null) throw new ApiException("User id must not be null", "400");
        return taskRepository.findAllByAssignee_Id(userId).stream().map(TaskResponse::new).toList();
    }

    public TaskResponse getTaskById(Integer id){
        if (id == null) throw new ApiException("Task id must not be null", "400");
        return new TaskResponse(taskRepository.findById(id)
                .orElseThrow(() -> new ApiException("Task not found", "410")));
    }

    public void createTask(TaskRequest request){
        // Validate required related IDs (also enforced by @Valid at controller)
        if (request.getProject() == null) throw new ApiException("Project id must not be null", "400");
        if (request.getAssignee() == null) throw new ApiException("Assignee id must not be null", "400");

        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTitle(request.getTitle());
        taskEntity.setStatus(parseStatus(request.getStatus()));

        UserEntity assignee = userRepository.findById(request.getAssignee())
                .orElseThrow(() -> new ApiException("Assignee not found", "410"));
        ProjectEntity project = projectRepository.findById(request.getProject())
                .orElseThrow(() -> new ApiException("Project not found", "410"));

        taskEntity.setAssignee(assignee);
        taskEntity.setProject(project);

        taskRepository.save(taskEntity);
    }

    public void updateTask(Integer id, TaskRequest request){
        if (id == null) throw new ApiException("Task id must not be null", "400");
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new ApiException("Task not found", "410"));

        // Update fields explicitly
        taskEntity.setTitle(request.getTitle());
        taskEntity.setStatus(parseStatus(request.getStatus()));

        Integer assigneeId = request.getAssignee();
        if (assigneeId == null) throw new ApiException("Assignee id must not be null", "400");
        UserEntity assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new ApiException("Assignee not found", "410"));
        taskEntity.setAssignee(assignee);

        Integer projectId = request.getProject();
        if (projectId == null) throw new ApiException("Project id must not be null", "400");
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException("Project not found", "410"));
        taskEntity.setProject(project);

        taskRepository.save(taskEntity);
    }

    public void deleteTask(Integer id){
        if (id == null) throw new ApiException("Task id must not be null", "400");
        taskRepository.deleteById(id);
    }

    private TaskStatus parseStatus(String status) {
        if (status == null) throw new ApiException("status must not be null", "400");
        try {
            return TaskStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ApiException("Invalid status value: " + status, "400");
        }
    }
}
