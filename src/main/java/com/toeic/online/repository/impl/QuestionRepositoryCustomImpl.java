package com.toeic.online.repository.impl;

import com.toeic.online.domain.Choice;
import com.toeic.online.repository.ChoiceRepository;
import com.toeic.online.repository.QuestionRepositoryCustom;
import com.toeic.online.service.dto.QuestionDTO;
import com.toeic.online.service.dto.SearchQuestionDTO;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QuestionRepositoryCustomImpl implements QuestionRepositoryCustom {

    private EntityManager entityManager;

    public QuestionRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Autowired
    private ChoiceRepository choiceRepository;

    @Override
    public List<QuestionDTO> search(SearchQuestionDTO searchQuestionDTO, Integer page, Integer pageSize) {
        StringBuilder sql = new StringBuilder();
        sql.append(
            "SELECT q.id,\n" +
            " tq.name as questionTypeName,\n" +
            " tq.code as questionType,\n" +
            " q.question_text as questionText,\n" +
            " s.code as subjectCode,\n" +
            " s.name as subjectName,\n" +
            " q.level as level,\n" +
            " q.point as point,\n" +
            " q.status as status,\n" +
            " q.create_date as createDate,\n" +
            " q.create_name as createName,\n" +
            " q.update_date as updateDate,\n" +
            " q.update_name as updateName\n" +
            " FROM question q left join subject s\n" +
            " on q.subject_code = s.code\n" +
            " left join type_question tq \n" +
            " on tq.code = q.question_type \n" +
            " WHERE 1 = 1 "
        );
        if (!searchQuestionDTO.getName().isEmpty()) {
            sql.append(" AND UPPER(question_text) like CONCAT('%', UPPER(:name), '%') ");
        }
        if (searchQuestionDTO.getSubjectCode() != null) {
            sql.append(" AND s.code = :subjectCode ");
        }
        if (searchQuestionDTO.getStatus() != null) {
            sql.append(" AND q.status = :status ");
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
        NativeQuery<QuestionDTO> query = ((Session) entityManager.getDelegate()).createNativeQuery(sql.toString());
        query
            .addScalar("id", new LongType())
            .addScalar("questionType", new StringType())
            .addScalar("questionTypeName", new StringType())
            .addScalar("questionText", new StringType())
            .addScalar("subjectCode", new StringType())
            .addScalar("subjectName", new StringType())
            .addScalar("level", new IntegerType())
            .addScalar("point", new FloatType())
            .addScalar("status", new BooleanType())
            .addScalar("createDate", new InstantType())
            .addScalar("updateDate", new InstantType())
            .addScalar("createName", new StringType())
            .addScalar("updateName", new StringType())
            .setResultTransformer(Transformers.aliasToBean(QuestionDTO.class));
        if (!searchQuestionDTO.getName().isEmpty()) {
            query.setParameter("name", searchQuestionDTO.getName());
        }
        if (searchQuestionDTO.getSubjectCode() != null) {
            query.setParameter("subjectCode", searchQuestionDTO.getSubjectCode());
        }
        if (searchQuestionDTO.getStatus() != null) {
            query.setParameter("status", searchQuestionDTO.getStatus());
        }
        return query.list();
    }

    @Override
    public List<QuestionDTO> export(SearchQuestionDTO searchQuestionDTO) {
        StringBuilder sql = new StringBuilder();
        sql.append(
            "SELECT q.id,\n" +
            " tq.name as questionType,\n" +
            " q.question_text as questionText,\n" +
            " s.code as subjectCode,\n" +
            " s.name as subjectName,\n" +
            " q.level as level,\n" +
            " q.point as point,\n" +
            " q.status as status,\n" +
            " q.create_date as createDate,\n" +
            " q.create_name as createName,\n" +
            " q.update_date as updateDate,\n" +
            " q.update_name as updateName\n" +
            " FROM question q left join subject s\n" +
            " on q.subject_code = s.code\n" +
            " left join type_question tq \n" +
            " on tq.code = q.question_type \n" +
            " WHERE 1 = 1 "
        );
        if (!searchQuestionDTO.getName().isEmpty()) {
            sql.append(" AND UPPER(question_text) like CONCAT('%', UPPER(:name), '%') ");
        }
        if (searchQuestionDTO.getSubjectCode() != null) {
            sql.append(" AND s.code = :subjectCode ");
        }
        if (searchQuestionDTO.getStatus() != null) {
            sql.append(" AND q.status = :status ");
        }
        NativeQuery<QuestionDTO> query = ((Session) entityManager.getDelegate()).createNativeQuery(sql.toString());
        query
            .addScalar("id", new LongType())
            .addScalar("questionType", new StringType())
            .addScalar("questionText", new StringType())
            .addScalar("subjectCode", new StringType())
            .addScalar("subjectName", new StringType())
            .addScalar("level", new IntegerType())
            .addScalar("point", new FloatType())
            .addScalar("status", new BooleanType())
            .addScalar("createDate", new InstantType())
            .addScalar("updateDate", new InstantType())
            .addScalar("createName", new StringType())
            .addScalar("updateName", new StringType())
            .setResultTransformer(Transformers.aliasToBean(QuestionDTO.class));
        if (!searchQuestionDTO.getName().isEmpty()) {
            query.setParameter("name", searchQuestionDTO.getName());
        }
        if (searchQuestionDTO.getSubjectCode() != null) {
            query.setParameter("subjectCode", searchQuestionDTO.getSubjectCode());
        }
        if (searchQuestionDTO.getStatus() != null) {
            query.setParameter("status", searchQuestionDTO.getStatus());
        }
        return query.list();
    }

    @Override
    public QuestionDTO findByQuestionId(Long questionId) {
        //        QuestionDTO questionDTO = new QuestionDTO();
        // Tìm thông tin của question
        StringBuilder sql = new StringBuilder();
        sql.append(
            "SELECT q.id,\n" +
            " q.question_type as questionType,\n" +
            " q.question_text as questionText,\n" +
            " s.code as subjectCode,\n" +
            " s.name as subjectName,\n" +
            " q.level as level,\n" +
            " q.point as point,\n" +
            " q.status as status,\n" +
            " q.create_date as createDate,\n" +
            " q.create_name as createName,\n" +
            " q.update_date as updateDate,\n" +
            " q.update_name as updateName\n" +
            " FROM question q left join subject s\n" +
            " on q.subject_code = s.code\n" +
            " WHERE 1 = 1 AND q.id = :id"
        );
        NativeQuery<QuestionDTO> query = ((Session) entityManager.getDelegate()).createNativeQuery(sql.toString());
        query
            .addScalar("id", new LongType())
            .addScalar("questionType", new StringType())
            .addScalar("questionText", new StringType())
            .addScalar("subjectCode", new StringType())
            .addScalar("subjectName", new StringType())
            .addScalar("level", new IntegerType())
            .addScalar("point", new FloatType())
            .addScalar("status", new BooleanType())
            .addScalar("createDate", new InstantType())
            .addScalar("updateDate", new InstantType())
            .addScalar("createName", new StringType())
            .addScalar("updateName", new StringType())
            .setResultTransformer(Transformers.aliasToBean(QuestionDTO.class));
        if (null != questionId) {
            query.setParameter("id", questionId);
        }
        return query.uniqueResult();
    }
}
