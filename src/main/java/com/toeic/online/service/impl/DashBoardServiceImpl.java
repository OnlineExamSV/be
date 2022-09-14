package com.toeic.online.service.impl;

import com.toeic.online.repository.*;
import com.toeic.online.service.DashBoarchService;
import com.toeic.online.service.dto.ClassroomSearchDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class DashBoardServiceImpl implements DashBoarchService {


    private final ClassroomRepositoryCustom classroomRepositoryCustom;

    private final ExamRepositoryCustom examRepositoryCustom;

    private final TeacherRepository teacherRepository;

    private final ClassroomRepository classroomRepository;

    private final StudentRepository studentRepository;

    private final ExamRepository examRepository;

    public DashBoardServiceImpl(ClassroomRepositoryCustom classroomRepositoryCustom, ExamRepositoryCustom examRepositoryCustom, TeacherRepository teacherRepository, ClassroomRepository classroomRepository, StudentRepository studentRepository, ExamRepository examRepository) {
        this.classroomRepositoryCustom = classroomRepositoryCustom;
        this.examRepositoryCustom = examRepositoryCustom;
        this.teacherRepository = teacherRepository;
        this.classroomRepository = classroomRepository;
        this.studentRepository = studentRepository;
        this.examRepository = examRepository;
    }

    @Override
    public Map<String, Object> getData(String codeTeacher) {
        // Lấy ds lớp học theo teacherCode
        Integer countClassByTeacher = 0;
        if(codeTeacher != "" && codeTeacher != null){
            ClassroomSearchDTO classroomSearchDTO = new ClassroomSearchDTO();
            classroomSearchDTO.setName("");
            classroomSearchDTO.setTeacherCode(codeTeacher);
            classroomSearchDTO.setStatus(1);
            countClassByTeacher = classroomRepositoryCustom.exportData(classroomSearchDTO).size();
        }else{
            countClassByTeacher = classroomRepository.findAll().size();
        }
        // Lấy ds giảng viên áp dụng cho role admin
        Integer countTeacher = teacherRepository.findAll().size();
        // Lấy số lượng sinh viên theo teacher code
        Integer countStudent = studentRepository.findAll().size();
        // Lấy số lượng bài thi đã làm
        Integer countExam = examRepository.findAll().size();

        Map<String, Object> res = new HashMap<>();
        res.put("countClass", countClassByTeacher);
        res.put("countTeacher", countTeacher);
        res.put("countStudent", countStudent);
        res.put("countExam", countExam);
        return res;
    }
}
