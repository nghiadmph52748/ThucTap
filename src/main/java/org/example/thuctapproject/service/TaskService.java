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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public List<TaskResponse> getMyTasks(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new ApiException("Unauthenticated", "401");
        String email = auth.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found", "404"));
        return taskRepository.findAllByAssignee_Id(user.getId()).stream().map(TaskResponse::new).toList();
    }

    public TaskResponse getTaskById(Integer id){
        if (id == null) throw new ApiException("Task id must not be null", "400");
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new ApiException("Task not found", "404"));
        // Enforce that USER can only view their own tasks
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            boolean isManager = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(r -> r.equals("ROLE_MANAGER"));
            if (!isManager) {
                String email = auth.getName();
                Integer currentUserId = userRepository.findByEmail(email)
                        .map(UserEntity::getId)
                        .orElse(null);
                Integer assigneeId = task.getAssignee() != null ? task.getAssignee().getId() : null;
                if (currentUserId == null || assigneeId == null || !currentUserId.equals(assigneeId)) {
                    throw new ApiException("Forbidden: You can only view your own tasks", "403");
                }
            }
        }
        return new TaskResponse(task);
    }

    public void createTask(TaskRequest request){
        if (request.getProject() == null) throw new ApiException("Project id must not be null", "400");
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTitle(request.getTitle());
        taskEntity.setStatus(parseStatus(request.getStatus()));
        if (request.getAssignee() != null && request.getAssignee() > 0) {
            UserEntity assignee = userRepository.findById(request.getAssignee())
                    .orElseThrow(() -> new ApiException("Assignee not found", "404"));
            taskEntity.setAssignee(assignee);
        }
        ProjectEntity project = projectRepository.findById(request.getProject())
                .orElseThrow(() -> new ApiException("Project not found", "404"));
        taskEntity.setProject(project);
        taskRepository.save(taskEntity);
    }

    public void assignTask(Integer taskId, Integer userId) {
        if (taskId == null) throw new ApiException("Task id must not be null", "400");
        if (userId == null) throw new ApiException("User id must not be null", "400");
        TaskEntity taskEntity = taskRepository.findById(taskId)
                .orElseThrow(() -> new ApiException("Task not found", "404"));
        UserEntity assignee = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("Assignee not found", "404"));
        Integer projectId = taskEntity.getProject().getId();
        boolean isMember = projectRepository.existsByIdAndUsers_Id(projectId, userId);
        if (!isMember) {
            throw new ApiException("User does not belong to the task's project", "403");
        }
        taskEntity.setAssignee(assignee);
        taskRepository.save(taskEntity);
    }

    public void changeStatus(Integer taskId, String status) {
        if (taskId == null) throw new ApiException("Task id must not be null", "400");
        if (taskRepository.findById(taskId).get().getStatus() == TaskStatus.DONE) {
            throw new ApiException("Cannot change status of a completed task", "400");
        }
        TaskEntity taskEntity = taskRepository.findById(taskId)
                .orElseThrow(() -> new ApiException("Task not found", "404"));
        taskEntity.setStatus(parseStatus(status));
        taskRepository.save(taskEntity);
    }


    public void updateTask(Integer id, TaskRequest request){
        if (id == null) throw new ApiException("Task id must not be null", "400");
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new ApiException("Task not found", "404"));

        // Update fields explicitly
        taskEntity.setTitle(request.getTitle());
        taskEntity.setStatus(parseStatus(request.getStatus()));

        Integer assigneeId = request.getAssignee();
        if (assigneeId == null) throw new ApiException("Assignee id must not be null", "400");
        UserEntity assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new ApiException("Assignee not found", "404"));
        taskEntity.setAssignee(assignee);

        Integer projectId = request.getProject();
        if (projectId == null) throw new ApiException("Project id must not be null", "400");
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException("Project not found", "404"));
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
            throw new ApiException("Invalid status value: " + status, "404");
        }
    }
}
