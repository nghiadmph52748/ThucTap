package org.example.thuctapproject.service;

import org.example.thuctapproject.entity.ProjectEntity;
import org.example.thuctapproject.entity.TaskEntity;
import org.example.thuctapproject.entity.TaskStatus;
import org.example.thuctapproject.entity.UserEntity;
import org.example.thuctapproject.exception.ApiException;
import org.example.thuctapproject.model.request.TaskRequest;
import org.example.thuctapproject.repository.ProjectRepository;
import org.example.thuctapproject.repository.TaskRepository;
import org.example.thuctapproject.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    private static final String DEFAULT_TITLE = "Build API";
    private static final int CREATE_PROJECT_ID = 10;
    private static final int ASSIGN_PROJECT_ID = 100;
    private static final int TASK_ID = 1;
    private static final int ASSIGNEE_ID = 20;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldCreateTaskAndSaveMappedFields_whenRequestIsValid() {
        TaskRequest request = createTaskRequest("Build API integration", "in_progress", CREATE_PROJECT_ID, 21);
        ProjectEntity project = createProject(CREATE_PROJECT_ID);
        UserEntity assignee = createUser(21);

        when(userRepository.findById(21)).thenReturn(Optional.of(assignee));
        when(projectRepository.findById(CREATE_PROJECT_ID)).thenReturn(Optional.of(project));

        taskService.createTask(request);

        ArgumentCaptor<TaskEntity> savedTaskCaptor = ArgumentCaptor.forClass(TaskEntity.class);
        verify(taskRepository).save(savedTaskCaptor.capture());

        TaskEntity savedTask = savedTaskCaptor.getValue();
        assertEquals("Build API integration", savedTask.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, savedTask.getStatus());
        assertEquals(assignee, savedTask.getAssignee());
        assertEquals(project, savedTask.getProject());

        verify(userRepository).findById(21);
        verify(projectRepository).findById(CREATE_PROJECT_ID);
    }

    @Test
    void shouldCreateTaskWithoutAssignee_whenAssigneeIsNull() {
        TaskRequest request = createTaskRequest(DEFAULT_TITLE, "TODO", CREATE_PROJECT_ID, null);
        ProjectEntity project = createProject(CREATE_PROJECT_ID);

        when(projectRepository.findById(CREATE_PROJECT_ID)).thenReturn(Optional.of(project));

        taskService.createTask(request);

        ArgumentCaptor<TaskEntity> savedTaskCaptor = ArgumentCaptor.forClass(TaskEntity.class);
        verify(taskRepository).save(savedTaskCaptor.capture());
        verify(userRepository, never()).findById(anyInt());

        TaskEntity savedTask = savedTaskCaptor.getValue();
        assertEquals(TaskStatus.TODO, savedTask.getStatus());
        assertNull(savedTask.getAssignee());
        assertEquals(project, savedTask.getProject());
    }

    @Test
    void shouldThrowBadRequest_whenProjectIdIsNull_duringCreateTask() {
        TaskRequest request = createTaskRequest(DEFAULT_TITLE, "TODO", null, ASSIGNEE_ID);

        ApiException ex = assertThrows(ApiException.class, () -> taskService.createTask(request));

        assertApiException(ex, "Project id must not be null", "400");
        verifyNoInteractions(taskRepository, userRepository, projectRepository);
    }

    @Test
    void shouldThrowNotFound_whenAssigneeDoesNotExist_duringCreateTask() {
        TaskRequest request = createTaskRequest(DEFAULT_TITLE, "TODO", CREATE_PROJECT_ID, ASSIGNEE_ID);

        when(userRepository.findById(ASSIGNEE_ID)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> taskService.createTask(request));

        assertApiException(ex, "Assignee not found", "404");
        verify(userRepository).findById(ASSIGNEE_ID);
        verify(projectRepository, never()).findById(anyInt());
        verify(taskRepository, never()).save(any(TaskEntity.class));
    }

    @Test
    void shouldThrowNotFound_whenProjectDoesNotExist_duringCreateTask() {
        TaskRequest request = createTaskRequest(DEFAULT_TITLE, "TODO", CREATE_PROJECT_ID, null);

        when(projectRepository.findById(CREATE_PROJECT_ID)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> taskService.createTask(request));

        assertApiException(ex, "Project not found", "404");
        verify(projectRepository).findById(CREATE_PROJECT_ID);
        verify(taskRepository, never()).save(any(TaskEntity.class));
    }

    @Test
    void shouldThrowBadRequest_whenStatusIsNull_duringCreateTask() {
        TaskRequest request = createTaskRequest(DEFAULT_TITLE, null, CREATE_PROJECT_ID, null);

        ApiException ex = assertThrows(ApiException.class, () -> taskService.createTask(request));

        assertApiException(ex, "status must not be null", "400");
        verifyNoInteractions(taskRepository, userRepository, projectRepository);
    }

    @Test
    void shouldThrowNotFound_whenStatusIsInvalid_duringCreateTask() {
        TaskRequest request = createTaskRequest(DEFAULT_TITLE, "unknown", CREATE_PROJECT_ID, null);

        ApiException ex = assertThrows(ApiException.class, () -> taskService.createTask(request));

        assertApiException(ex, "Invalid status value: unknown", "404");
        verifyNoInteractions(taskRepository, userRepository, projectRepository);
    }

    @Test
    void shouldAssignTaskAndSave_whenUserBelongsToProject() {
        TaskEntity task = createTask(TASK_ID, ASSIGN_PROJECT_ID);
        UserEntity assignee = createUser(ASSIGNEE_ID);

        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
        when(userRepository.findById(ASSIGNEE_ID)).thenReturn(Optional.of(assignee));
        when(projectRepository.existsByIdAndUsers_Id(ASSIGN_PROJECT_ID, ASSIGNEE_ID)).thenReturn(true);

        taskService.assignTask(TASK_ID, ASSIGNEE_ID);

        assertEquals(assignee, task.getAssignee());
        verify(taskRepository).save(task);
        verify(projectRepository).existsByIdAndUsers_Id(ASSIGN_PROJECT_ID, ASSIGNEE_ID);
    }

    @Test
    void shouldRejectAssignTask_whenUserNotInTaskProject() {
        TaskEntity task = createTask(2, 101);
        UserEntity assignee = createUser(ASSIGNEE_ID);

        when(taskRepository.findById(2)).thenReturn(Optional.of(task));
        when(userRepository.findById(ASSIGNEE_ID)).thenReturn(Optional.of(assignee));
        when(projectRepository.existsByIdAndUsers_Id(101, ASSIGNEE_ID)).thenReturn(false);

        ApiException ex = assertThrows(ApiException.class, () -> taskService.assignTask(2, ASSIGNEE_ID));

        assertApiException(ex, "User does not belong to the task's project", "403");
        verify(taskRepository, never()).save(any(TaskEntity.class));
    }

    @Test
    void shouldThrowBadRequest_whenTaskIdIsNull_duringAssignTask() {
        ApiException ex = assertThrows(ApiException.class, () -> taskService.assignTask(null, ASSIGNEE_ID));

        assertApiException(ex, "Task id must not be null", "400");
        verifyNoInteractions(taskRepository, userRepository, projectRepository);
    }

    @Test
    void shouldThrowBadRequest_whenUserIdIsNull_duringAssignTask() {
        ApiException ex = assertThrows(ApiException.class, () -> taskService.assignTask(TASK_ID, null));

        assertApiException(ex, "User id must not be null", "400");
        verifyNoInteractions(taskRepository, userRepository, projectRepository);
    }

    @Test
    void shouldThrowNotFound_whenTaskDoesNotExist_duringAssignTask() {
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> taskService.assignTask(TASK_ID, ASSIGNEE_ID));

        assertApiException(ex, "Task not found", "404");
        verify(taskRepository).findById(TASK_ID);
        verify(userRepository, never()).findById(anyInt());
        verify(projectRepository, never()).existsByIdAndUsers_Id(anyInt(), anyInt());
    }

    @Test
    void shouldThrowNotFound_whenAssigneeDoesNotExist_duringAssignTask() {
        TaskEntity task = createTask(TASK_ID, ASSIGN_PROJECT_ID);
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
        when(userRepository.findById(ASSIGNEE_ID)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> taskService.assignTask(TASK_ID, ASSIGNEE_ID));

        assertApiException(ex, "Assignee not found", "404");
        verify(taskRepository).findById(TASK_ID);
        verify(userRepository).findById(ASSIGNEE_ID);
        verify(projectRepository, never()).existsByIdAndUsers_Id(anyInt(), anyInt());
        verify(taskRepository, never()).save(any(TaskEntity.class));
    }

    private void assertApiException(ApiException ex, String message, String code) {
        assertEquals(message, ex.getMessage());
        assertEquals(code, ex.getCode());
    }

    private TaskRequest createTaskRequest(String title, String status, Integer projectId, Integer assigneeId) {
        TaskRequest request = new TaskRequest();
        request.setTitle(title);
        request.setStatus(status);
        request.setProject(projectId);
        request.setAssignee(assigneeId);
        request.setDeadline(LocalDate.now().plusDays(1));
        return request;
    }

    private TaskEntity createTask(Integer taskId, Integer projectId) {
        TaskEntity task = new TaskEntity();
        task.setId(taskId);
        task.setTitle("Task " + taskId);
        task.setStatus(TaskStatus.TODO);
        task.setProject(createProject(projectId));
        task.setDeadline(LocalDate.now().plusDays(1));
        return task;
    }

    private ProjectEntity createProject(Integer id) {
        ProjectEntity project = new ProjectEntity();
        project.setId(id);
        project.setName("Project " + id);
        return project;
    }

    private UserEntity createUser(Integer id) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setFullName("User " + id);
        user.setEmail("user" + id + "@mail.test");
        user.setPassword("secret");
        return user;
    }
}



