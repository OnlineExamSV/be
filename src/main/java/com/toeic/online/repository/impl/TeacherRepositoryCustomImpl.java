package com.toeic.online.repository.impl;

import com.toeic.online.repository.TeacherRepositoryCustom;
import com.toeic.online.service.dto.SearchTeacherDTO;
import com.toeic.online.service.dto.TeacherDTO;
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
public class TeacherRepositoryCustomImpl implements TeacherRepositoryCustom {

    private EntityManager entityManager;

    public TeacherRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<TeacherDTO> search(SearchTeacherDTO searchTeacherDTO, Integer page, Integer pageSize) {
        StringBuilder sql = new StringBuilder(
            "SELECT " +
            " t.id, " +
            " t.code," +
            " t.full_name as fullName, " +
            " t.email, " +
            " t.phone, " +
            " t.status, " +
            " case when t.status = 0 then 'Đang khóa' else 'Đang hoạt động' end as statusStr, " +
            " t.create_date as createDate," +
            " t.create_name as createName " +
            " FROM teacher t" +
            " WHERE 1 = 1 "
        );
        if (!searchTeacherDTO.getName().isEmpty()) {
            sql.append(
                " AND (UPPER(t.code) like CONCAT('%', UPPER(:name), '%') or UPPER(t.full_name) like CONCAT('%', UPPER(:name), '%')) "
            );
        }
        if (null != searchTeacherDTO.getStatus()) {
            sql.append(" AND t.status = :status");
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
        NativeQuery<TeacherDTO> query = ((Session) entityManager.getDelegate()).createNativeQuery(sql.toString());
        query
            .addScalar("code", new StringType())
            .addScalar("id", new LongType())
            .addScalar("fullName", new StringType())
            .addScalar("email", new StringType())
            .addScalar("phone", new StringType())
            .addScalar("status", new BooleanType())
            .addScalar("statusStr", new StringType())
            .addScalar("createDate", new InstantType())
            .addScalar("createName", new StringType())
            .setResultTransformer(Transformers.aliasToBean(TeacherDTO.class));
        if (!searchTeacherDTO.getName().isEmpty()) {
            query.setParameter("name", searchTeacherDTO.getName());
        }
        if (null != searchTeacherDTO.getStatus()) {
            query.setParameter("status", searchTeacherDTO.getStatus());
        }
        return query.list();
    }

    @Override
    public List<TeacherDTO> exportData(SearchTeacherDTO searchTeacherDTO) {
        StringBuilder sql = new StringBuilder(
            "SELECT" +
            " t.code," +
            " t.full_name as fullName, " +
            " t.email, " +
            " t.phone, " +
            " t.status, " +
            " case when t.status = 0 then 'Đang khóa' else 'Đang hoạt động' end as statusStr, " +
            " t.create_date as createDate," +
            " t.create_name as createName " +
            " FROM teacher t" +
            " WHERE 1 = 1 "
        );
        if (!searchTeacherDTO.getName().isEmpty()) {
            sql.append(
                " AND (UPPER(t.code) like CONCAT('%', UPPER(:name), '%') or UPPER(t.full_name) like CONCAT('%', UPPER(:name), '%')) "
            );
        }
        if (null != searchTeacherDTO.getStatus()) {
            sql.append(" AND t.status = :status");
        }
        NativeQuery<TeacherDTO> query = ((Session) entityManager.getDelegate()).createNativeQuery(sql.toString());
        query
            .addScalar("code", new StringType())
            .addScalar("fullName", new StringType())
            .addScalar("email", new StringType())
            .addScalar("phone", new StringType())
            .addScalar("status", new BooleanType())
            .addScalar("statusStr", new StringType())
            .addScalar("createDate", new InstantType())
            .addScalar("createName", new StringType())
            .setResultTransformer(Transformers.aliasToBean(TeacherDTO.class));
        if (!searchTeacherDTO.getName().isEmpty()) {
            query.setParameter("name", searchTeacherDTO.getName());
        }
        if (null != searchTeacherDTO.getStatus()) {
            query.setParameter("status", searchTeacherDTO.getStatus());
        }
        return query.list();
    }
}
