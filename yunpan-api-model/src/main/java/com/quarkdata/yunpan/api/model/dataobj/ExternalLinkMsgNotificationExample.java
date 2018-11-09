package com.quarkdata.yunpan.api.model.dataobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExternalLinkMsgNotificationExample implements Serializable {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    protected Integer limitStart;

    protected Integer limitEnd;

    private static final long serialVersionUID = 1L;

    public ExternalLinkMsgNotificationExample() {
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

        public Criteria andLinkIdIsNull() {
            addCriterion("link_id is null");
            return (Criteria) this;
        }

        public Criteria andLinkIdIsNotNull() {
            addCriterion("link_id is not null");
            return (Criteria) this;
        }

        public Criteria andLinkIdEqualTo(Long value) {
            addCriterion("link_id =", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdNotEqualTo(Long value) {
            addCriterion("link_id <>", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdGreaterThan(Long value) {
            addCriterion("link_id >", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdGreaterThanOrEqualTo(Long value) {
            addCriterion("link_id >=", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdLessThan(Long value) {
            addCriterion("link_id <", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdLessThanOrEqualTo(Long value) {
            addCriterion("link_id <=", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdIn(List<Long> values) {
            addCriterion("link_id in", values, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdNotIn(List<Long> values) {
            addCriterion("link_id not in", values, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdBetween(Long value1, Long value2) {
            addCriterion("link_id between", value1, value2, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdNotBetween(Long value1, Long value2) {
            addCriterion("link_id not between", value1, value2, "linkId");
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

        public Criteria andReceiverTypeIsNull() {
            addCriterion("receiver_type is null");
            return (Criteria) this;
        }

        public Criteria andReceiverTypeIsNotNull() {
            addCriterion("receiver_type is not null");
            return (Criteria) this;
        }

        public Criteria andReceiverTypeEqualTo(String value) {
            addCriterion("receiver_type =", value, "receiverType");
            return (Criteria) this;
        }

        public Criteria andReceiverTypeNotEqualTo(String value) {
            addCriterion("receiver_type <>", value, "receiverType");
            return (Criteria) this;
        }

        public Criteria andReceiverTypeGreaterThan(String value) {
            addCriterion("receiver_type >", value, "receiverType");
            return (Criteria) this;
        }

        public Criteria andReceiverTypeGreaterThanOrEqualTo(String value) {
            addCriterion("receiver_type >=", value, "receiverType");
            return (Criteria) this;
        }

        public Criteria andReceiverTypeLessThan(String value) {
            addCriterion("receiver_type <", value, "receiverType");
            return (Criteria) this;
        }

        public Criteria andReceiverTypeLessThanOrEqualTo(String value) {
            addCriterion("receiver_type <=", value, "receiverType");
            return (Criteria) this;
        }

        public Criteria andReceiverTypeLike(String value) {
            addCriterion("receiver_type like", value, "receiverType");
            return (Criteria) this;
        }

        public Criteria andReceiverTypeNotLike(String value) {
            addCriterion("receiver_type not like", value, "receiverType");
            return (Criteria) this;
        }

        public Criteria andReceiverTypeIn(List<String> values) {
            addCriterion("receiver_type in", values, "receiverType");
            return (Criteria) this;
        }

        public Criteria andReceiverTypeNotIn(List<String> values) {
            addCriterion("receiver_type not in", values, "receiverType");
            return (Criteria) this;
        }

        public Criteria andReceiverTypeBetween(String value1, String value2) {
            addCriterion("receiver_type between", value1, value2, "receiverType");
            return (Criteria) this;
        }

        public Criteria andReceiverTypeNotBetween(String value1, String value2) {
            addCriterion("receiver_type not between", value1, value2, "receiverType");
            return (Criteria) this;
        }

        public Criteria andReceiverDetailIsNull() {
            addCriterion("receiver_detail is null");
            return (Criteria) this;
        }

        public Criteria andReceiverDetailIsNotNull() {
            addCriterion("receiver_detail is not null");
            return (Criteria) this;
        }

        public Criteria andReceiverDetailEqualTo(String value) {
            addCriterion("receiver_detail =", value, "receiverDetail");
            return (Criteria) this;
        }

        public Criteria andReceiverDetailNotEqualTo(String value) {
            addCriterion("receiver_detail <>", value, "receiverDetail");
            return (Criteria) this;
        }

        public Criteria andReceiverDetailGreaterThan(String value) {
            addCriterion("receiver_detail >", value, "receiverDetail");
            return (Criteria) this;
        }

        public Criteria andReceiverDetailGreaterThanOrEqualTo(String value) {
            addCriterion("receiver_detail >=", value, "receiverDetail");
            return (Criteria) this;
        }

        public Criteria andReceiverDetailLessThan(String value) {
            addCriterion("receiver_detail <", value, "receiverDetail");
            return (Criteria) this;
        }

        public Criteria andReceiverDetailLessThanOrEqualTo(String value) {
            addCriterion("receiver_detail <=", value, "receiverDetail");
            return (Criteria) this;
        }

        public Criteria andReceiverDetailLike(String value) {
            addCriterion("receiver_detail like", value, "receiverDetail");
            return (Criteria) this;
        }

        public Criteria andReceiverDetailNotLike(String value) {
            addCriterion("receiver_detail not like", value, "receiverDetail");
            return (Criteria) this;
        }

        public Criteria andReceiverDetailIn(List<String> values) {
            addCriterion("receiver_detail in", values, "receiverDetail");
            return (Criteria) this;
        }

        public Criteria andReceiverDetailNotIn(List<String> values) {
            addCriterion("receiver_detail not in", values, "receiverDetail");
            return (Criteria) this;
        }

        public Criteria andReceiverDetailBetween(String value1, String value2) {
            addCriterion("receiver_detail between", value1, value2, "receiverDetail");
            return (Criteria) this;
        }

        public Criteria andReceiverDetailNotBetween(String value1, String value2) {
            addCriterion("receiver_detail not between", value1, value2, "receiverDetail");
            return (Criteria) this;
        }

        public Criteria andIsSentIsNull() {
            addCriterion("is_sent is null");
            return (Criteria) this;
        }

        public Criteria andIsSentIsNotNull() {
            addCriterion("is_sent is not null");
            return (Criteria) this;
        }

        public Criteria andIsSentEqualTo(String value) {
            addCriterion("is_sent =", value, "isSent");
            return (Criteria) this;
        }

        public Criteria andIsSentNotEqualTo(String value) {
            addCriterion("is_sent <>", value, "isSent");
            return (Criteria) this;
        }

        public Criteria andIsSentGreaterThan(String value) {
            addCriterion("is_sent >", value, "isSent");
            return (Criteria) this;
        }

        public Criteria andIsSentGreaterThanOrEqualTo(String value) {
            addCriterion("is_sent >=", value, "isSent");
            return (Criteria) this;
        }

        public Criteria andIsSentLessThan(String value) {
            addCriterion("is_sent <", value, "isSent");
            return (Criteria) this;
        }

        public Criteria andIsSentLessThanOrEqualTo(String value) {
            addCriterion("is_sent <=", value, "isSent");
            return (Criteria) this;
        }

        public Criteria andIsSentLike(String value) {
            addCriterion("is_sent like", value, "isSent");
            return (Criteria) this;
        }

        public Criteria andIsSentNotLike(String value) {
            addCriterion("is_sent not like", value, "isSent");
            return (Criteria) this;
        }

        public Criteria andIsSentIn(List<String> values) {
            addCriterion("is_sent in", values, "isSent");
            return (Criteria) this;
        }

        public Criteria andIsSentNotIn(List<String> values) {
            addCriterion("is_sent not in", values, "isSent");
            return (Criteria) this;
        }

        public Criteria andIsSentBetween(String value1, String value2) {
            addCriterion("is_sent between", value1, value2, "isSent");
            return (Criteria) this;
        }

        public Criteria andIsSentNotBetween(String value1, String value2) {
            addCriterion("is_sent not between", value1, value2, "isSent");
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