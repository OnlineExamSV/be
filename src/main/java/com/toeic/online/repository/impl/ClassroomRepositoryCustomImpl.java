package com.toeic.online.repository.impl;

import com.toeic.online.repository.ClassroomRepositoryCustom;
import com.toeic.online.service.dto.ClassroomDTO;
import com.toeic.online.service.dto.ClassroomSearchDTO;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.InstantType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Component;

@Component
public class ClassroomRepositoryCustomImpl implements ClassroomRepositoryCustom {

    private EntityManager entityManager;

    public ClassroomRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<ClassroomDTO> exportData(ClassroomSearchDTO classroomSearchDTO) {
        StringBuilder sql = new StringBuilder(
            "SELECT" +
            " c.id, " +
            " c.code," +
            " c.name," +
            " t.full_name as teacherName," +
            " c.status," +
            " case when c.status = 0 then 'Đang khóa' else 'Đang hoạt động' end as statusStr, " +
            " c.create_date as createDate, c.create_name as createName " +
            "FROM classroom c join teacher t " +
            "ON c.teacher_code = t.code "
        );
        if (null != classroomSearchDTO.getName()) {
            sql.append(" AND (UPPER(c.code) like CONCAT('%', UPPER(:name), '%') or UPPER(c.name) like CONCAT('%', UPPER(:name), '%') )");
        }
        if (null != classroomSearchDTO.getTeacherCode()) {
            sql.append(" AND t.code = :teacherCode ");
        }
        if (null != classroomSearchDTO.getStatus()) {
            sql.append(" AND c.status = :status");
        }
        NativeQuery<ClassroomDTO> query = ((Session) entityManager.getDelegate()).createNativeQuery(sql.toString());
        query
            .addScalar("code", new StringType())
            .addScalar("name", new StringType())
            .addScalar("id", new LongType())
            .addScalar("teacherName", new StringType())
            .addScalar("statusStr", new StringType())
            .addScalar("createDate", new InstantType())
            .addScalar("createName", new StringType())
            .setResultTransformer(Transformers.aliasToBean(ClassroomDTO.class));
        if (null != classroomSearchDTO.getName()) {
            query.setParameter("name", classroomSearchDTO.getName());
        }
        if (null != classroomSearchDTO.getTeacherCode()) {
            query.setParameter("teacherCode", classroomSearchDTO.getTeacherCode());
        }
        if (null != classroomSearchDTO.getStatus()) {
            query.setParameter("status", classroomSearchDTO.getStatus());
        }
        return query.list();
    }

    @Override
    public List<ClassroomDTO> search(ClassroomSearchDTO classroomSearchDTO, Integer page, Integer pageSize) {
        StringBuilder sql = new StringBuilder(
            "SELECT" +
            " c.id, " +
            " c.code, " +
            " c.name, " +
            " t.code as teacherCode, " +
            " t.full_name as teacherName," +
            " c.status," +
            " case when c.status = 0 then 'Đang khóa' else 'Đang hoạt động' end as statusStr, " +
            " c.create_date as createDate, c.create_name as createName " +
            "FROM classroom c join teacher t " +
            "ON c.teacher_code = t.code " +
            " WHERE 1 = 1 "
        );
        if (null != classroomSearchDTO.getName()) {
            sql.append(" AND (UPPER(c.code) like CONCAT('%', UPPER(:name), '%') or UPPER(c.name) like CONCAT('%', UPPER(:name), '%') )");
        }
        if (null != classroomSearchDTO.getTeacherCode()) {
            sql.append(" AND t.code = :teacherCode ");
        }
        if (null != classroomSearchDTO.getStatus()) {
            sql.append(" AND c.status = :status");
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
        NativeQuery<ClassroomDTO> query = ((Session) entityManager.getDelegate()).createNativeQuery(sql.toString());
        query
            .addScalar("code", new StringType())
            .addScalar("id", new LongType())
            .addScalar("name", new StringType())
            .addScalar("teacherCode", new StringType())
            .addScalar("teacherName", new StringType())
            .addScalar("status", new BooleanType())
            .addScalar("statusStr", new StringType())
            .addScalar("createDate", new InstantType())
            .addScalar("createName", new StringType())
            .setResultTransformer(Transformers.aliasToBean(ClassroomDTO.class));
        if (null != classroomSearchDTO.getName()) {
            query.setParameter("name", classroomSearchDTO.getName());
        }
        if (null != classroomSearchDTO.getTeacherCode()) {
            query.setParameter("teacherCode", classroomSearchDTO.getTeacherCode());
        }
        if (null != classroomSearchDTO.getStatus()) {
            query.setParameter("status", classroomSearchDTO.getStatus());
        }
        return query.list();
    }
}
