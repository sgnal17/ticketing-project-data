package com.cydeo.service.impl;

import com.cydeo.dto.TaskDTO;
import com.cydeo.entity.Task;
import com.cydeo.enums.Status;
import com.cydeo.mapper.TaskMapper;
import com.cydeo.repository.TaskRepository;
import com.cydeo.service.TaskService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
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
           convertedTask.setTaskStatus(foundTask.get().getTaskStatus());
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
}
