package com.javaweb.repository.custom.impl;

import com.javaweb.builder.BuildingSearchBuilder;
import com.javaweb.entity.BuildingEntity;
import com.javaweb.repository.custom.BuildingRepositoryCustom;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class BuildingRepositoryImpl implements BuildingRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<BuildingEntity> findAll(BuildingSearchBuilder builder) {
        StringBuilder sql = new StringBuilder("SELECT b.* FROM building b ");
        Map<String, Object> parameters = new LinkedHashMap<>();

        if (builder.getStaffId() != null) {
            sql.append(" INNER JOIN assignmentbuilding ab ON b.id = ab.buildingid ");
        }

        sql.append(" WHERE 1=1 ");
        appendSimpleFilters(builder, sql, parameters);
        appendRangeAndAssignmentFilters(builder, sql, parameters);
        appendTypeFilters(builder.getTypeCode(), sql, parameters);
        sql.append(" GROUP BY b.id ORDER BY b.id DESC");

        Query query = entityManager.createNativeQuery(sql.toString(), BuildingEntity.class);
        for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
            query.setParameter(parameter.getKey(), parameter.getValue());
        }
        return query.getResultList();
    }

    @Override
    public List<BuildingEntity> findAll(BuildingSearchBuilder builder, Pageable pageable) {
        StringBuilder sql = new StringBuilder("SELECT b.* FROM building b ");
        Map<String, Object> parameters = new LinkedHashMap<>();

        if (builder.getStaffId() != null) {
            sql.append(" INNER JOIN assignmentbuilding ab ON b.id = ab.buildingid ");
        }

        sql.append(" WHERE 1=1 ");
        appendSimpleFilters(builder, sql, parameters);
        appendRangeAndAssignmentFilters(builder, sql, parameters);
        appendTypeFilters(builder.getTypeCode(), sql, parameters);
        sql.append(" GROUP BY b.id ORDER BY b.id DESC");

        Query query = entityManager.createNativeQuery(sql.toString(), BuildingEntity.class);
        for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
            query.setParameter(parameter.getKey(), parameter.getValue());
        }

        if (pageable != null) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        return query.getResultList();
    }

    @Override
    public int countTotal(BuildingSearchBuilder builder) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT b.id) FROM building b ");
        Map<String, Object> parameters = new LinkedHashMap<>();

        if (builder.getStaffId() != null) {
            sql.append(" INNER JOIN assignmentbuilding ab ON b.id = ab.buildingid ");
        }

        sql.append(" WHERE 1=1 ");
        appendSimpleFilters(builder, sql, parameters);
        appendRangeAndAssignmentFilters(builder, sql, parameters);
        appendTypeFilters(builder.getTypeCode(), sql, parameters);

        Query query = entityManager.createNativeQuery(sql.toString());
        for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
            query.setParameter(parameter.getKey(), parameter.getValue());
        }

        Object result = query.getSingleResult();
        if (result instanceof Number) {
            return ((Number) result).intValue();
        }
        return 0;
    }

    private void appendSimpleFilters(BuildingSearchBuilder builder, StringBuilder sql,
                                     Map<String, Object> parameters) {
        appendLikeFilter(sql, parameters, "name", "b.name", builder.getName());
        appendLikeFilter(sql, parameters, "street", "b.street", builder.getStreet());
        appendLikeFilter(sql, parameters, "district", "b.district", builder.getDistrict());
        appendLikeFilter(sql, parameters, "ward", "b.ward", builder.getWard());
        appendEqualsFilter(sql, parameters, "numberOfBasement", "b.numberofbasement",
                builder.getNumberOfBasement());
        appendLikeFilter(sql, parameters, "managerName", "b.managername", builder.getManagerName());
        appendLikeFilter(sql, parameters, "managerPhone", "b.managerphone", builder.getManagerPhone());
        appendEqualsFilter(sql, parameters, "floorArea", "b.floorarea", builder.getFloorArea());
        appendLikeFilter(sql, parameters, "direction", "b.direction", builder.getDirection());
        appendLikeFilter(sql, parameters, "level", "b.level", builder.getLevel());
    }

    private void appendRangeAndAssignmentFilters(BuildingSearchBuilder builder, StringBuilder sql,
                                                  Map<String, Object> parameters) {
        appendComparisonFilter(sql, parameters, "rentPriceFrom", "b.rentprice", ">=",
                builder.getRentPriceFrom());
        appendComparisonFilter(sql, parameters, "rentPriceTo", "b.rentprice", "<=",
                builder.getRentPriceTo());

        if (builder.getRentAreaFrom() != null || builder.getRentAreaTo() != null) {
            sql.append(" AND EXISTS (SELECT 1 FROM rentarea r WHERE r.buildingid = b.id ");
            appendComparisonFilter(sql, parameters, "rentAreaFrom", "r.value", ">=",
                    builder.getRentAreaFrom());
            appendComparisonFilter(sql, parameters, "rentAreaTo", "r.value", "<=",
                    builder.getRentAreaTo());
            sql.append(")");
        }

        if (builder.getStaffId() != null) {
            sql.append(" AND ab.staffid = :staffId");
            parameters.put("staffId", builder.getStaffId());
        }
    }

    private void appendTypeFilters(List<String> typeCodes, StringBuilder sql,
                                   Map<String, Object> parameters) {
        if (typeCodes == null || typeCodes.isEmpty()) {
            return;
        }

        sql.append(" AND (");
        for (int index = 0; index < typeCodes.size(); index++) {
            if (index > 0) {
                sql.append(" OR ");
            }
            String parameterName = "typeCode" + index;
            sql.append("b.type LIKE :").append(parameterName);
            parameters.put(parameterName, "%" + typeCodes.get(index) + "%");
        }
        sql.append(")");
    }

    private void appendLikeFilter(StringBuilder sql, Map<String, Object> parameters,
                                  String parameterName, String column, String value) {
        if (value != null) {
            sql.append(" AND ").append(column).append(" LIKE :").append(parameterName);
            parameters.put(parameterName, "%" + value + "%");
        }
    }

    private void appendEqualsFilter(StringBuilder sql, Map<String, Object> parameters,
                                    String parameterName, String column, Number value) {
        if (value != null) {
            sql.append(" AND ").append(column).append(" = :").append(parameterName);
            parameters.put(parameterName, value);
        }
    }

    private void appendComparisonFilter(StringBuilder sql, Map<String, Object> parameters,
                                        String parameterName, String column, String operator,
                                        Number value) {
        if (value != null) {
            sql.append(" AND ").append(column).append(" ").append(operator)
                    .append(" :").append(parameterName);
            parameters.put(parameterName, value);
        }
    }

}
