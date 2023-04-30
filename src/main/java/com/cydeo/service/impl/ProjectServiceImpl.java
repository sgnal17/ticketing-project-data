package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.entity.Project;
import com.cydeo.mapper.ProjectMapper;
import com.cydeo.repository.ProjectRepository;
import com.cydeo.service.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(ProjectRepository projectRepository, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
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
    projectRepository.save(project);
    }

    @Override
    public void update(ProjectDTO dto) {
        Project project=projectRepository.findByProjectCode(dto.getProjectCode());
        project.setId(project.getId());
        projectRepository.save(project);
    }

    @Override
    public void delete(String code) {
    Project project=projectRepository.findByProjectCode(code);
    projectRepository.delete(project);
    }
}
