package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Task;
import com.cydeo.enums.Status;
import com.cydeo.mapper.ProjectMapper;
import com.cydeo.mapper.TaskMapper;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.TaskRepository;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ProjectMapper projectMapper;
    private final UserService userService;
    private final UserMapper userMapper;

    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper, ProjectMapper projectMapper, UserService userService, UserMapper userMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.projectMapper = projectMapper;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Override
    public List<TaskDTO> listAllTasks() {
        List<Task> taskList=taskRepository.findAll();
        return taskList.stream().map(taskMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public void save(TaskDTO dto) {
        dto.setAssignedDate(LocalDate.now());
        dto.setTaskStatus(Status.OPEN);
        Task task= taskMapper.convertToEntity(dto);
        taskRepository.save(task);
    }

    @Override
    public void update(TaskDTO dto) {
       Optional<Task> foundTask= taskRepository.findById(dto.getId());
       Task convertedTask= taskMapper.convertToEntity(dto);
       if(foundTask.isPresent()) {
           convertedTask.setTaskStatus((dto.getTaskStatus()==null)? foundTask.get().getTaskStatus() :dto.getTaskStatus());
           convertedTask.setAssignedDate(foundTask.get().getAssignedDate());
           taskRepository.save(convertedTask);
       }
    }

    @Override
    public void delete(Long id) {

       Optional<Task> foundTask= taskRepository.findById(id);

       if(foundTask.isPresent()){
           foundTask.get().setIsDeleted(true);
           taskRepository.save(foundTask.get());
       }
    }

    @Override
    public TaskDTO findById(Long id) {
        return null;
    }

    @Override
    public int totalNonCompletedTasks(String projectCode) {
        return taskRepository.totalNonCompletedTasks(projectCode);
    }

    @Override
    public int totalCompletedTasks(String projectCode) {
        return taskRepository.totalCompletedTasks(projectCode);
    }

    @Override
    public void deleteByProject(ProjectDTO dto) {
     List<Task> taskList=taskRepository.findAllByProject(projectMapper.convertToEntity(dto));
     taskList.forEach(task -> delete(task.getId()));
    }

    @Override
    public void completeByProject(ProjectDTO dto) {
        List<Task> taskList=taskRepository.findAllByProject(projectMapper.convertToEntity(dto));
        taskList.stream().map(taskMapper::convertToDto).forEach(taskDTO -> {
        taskDTO.setTaskStatus(Status.COMPLETE);
        update(taskDTO);
        });
    }

    @Override
    public List<TaskDTO> listAllTasksByStatusIsNot(Status status) {
        UserDTO loggedInUser=userService.findByUserName("jhon@employee.com");

        List<Task> taskList= taskRepository.findAllByTaskStatusIsNotAndAssignedEmployee(status,userMapper.convertToEntity(loggedInUser));

        return taskList.stream().map(taskMapper::convertToDto).collect(Collectors.toList());

    }

    @Override
    public List<TaskDTO> listAllTasksByStatusIs(Status status) {
        UserDTO loggedInUser=userService.findByUserName("jhon@employee.com");

        List<Task> taskList= taskRepository.findAllByTaskStatusAndAssignedEmployee(status,userMapper.convertToEntity(loggedInUser));

        return taskList.stream().map(taskMapper::convertToDto).collect(Collectors.toList());
    }
}
