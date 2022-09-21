package com.toeic.online.repository.impl;

import com.toeic.online.repository.ExamRepositoryCustom;
import com.toeic.online.service.dto.ClassroomSearchDTO;
import com.toeic.online.service.dto.ClassroomStudentDTO;
import com.toeic.online.service.dto.ExamDTO;
import com.toeic.online.service.dto.QuestionDTO;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;
import org.springframework.stereotype.Component;

@Component
public class ExamRepositoryCustomImpl implements ExamRepositoryCustom {

    private EntityManager entityManager;

    public ExamRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<ExamDTO> search(ClassroomSearchDTO subjectCode, Integer page, Integer pageSize) {
        StringBuilder sql = new StringBuilder();
        sql.append(
            "SELECT e.id,\n" +
            "e.title as title,\n" +
            "s.code as subjectCode,\n" +
            "s.name as subjectName,\n" +
            "e.status as status,\n" +
            "e.begin_exam as beginExam,\n" +
            "e.finish_exam as finishExam,\n" +
            "e.duration_exam as durationExam\n" +
            "FROM exam e join subject s\n" +
            "ON e.subject_code = s.code\n" +
            "WHERE 1 = 1 "
        );
        if (null != subjectCode.getTeacherCode()) {
            sql.append(" AND s.code = :subjectCode ");
        }
        if (null != subjectCode.getStatus()) {
            sql.append(" AND e.status = :status ");
        }
        if (page != null && pageSize != null) {
            Integer offset;
            if (page <= 1) {
                offset = 0;
            } else {
                offset = (page - 1) * pageSize;
            }
            sql.append("  LIMIT " + offset + " , " + pageSize + " ");
        }
        NativeQuery<ExamDTO> query = ((Session) entityManager.getDelegate()).createNativeQuery(sql.toString());
        query
            .addScalar("id", new LongType())
            .addScalar("title", new StringType())
            .addScalar("subjectCode", new StringType())
            .addScalar("subjectName", new StringType())
            .addScalar("status", new BooleanType())
            .addScalar("beginExam", new InstantType())
            .addScalar("finishExam", new InstantType())
            .addScalar("durationExam", new IntegerType())
            .setResultTransformer(Transformers.aliasToBean(ExamDTO.class));
        if (null != subjectCode.getTeacherCode()) {
            query.setParameter("subjectCode", subjectCode.getTeacherCode());
        }
        if (null != subjectCode.getStatus()) {
            query.setParameter("status", subjectCode.getStatus());
        }
        return query.list();
    }

    @Override
    public List<ExamDTO> export(ClassroomSearchDTO subjectCode) {
        StringBuilder sql = new StringBuilder();
        sql.append(
            "SELECT e.id,\n" +
            "e.title as title,\n" +
            "s.code as subjectCode,\n" +
            "s.name as subjectName,\n" +
            "e.status as status,\n" +
            "e.begin_exam as beginExam,\n" +
            "e.finish_exam as finishExam,\n" +
            "e.duration_exam as durationExam\n" +
            "FROM exam e join subject s\n" +
            "ON e.subject_code = s.code\n" +
            "WHERE 1 = 1 "
        );
        if (null != subjectCode.getTeacherCode()) {
            sql.append(" AND s.code = :subjectCode ");
        }
        if (null != subjectCode.getStatus()) {
            sql.append(" AND e.status = :status ");
        }
        NativeQuery<ExamDTO> query = ((Session) entityManager.getDelegate()).createNativeQuery(sql.toString());
        query
            .addScalar("id", new LongType())
            .addScalar("title", new StringType())
            .addScalar("subjectCode", new StringType())
            .addScalar("subjectName", new StringType())
            .addScalar("status", new BooleanType())
            .addScalar("beginExam", new InstantType())
            .addScalar("finishExam", new InstantType())
            .addScalar("durationExam", new IntegerType())
            .setResultTransformer(Transformers.aliasToBean(ExamDTO.class));
        if (null != subjectCode.getTeacherCode()) {
            query.setParameter("subjectCode", subjectCode.getTeacherCode());
        }
        if (null != subjectCode.getStatus()) {
            query.setParameter("status", subjectCode.getStatus());
        }
        return query.list();
    }

    @Override
    public List<ExamDTO> getListExamByStudentCode(String studentCode) {
        StringBuilder sql = new StringBuilder();
        sql.append(
            "SELECT ex.title as title, \n" +
            " ex.id as id, \n" +
            " ex.begin_exam as beginExam, \n" +
            " ex.duration_exam as durationExam, \n" +
            " ex.finish_exam as finishExam, \n" +
            " s.name as subjectName \n" +
            " FROM exam ex join subject s on ex.subject_code = s.code\n" +
            " join classroom c on s.class_code = c.code\n" +
            " join classroom_student cs on cs.class_code = c.code\n" +
            " join student su on su.code = cs.student_code\n" +
            " WHERE 1 = 1"
        );
        if (null != studentCode) {
            sql.append(" AND su.code = :studentCode ");
        }
        NativeQuery<ExamDTO> query = ((Session) entityManager.getDelegate()).createNativeQuery(sql.toString());
        query
            .addScalar("id", new LongType())
            .addScalar("title", new StringType())
            .addScalar("subjectName", new StringType())
            .addScalar("beginExam", new InstantType())
            .addScalar("finishExam", new InstantType())
            .addScalar("durationExam", new IntegerType())
            .setResultTransformer(Transformers.aliasToBean(ExamDTO.class));
        if (null != studentCode) {
            query.setParameter("studentCode", studentCode);
        }
        return query.list();
    }


    @Override
    public ExamDTO findById(Long id) {
        StringBuilder sql = new StringBuilder(
            " SELECT e.begin_exam as beginExam, e.duration_exam as durationExam, " +
            " e.finish_exam as finishExam, e.question_data as questionData, " +
            " e.subject_code as subjectCode, e.title as title, s.class_code as classCode, " +
            " c.name as name, s.name as subjectName FROM exam e inner join subject s " +
            " on e.subject_code = s.code inner join classroom c on c.code = s.class_code" +
            " WHERE e.id =  " + id
        );
        NativeQuery<ExamDTO> query = ((Session) entityManager.getDelegate()).createNativeQuery(sql.toString());
        query
            .addScalar("id", new LongType())
            .addScalar("beginExam", new InstantType())
            .addScalar("durationExam", new IntegerType())
            .addScalar("finishExam", new InstantType())
            .addScalar("subjectCode", new StringType())
            .addScalar("subjectName", new StringType())
            .addScalar("questionData", new StringType())
            .addScalar("name", new StringType())
            .addScalar("title", new StringType())
            .addScalar("classCode", new StringType())
            .setResultTransformer(Transformers.aliasToBean(ExamDTO.class));
        return query.uniqueResult();
    }
}
