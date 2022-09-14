package com.toeic.online.repository.impl;

import com.toeic.online.repository.ClassroomStudentRepositoryCustom;
import com.toeic.online.service.dto.ClassroomStudentDTO;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.InstantType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Component;

@Component
public class ClassroomStudentRepositoryCustomImpl implements ClassroomStudentRepositoryCustom {

    private EntityManager entityManager;

    public ClassroomStudentRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<ClassroomStudentDTO> search(String classCode, String studentCode, Integer page, Integer pageSize) {
        StringBuilder sql = new StringBuilder();
        sql.append(
            "SELECT cs.student_code as studentCode,\n" +
            "s.full_name as studentName,\n" +
            " cs.class_code as classCode, \n" +
            " cs.id as id, \n" +
            "s.email as email,\n" +
            "s.phone as phone \n" +
            "FROM classroom c join classroom_student cs on c.code = cs.class_code\n" +
            "join student s on cs.student_code = s.code WHERE 1 = 1 "
        );
        if (!classCode.isEmpty()) {
            sql.append(" AND c.code = '" + classCode + "' ");
        }
        if (!studentCode.isEmpty()) {
            sql.append(" AND s.code = '" + studentCode + "' ");
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
        NativeQuery<ClassroomStudentDTO> query = ((Session) entityManager.getDelegate()).createNativeQuery(sql.toString());
        query
            .addScalar("studentCode", new StringType())
            .addScalar("studentName", new StringType())
            .addScalar("email", new StringType())
            .addScalar("phone", new StringType())
            .addScalar("classCode", new StringType())
            .addScalar("id", new LongType())
            .setResultTransformer(Transformers.aliasToBean(ClassroomStudentDTO.class));
        return query.list();
    }

    @Override
    public List<ClassroomStudentDTO> exportData(String classCode, String studentCode) {
        StringBuilder sql = new StringBuilder();
        sql.append(
            "SELECT cs.student_code as studentCode,\n" +
            "s.full_name as studentName,\n" +
            "s.email as email,\n" +
            "s.phone as phone \n" +
            "FROM classroom c join classroom_student cs on c.code = cs.class_code\n" +
            "join student s on cs.student_code = s.code WHERE 1 = 1 "
        );
        if (!classCode.isEmpty()) {
            sql.append(" AND c.code = :classCode ");
        }
        if (!studentCode.isEmpty()) {
            sql.append(" AND s.code = :studentCode");
        }
        NativeQuery<ClassroomStudentDTO> query = ((Session) entityManager.getDelegate()).createNativeQuery(sql.toString());
        query
            .addScalar("studentCode", new StringType())
            .addScalar("studentName", new StringType())
            .addScalar("email", new StringType())
            .addScalar("phone", new StringType())
            .setResultTransformer(Transformers.aliasToBean(ClassroomStudentDTO.class));
        if (!classCode.isEmpty()) {
            query.setParameter("classCode", classCode);
        }
        if (!studentCode.isEmpty()) {
            query.setParameter("studentCode", studentCode);
        }
        return query.list();
    }
}
