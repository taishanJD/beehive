package com.quarkdata.yunpan.api.model.dataobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IncUserConfigExample implements Serializable {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    protected Integer limitStart;

    protected Integer limitEnd;

    private static final long serialVersionUID = 1L;

    public IncUserConfigExample() {
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

        public Criteria andUserIdIsNull() {
            addCriterion("user_id is null");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNotNull() {
            addCriterion("user_id is not null");
            return (Criteria) this;
        }

        public Criteria andUserIdEqualTo(Long value) {
            addCriterion("user_id =", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotEqualTo(Long value) {
            addCriterion("user_id <>", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThan(Long value) {
            addCriterion("user_id >", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThanOrEqualTo(Long value) {
            addCriterion("user_id >=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThan(Long value) {
            addCriterion("user_id <", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThanOrEqualTo(Long value) {
            addCriterion("user_id <=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdIn(List<Long> values) {
            addCriterion("user_id in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotIn(List<Long> values) {
            addCriterion("user_id not in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdBetween(Long value1, Long value2) {
            addCriterion("user_id between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotBetween(Long value1, Long value2) {
            addCriterion("user_id not between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andIsReceiveMessageIsNull() {
            addCriterion("is_receive_message is null");
            return (Criteria) this;
        }

        public Criteria andIsReceiveMessageIsNotNull() {
            addCriterion("is_receive_message is not null");
            return (Criteria) this;
        }

        public Criteria andIsReceiveMessageEqualTo(String value) {
            addCriterion("is_receive_message =", value, "isReceiveMessage");
            return (Criteria) this;
        }

        public Criteria andIsReceiveMessageNotEqualTo(String value) {
            addCriterion("is_receive_message <>", value, "isReceiveMessage");
            return (Criteria) this;
        }

        public Criteria andIsReceiveMessageGreaterThan(String value) {
            addCriterion("is_receive_message >", value, "isReceiveMessage");
            return (Criteria) this;
        }

        public Criteria andIsReceiveMessageGreaterThanOrEqualTo(String value) {
            addCriterion("is_receive_message >=", value, "isReceiveMessage");
            return (Criteria) this;
        }

        public Criteria andIsReceiveMessageLessThan(String value) {
            addCriterion("is_receive_message <", value, "isReceiveMessage");
            return (Criteria) this;
        }

        public Criteria andIsReceiveMessageLessThanOrEqualTo(String value) {
            addCriterion("is_receive_message <=", value, "isReceiveMessage");
            return (Criteria) this;
        }

        public Criteria andIsReceiveMessageLike(String value) {
            addCriterion("is_receive_message like", value, "isReceiveMessage");
            return (Criteria) this;
        }

        public Criteria andIsReceiveMessageNotLike(String value) {
            addCriterion("is_receive_message not like", value, "isReceiveMessage");
            return (Criteria) this;
        }

        public Criteria andIsReceiveMessageIn(List<String> values) {
            addCriterion("is_receive_message in", values, "isReceiveMessage");
            return (Criteria) this;
        }

        public Criteria andIsReceiveMessageNotIn(List<String> values) {
            addCriterion("is_receive_message not in", values, "isReceiveMessage");
            return (Criteria) this;
        }

        public Criteria andIsReceiveMessageBetween(String value1, String value2) {
            addCriterion("is_receive_message between", value1, value2, "isReceiveMessage");
            return (Criteria) this;
        }

        public Criteria andIsReceiveMessageNotBetween(String value1, String value2) {
            addCriterion("is_receive_message not between", value1, value2, "isReceiveMessage");
            return (Criteria) this;
        }

        public Criteria andIsReceiveEmailMessageIsNull() {
            addCriterion("is_receive_email_message is null");
            return (Criteria) this;
        }

        public Criteria andIsReceiveEmailMessageIsNotNull() {
            addCriterion("is_receive_email_message is not null");
            return (Criteria) this;
        }

        public Criteria andIsReceiveEmailMessageEqualTo(String value) {
            addCriterion("is_receive_email_message =", value, "isReceiveEmailMessage");
            return (Criteria) this;
        }

        public Criteria andIsReceiveEmailMessageNotEqualTo(String value) {
            addCriterion("is_receive_email_message <>", value, "isReceiveEmailMessage");
            return (Criteria) this;
        }

        public Criteria andIsReceiveEmailMessageGreaterThan(String value) {
            addCriterion("is_receive_email_message >", value, "isReceiveEmailMessage");
            return (Criteria) this;
        }

        public Criteria andIsReceiveEmailMessageGreaterThanOrEqualTo(String value) {
            addCriterion("is_receive_email_message >=", value, "isReceiveEmailMessage");
            return (Criteria) this;
        }

        public Criteria andIsReceiveEmailMessageLessThan(String value) {
            addCriterion("is_receive_email_message <", value, "isReceiveEmailMessage");
            return (Criteria) this;
        }

        public Criteria andIsReceiveEmailMessageLessThanOrEqualTo(String value) {
            addCriterion("is_receive_email_message <=", value, "isReceiveEmailMessage");
            return (Criteria) this;
        }

        public Criteria andIsReceiveEmailMessageLike(String value) {
            addCriterion("is_receive_email_message like", value, "isReceiveEmailMessage");
            return (Criteria) this;
        }

        public Criteria andIsReceiveEmailMessageNotLike(String value) {
            addCriterion("is_receive_email_message not like", value, "isReceiveEmailMessage");
            return (Criteria) this;
        }

        public Criteria andIsReceiveEmailMessageIn(List<String> values) {
            addCriterion("is_receive_email_message in", values, "isReceiveEmailMessage");
            return (Criteria) this;
        }

        public Criteria andIsReceiveEmailMessageNotIn(List<String> values) {
            addCriterion("is_receive_email_message not in", values, "isReceiveEmailMessage");
            return (Criteria) this;
        }

        public Criteria andIsReceiveEmailMessageBetween(String value1, String value2) {
            addCriterion("is_receive_email_message between", value1, value2, "isReceiveEmailMessage");
            return (Criteria) this;
        }

        public Criteria andIsReceiveEmailMessageNotBetween(String value1, String value2) {
            addCriterion("is_receive_email_message not between", value1, value2, "isReceiveEmailMessage");
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