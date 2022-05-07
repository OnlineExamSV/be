package com.toeic.online.commons;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MapperUtils {
    private static ModelMapper mm = new ModelMapper();
    private static Gson gson = new Gson();
    static {
        mm.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public static <T, E> T map(E entity, Class<T> clazz) {
        return mm.map(entity, clazz);
    }

    public static <T, E> void mapFromSource(E source, T updatedOn) {
        mm.map(source, updatedOn);
    }

    public static <T, E> List<T> mapList(List<E> entity, Class<T> clazz) {
        return entity.stream().map(e -> mm.map(e, clazz)).collect(Collectors.toList());
    }

    public static <T> String toJson(T obj) {
        return gson.toJson(obj);
    }

    public static JsonObject toJsonObject(String json) {
        return gson.fromJson(json, JsonObject.class);
    }

    public static <T> Page<T> getPageImpl(EntityManager em, String sql, Map<String, Object> params, Pageable pageable, String resultMapping) {
        Query countQuery = em.createNativeQuery("SELECT Count(*) from (" + sql + ") a ");
        Query query = em.createNativeQuery(sql, resultMapping);
        if (!pageable.isUnpaged()) {
            query.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize());
        }
        if (!StringUtils.isNullOrEmpty(params)) {
            setQueryParams(query, countQuery, params);
        }
        List<T> result = query.getResultList();

        return new PageImpl<>(result, pageable, ((BigInteger) countQuery.getSingleResult()).intValue());
    }

    public static void setQueryParams(Query query, Query countQuery, Map<String, Object> params) {
        if (null == params || params.isEmpty()) {
            return;
        }

        params.forEach((k, v) -> {
            query.setParameter(k, v);
            countQuery.setParameter(k, v);
        });
    }

    public static void setQueryParams(Query query, Map<String, Object> params) {
        if (null == params || params.isEmpty()) {
            return;
        }

        params.forEach((k, v) -> {
            query.setParameter(k, v);
        });
    }
}
