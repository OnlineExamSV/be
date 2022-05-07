package com.toeic.online.repository.impl;

import com.toeic.online.repository.SubjectRepositoryCustom;
import com.toeic.online.service.dto.ClassroomDTO;
import com.toeic.online.service.dto.SearchSubjectDTO;
import com.toeic.online.service.dto.SubjectDTO;
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
public class SubjectRepositoryCustomImpl implements SubjectRepositoryCustom {

    private EntityManager entityManager;

    public SubjectRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<SubjectDTO> exportData(SearchSubjectDTO searchSubjectDTO) {
        StringBuilder sql = new StringBuilder(
            "SELECT distinct s.id, " +
            " s.code," +
            " s.name," +
            " c.name as className," +
            " s.status," +
            " case when s.status = 0 then 'Đang khóa' else 'Đang hoạt động' end as statusStr, " +
            " s.create_date as createDate, s.create_name as createName " +
            "FROM subject s join classroom c " +
            "ON s.class_code = c.code" +
            " WHERE 1 = 1 "
        );
        if (null != searchSubjectDTO.getName()) {
            sql.append(" AND (UPPER(s.code) like CONCAT('%', UPPER(:name), '%') or UPPER(s.name) like CONCAT('%', UPPER(:name), '%') )");
        }
        if (null != searchSubjectDTO.getClassCode()) {
            sql.append(" AND c.code = :classCode ");
        }
        if (null != searchSubjectDTO.getStatus()) {
            sql.append(" AND s.status = :status ");
        }
        NativeQuery<SubjectDTO> query = ((Session) entityManager.getDelegate()).createNativeQuery(sql.toString());
        query
            .addScalar("code", new StringType())
            .addScalar("name", new StringType())
            .addScalar("className", new StringType())
            .addScalar("statusStr", new StringType())
            .addScalar("createDate", new InstantType())
            .addScalar("createName", new StringType())
            .setResultTransformer(Transformers.aliasToBean(SubjectDTO.class));
        if (null != searchSubjectDTO.getName()) {
            query.setParameter("name", searchSubjectDTO.getName());
        }
        if (null != searchSubjectDTO.getClassCode()) {
            query.setParameter("classCode", searchSubjectDTO.getClassCode());
        }
        if (null != searchSubjectDTO.getStatus()) {
            query.setParameter("status", searchSubjectDTO.getStatus());
        }
        return query.list();
    }

    @Override
    public List<SubjectDTO> search(SearchSubjectDTO searchSubjectDTO, Integer page, Integer pageSize) {
        StringBuilder sql = new StringBuilder(
            "SELECT distinct" +
            " s.id, " +
            "s.code," +
            " s.name," +
            " c.name as className," +
            " s.class_code as classCode, " +
            " s.status," +
            " case when s.status = 0 then 'Đang khóa' else 'Đang hoạt động' end as statusStr, " +
            " s.create_date as createDate, s.create_name as createName " +
            "FROM subject s join classroom c " +
            "ON s.class_code = c.code" +
            " WHERE 1 = 1 "
        );
        if (null != searchSubjectDTO.getName()) {
            sql.append(" AND (UPPER(s.code) like CONCAT('%', UPPER(:name), '%') or UPPER(s.name) like CONCAT('%', UPPER(:name), '%') )");
        }
        if (null != searchSubjectDTO.getClassCode()) {
            sql.append(" AND c.code = :classCode ");
        }
        if (null != searchSubjectDTO.getStatus()) {
            sql.append(" AND s.status = :status ");
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
        NativeQuery<SubjectDTO> query = ((Session) entityManager.getDelegate()).createNativeQuery(sql.toString());
        query
            .addScalar("code", new StringType())
            .addScalar("name", new StringType())
            .addScalar("id", new LongType())
            .addScalar("className", new StringType())
            .addScalar("classCode", new StringType())
            .addScalar("status", new BooleanType())
            .addScalar("statusStr", new StringType())
            .addScalar("createDate", new InstantType())
            .addScalar("createName", new StringType())
            .setResultTransformer(Transformers.aliasToBean(SubjectDTO.class));
        if (null != searchSubjectDTO.getName()) {
            query.setParameter("name", searchSubjectDTO.getName());
        }
        if (null != searchSubjectDTO.getClassCode()) {
            query.setParameter("classCode", searchSubjectDTO.getClassCode());
        }
        if (null != searchSubjectDTO.getStatus()) {
            query.setParameter("status", searchSubjectDTO.getStatus());
        }
        return query.list();
    }
}
