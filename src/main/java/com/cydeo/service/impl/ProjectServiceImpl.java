package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Project;
import com.cydeo.entity.User;
import com.cydeo.enums.Status;
import com.cydeo.mapper.ProjectMapper;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.ProjectRepository;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final TaskService taskService;

    public ProjectServiceImpl(ProjectRepository projectRepository, ProjectMapper projectMapper, UserService userService, UserMapper userMapper, TaskService taskService) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.userService = userService;
        this.userMapper = userMapper;
        this.taskService = taskService;
    }

    @Override
    public List<ProjectDTO> listAllProjects() {
        List<Project> projectList=projectRepository.findAll();

       return projectList.stream().map(p->projectMapper.convertToDto(p)).collect(Collectors.toList());

    }

    @Override
    public ProjectDTO getByProjectCode(String code) {
        Project project=projectRepository.findByProjectCode(code);
        return  projectMapper.convertToDto(project);

    }

    @Override
    public void save(ProjectDTO dto) {
    Project project=projectMapper.convertToEntity(dto);
    project.setProjectStatus(Status.OPEN);
    projectRepository.save(project);
    }

    @Override
    public void update(ProjectDTO dto) {
        Project project=projectRepository.findByProjectCode(dto.getProjectCode());
        Project convertedProject= projectMapper.convertToEntity(dto);
        convertedProject.setId(project.getId());
        convertedProject.setProjectStatus(project.getProjectStatus());
        projectRepository.save(convertedProject);
    }

    @Override
    public void delete(String code) {

        Project project=projectRepository.findByProjectCode(code);
        // hard delete
        // projectRepository.delete(project);
        // soft delete
        project.setIsDeleted(true);
        //changing current project code to make this project code for future use
        project.setProjectCode(project.getProjectCode()+"-"+project.getId());
        projectRepository.save(project);

        //after I delete project I need to delete tasks that belongs to this project
        taskService.deleteByProject(projectMapper.convertToDto(project));
    }

    @Override
    public void complete(String code) {
      Project project=projectRepository.findByProjectCode(code);
      project.setProjectStatus(Status.COMPLETE);
      projectRepository.save(project);

      taskService.completeByProject(projectMapper.convertToDto(project));
    }

    @Override
    public List<ProjectDTO> listAllProjectDetails() {
      UserDTO currentUserDTO= userService.findByUserName("harold@manager.com");
      User user=userMapper.convertToEntity(currentUserDTO);

      List<Project> projectList=projectRepository.getProjectsByAssignedManager(user);
    return  projectList.stream().map(project -> {
         ProjectDTO obj= projectMapper.convertToDto(project);
         obj.setUnfinishedTaskCounts(taskService.totalNonCompletedTasks(project.getProjectCode()));
         obj.setCompleteTaskCounts(taskService.totalCompletedTasks(project.getProjectCode()));

         return obj;
      }).collect(Collectors.toList());



    }
}
