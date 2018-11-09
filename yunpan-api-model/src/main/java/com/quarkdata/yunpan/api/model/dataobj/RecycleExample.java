package com.quarkdata.yunpan.api.model.dataobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecycleExample implements Serializable {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    protected Integer limitStart;

    protected Integer limitEnd;

    private static final long serialVersionUID = 1L;

    public RecycleExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    public void setLimitStart(Integer limitStart) {
        this.limitStart=limitStart;
    }

    public Integer getLimitStart() {
        return limitStart;
    }

    public void setLimitEnd(Integer limitEnd) {
        this.limitEnd=limitEnd;
    }

    public Integer getLimitEnd() {
        return limitEnd;
    }

    protected abstract static class GeneratedCriteria implements Serializable {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIncIdIsNull() {
            addCriterion("inc_id is null");
            return (Criteria) this;
        }

        public Criteria andIncIdIsNotNull() {
            addCriterion("inc_id is not null");
            return (Criteria) this;
        }

        public Criteria andIncIdEqualTo(Long value) {
            addCriterion("inc_id =", value, "incId");
            return (Criteria) this;
        }

        public Criteria andIncIdNotEqualTo(Long value) {
            addCriterion("inc_id <>", value, "incId");
            return (Criteria) this;
        }

        public Criteria andIncIdGreaterThan(Long value) {
            addCriterion("inc_id >", value, "incId");
            return (Criteria) this;
        }

        public Criteria andIncIdGreaterThanOrEqualTo(Long value) {
            addCriterion("inc_id >=", value, "incId");
            return (Criteria) this;
        }

        public Criteria andIncIdLessThan(Long value) {
            addCriterion("inc_id <", value, "incId");
            return (Criteria) this;
        }

        public Criteria andIncIdLessThanOrEqualTo(Long value) {
            addCriterion("inc_id <=", value, "incId");
            return (Criteria) this;
        }

        public Criteria andIncIdIn(List<Long> values) {
            addCriterion("inc_id in", values, "incId");
            return (Criteria) this;
        }

        public Criteria andIncIdNotIn(List<Long> values) {
            addCriterion("inc_id not in", values, "incId");
            return (Criteria) this;
        }

        public Criteria andIncIdBetween(Long value1, Long value2) {
            addCriterion("inc_id between", value1, value2, "incId");
            return (Criteria) this;
        }

        public Criteria andIncIdNotBetween(Long value1, Long value2) {
            addCriterion("inc_id not between", value1, value2, "incId");
            return (Criteria) this;
        }

        public Criteria andDocumentIdIsNull() {
            addCriterion("document_id is null");
            return (Criteria) this;
        }

        public Criteria andDocumentIdIsNotNull() {
            addCriterion("document_id is not null");
            return (Criteria) this;
        }

        public Criteria andDocumentIdEqualTo(Long value) {
            addCriterion("document_id =", value, "documentId");
            return (Criteria) this;
        }

        public Criteria andDocumentIdNotEqualTo(Long value) {
            addCriterion("document_id <>", value, "documentId");
            return (Criteria) this;
        }

        public Criteria andDocumentIdGreaterThan(Long value) {
            addCriterion("document_id >", value, "documentId");
            return (Criteria) this;
        }

        public Criteria andDocumentIdGreaterThanOrEqualTo(Long value) {
            addCriterion("document_id >=", value, "documentId");
            return (Criteria) this;
        }

        public Criteria andDocumentIdLessThan(Long value) {
            addCriterion("document_id <", value, "documentId");
            return (Criteria) this;
        }

        public Criteria andDocumentIdLessThanOrEqualTo(Long value) {
            addCriterion("document_id <=", value, "documentId");
            return (Criteria) this;
        }

        public Criteria andDocumentIdIn(List<Long> values) {
            addCriterion("document_id in", values, "documentId");
            return (Criteria) this;
        }

        public Criteria andDocumentIdNotIn(List<Long> values) {
            addCriterion("document_id not in", values, "documentId");
            return (Criteria) this;
        }

        public Criteria andDocumentIdBetween(Long value1, Long value2) {
            addCriterion("document_id between", value1, value2, "documentId");
            return (Criteria) this;
        }

        public Criteria andDocumentIdNotBetween(Long value1, Long value2) {
            addCriterion("document_id not between", value1, value2, "documentId");
            return (Criteria) this;
        }

        public Criteria andDocumentVersionIdIsNull() {
            addCriterion("document_version_id is null");
            return (Criteria) this;
        }

        public Criteria andDocumentVersionIdIsNotNull() {
            addCriterion("document_version_id is not null");
            return (Criteria) this;
        }

        public Criteria andDocumentVersionIdEqualTo(Long value) {
            addCriterion("document_version_id =", value, "documentVersionId");
            return (Criteria) this;
        }

        public Criteria andDocumentVersionIdNotEqualTo(Long value) {
            addCriterion("document_version_id <>", value, "documentVersionId");
            return (Criteria) this;
        }

        public Criteria andDocumentVersionIdGreaterThan(Long value) {
            addCriterion("document_version_id >", value, "documentVersionId");
            return (Criteria) this;
        }

        public Criteria andDocumentVersionIdGreaterThanOrEqualTo(Long value) {
            addCriterion("document_version_id >=", value, "documentVersionId");
            return (Criteria) this;
        }

        public Criteria andDocumentVersionIdLessThan(Long value) {
            addCriterion("document_version_id <", value, "documentVersionId");
            return (Criteria) this;
        }

        public Criteria andDocumentVersionIdLessThanOrEqualTo(Long value) {
            addCriterion("document_version_id <=", value, "documentVersionId");
            return (Criteria) this;
        }

        public Criteria andDocumentVersionIdIn(List<Long> values) {
            addCriterion("document_version_id in", values, "documentVersionId");
            return (Criteria) this;
        }

        public Criteria andDocumentVersionIdNotIn(List<Long> values) {
            addCriterion("document_version_id not in", values, "documentVersionId");
            return (Criteria) this;
        }

        public Criteria andDocumentVersionIdBetween(Long value1, Long value2) {
            addCriterion("document_version_id between", value1, value2, "documentVersionId");
            return (Criteria) this;
        }

        public Criteria andDocumentVersionIdNotBetween(Long value1, Long value2) {
            addCriterion("document_version_id not between", value1, value2, "documentVersionId");
            return (Criteria) this;
        }

        public Criteria andDocumentParentIdIsNull() {
            addCriterion("document_parent_id is null");
            return (Criteria) this;
        }

        public Criteria andDocumentParentIdIsNotNull() {
            addCriterion("document_parent_id is not null");
            return (Criteria) this;
        }

        public Criteria andDocumentParentIdEqualTo(Long value) {
            addCriterion("document_parent_id =", value, "documentParentId");
            return (Criteria) this;
        }

        public Criteria andDocumentParentIdNotEqualTo(Long value) {
            addCriterion("document_parent_id <>", value, "documentParentId");
            return (Criteria) this;
        }

        public Criteria andDocumentParentIdGreaterThan(Long value) {
            addCriterion("document_parent_id >", value, "documentParentId");
            return (Criteria) this;
        }

        public Criteria andDocumentParentIdGreaterThanOrEqualTo(Long value) {
            addCriterion("document_parent_id >=", value, "documentParentId");
            return (Criteria) this;
        }

        public Criteria andDocumentParentIdLessThan(Long value) {
            addCriterion("document_parent_id <", value, "documentParentId");
            return (Criteria) this;
        }

        public Criteria andDocumentParentIdLessThanOrEqualTo(Long value) {
            addCriterion("document_parent_id <=", value, "documentParentId");
            return (Criteria) this;
        }

        public Criteria andDocumentParentIdIn(List<Long> values) {
            addCriterion("document_parent_id in", values, "documentParentId");
            return (Criteria) this;
        }

        public Criteria andDocumentParentIdNotIn(List<Long> values) {
            addCriterion("document_parent_id not in", values, "documentParentId");
            return (Criteria) this;
        }

        public Criteria andDocumentParentIdBetween(Long value1, Long value2) {
            addCriterion("document_parent_id between", value1, value2, "documentParentId");
            return (Criteria) this;
        }

        public Criteria andDocumentParentIdNotBetween(Long value1, Long value2) {
            addCriterion("document_parent_id not between", value1, value2, "documentParentId");
            return (Criteria) this;
        }

        public Criteria andDocumentIdPathIsNull() {
            addCriterion("document_id_path is null");
            return (Criteria) this;
        }

        public Criteria andDocumentIdPathIsNotNull() {
            addCriterion("document_id_path is not null");
            return (Criteria) this;
        }

        public Criteria andDocumentIdPathEqualTo(String value) {
            addCriterion("document_id_path =", value, "documentIdPath");
            return (Criteria) this;
        }

        public Criteria andDocumentIdPathNotEqualTo(String value) {
            addCriterion("document_id_path <>", value, "documentIdPath");
            return (Criteria) this;
        }

        public Criteria andDocumentIdPathGreaterThan(String value) {
            addCriterion("document_id_path >", value, "documentIdPath");
            return (Criteria) this;
        }

        public Criteria andDocumentIdPathGreaterThanOrEqualTo(String value) {
            addCriterion("document_id_path >=", value, "documentIdPath");
            return (Criteria) this;
        }

        public Criteria andDocumentIdPathLessThan(String value) {
            addCriterion("document_id_path <", value, "documentIdPath");
            return (Criteria) this;
        }

        public Criteria andDocumentIdPathLessThanOrEqualTo(String value) {
            addCriterion("document_id_path <=", value, "documentIdPath");
            return (Criteria) this;
        }

        public Criteria andDocumentIdPathLike(String value) {
            addCriterion("document_id_path like", value, "documentIdPath");
            return (Criteria) this;
        }

        public Criteria andDocumentIdPathNotLike(String value) {
            addCriterion("document_id_path not like", value, "documentIdPath");
            return (Criteria) this;
        }

        public Criteria andDocumentIdPathIn(List<String> values) {
            addCriterion("document_id_path in", values, "documentIdPath");
            return (Criteria) this;
        }

        public Criteria andDocumentIdPathNotIn(List<String> values) {
            addCriterion("document_id_path not in", values, "documentIdPath");
            return (Criteria) this;
        }

        public Criteria andDocumentIdPathBetween(String value1, String value2) {
            addCriterion("document_id_path between", value1, value2, "documentIdPath");
            return (Criteria) this;
        }

        public Criteria andDocumentIdPathNotBetween(String value1, String value2) {
            addCriterion("document_id_path not between", value1, value2, "documentIdPath");
            return (Criteria) this;
        }

        public Criteria andCreateUserIsNull() {
            addCriterion("create_user is null");
            return (Criteria) this;
        }

        public Criteria andCreateUserIsNotNull() {
            addCriterion("create_user is not null");
            return (Criteria) this;
        }

        public Criteria andCreateUserEqualTo(Long value) {
            addCriterion("create_user =", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotEqualTo(Long value) {
            addCriterion("create_user <>", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserGreaterThan(Long value) {
            addCriterion("create_user >", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserGreaterThanOrEqualTo(Long value) {
            addCriterion("create_user >=", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserLessThan(Long value) {
            addCriterion("create_user <", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserLessThanOrEqualTo(Long value) {
            addCriterion("create_user <=", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserIn(List<Long> values) {
            addCriterion("create_user in", values, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotIn(List<Long> values) {
            addCriterion("create_user not in", values, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserBetween(Long value1, Long value2) {
            addCriterion("create_user between", value1, value2, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotBetween(Long value1, Long value2) {
            addCriterion("create_user not between", value1, value2, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andIsDeleteIsNull() {
            addCriterion("is_delete is null");
            return (Criteria) this;
        }

        public Criteria andIsDeleteIsNotNull() {
            addCriterion("is_delete is not null");
            return (Criteria) this;
        }

        public Criteria andIsDeleteEqualTo(String value) {
            addCriterion("is_delete =", value, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteNotEqualTo(String value) {
            addCriterion("is_delete <>", value, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteGreaterThan(String value) {
            addCriterion("is_delete >", value, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteGreaterThanOrEqualTo(String value) {
            addCriterion("is_delete >=", value, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteLessThan(String value) {
            addCriterion("is_delete <", value, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteLessThanOrEqualTo(String value) {
            addCriterion("is_delete <=", value, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteLike(String value) {
            addCriterion("is_delete like", value, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteNotLike(String value) {
            addCriterion("is_delete not like", value, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteIn(List<String> values) {
            addCriterion("is_delete in", values, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteNotIn(List<String> values) {
            addCriterion("is_delete not in", values, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteBetween(String value1, String value2) {
            addCriterion("is_delete between", value1, value2, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteNotBetween(String value1, String value2) {
            addCriterion("is_delete not between", value1, value2, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsVisiableIsNull() {
            addCriterion("is_visiable is null");
            return (Criteria) this;
        }

        public Criteria andIsVisiableIsNotNull() {
            addCriterion("is_visiable is not null");
            return (Criteria) this;
        }

        public Criteria andIsVisiableEqualTo(String value) {
            addCriterion("is_visiable =", value, "isVisiable");
            return (Criteria) this;
        }

        public Criteria andIsVisiableNotEqualTo(String value) {
            addCriterion("is_visiable <>", value, "isVisiable");
            return (Criteria) this;
        }

        public Criteria andIsVisiableGreaterThan(String value) {
            addCriterion("is_visiable >", value, "isVisiable");
            return (Criteria) this;
        }

        public Criteria andIsVisiableGreaterThanOrEqualTo(String value) {
            addCriterion("is_visiable >=", value, "isVisiable");
            return (Criteria) this;
        }

        public Criteria andIsVisiableLessThan(String value) {
            addCriterion("is_visiable <", value, "isVisiable");
            return (Criteria) this;
        }

        public Criteria andIsVisiableLessThanOrEqualTo(String value) {
            addCriterion("is_visiable <=", value, "isVisiable");
            return (Criteria) this;
        }

        public Criteria andIsVisiableLike(String value) {
            addCriterion("is_visiable like", value, "isVisiable");
            return (Criteria) this;
        }

        public Criteria andIsVisiableNotLike(String value) {
            addCriterion("is_visiable not like", value, "isVisiable");
            return (Criteria) this;
        }

        public Criteria andIsVisiableIn(List<String> values) {
            addCriterion("is_visiable in", values, "isVisiable");
            return (Criteria) this;
        }

        public Criteria andIsVisiableNotIn(List<String> values) {
            addCriterion("is_visiable not in", values, "isVisiable");
            return (Criteria) this;
        }

        public Criteria andIsVisiableBetween(String value1, String value2) {
            addCriterion("is_visiable between", value1, value2, "isVisiable");
            return (Criteria) this;
        }

        public Criteria andIsVisiableNotBetween(String value1, String value2) {
            addCriterion("is_visiable not between", value1, value2, "isVisiable");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria implements Serializable {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion implements Serializable {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}