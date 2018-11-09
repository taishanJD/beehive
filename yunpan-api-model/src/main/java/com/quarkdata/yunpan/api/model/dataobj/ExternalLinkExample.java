package com.quarkdata.yunpan.api.model.dataobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExternalLinkExample implements Serializable {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    protected Integer limitStart;

    protected Integer limitEnd;

    private static final long serialVersionUID = 1L;

    public ExternalLinkExample() {
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

        public Criteria andIsEnableExternalLinkIsNull() {
            addCriterion("is_enable_external_link is null");
            return (Criteria) this;
        }

        public Criteria andIsEnableExternalLinkIsNotNull() {
            addCriterion("is_enable_external_link is not null");
            return (Criteria) this;
        }

        public Criteria andIsEnableExternalLinkEqualTo(String value) {
            addCriterion("is_enable_external_link =", value, "isEnableExternalLink");
            return (Criteria) this;
        }

        public Criteria andIsEnableExternalLinkNotEqualTo(String value) {
            addCriterion("is_enable_external_link <>", value, "isEnableExternalLink");
            return (Criteria) this;
        }

        public Criteria andIsEnableExternalLinkGreaterThan(String value) {
            addCriterion("is_enable_external_link >", value, "isEnableExternalLink");
            return (Criteria) this;
        }

        public Criteria andIsEnableExternalLinkGreaterThanOrEqualTo(String value) {
            addCriterion("is_enable_external_link >=", value, "isEnableExternalLink");
            return (Criteria) this;
        }

        public Criteria andIsEnableExternalLinkLessThan(String value) {
            addCriterion("is_enable_external_link <", value, "isEnableExternalLink");
            return (Criteria) this;
        }

        public Criteria andIsEnableExternalLinkLessThanOrEqualTo(String value) {
            addCriterion("is_enable_external_link <=", value, "isEnableExternalLink");
            return (Criteria) this;
        }

        public Criteria andIsEnableExternalLinkLike(String value) {
            addCriterion("is_enable_external_link like", value, "isEnableExternalLink");
            return (Criteria) this;
        }

        public Criteria andIsEnableExternalLinkNotLike(String value) {
            addCriterion("is_enable_external_link not like", value, "isEnableExternalLink");
            return (Criteria) this;
        }

        public Criteria andIsEnableExternalLinkIn(List<String> values) {
            addCriterion("is_enable_external_link in", values, "isEnableExternalLink");
            return (Criteria) this;
        }

        public Criteria andIsEnableExternalLinkNotIn(List<String> values) {
            addCriterion("is_enable_external_link not in", values, "isEnableExternalLink");
            return (Criteria) this;
        }

        public Criteria andIsEnableExternalLinkBetween(String value1, String value2) {
            addCriterion("is_enable_external_link between", value1, value2, "isEnableExternalLink");
            return (Criteria) this;
        }

        public Criteria andIsEnableExternalLinkNotBetween(String value1, String value2) {
            addCriterion("is_enable_external_link not between", value1, value2, "isEnableExternalLink");
            return (Criteria) this;
        }

        public Criteria andExternalLinkTypeIsNull() {
            addCriterion("external_link_type is null");
            return (Criteria) this;
        }

        public Criteria andExternalLinkTypeIsNotNull() {
            addCriterion("external_link_type is not null");
            return (Criteria) this;
        }

        public Criteria andExternalLinkTypeEqualTo(String value) {
            addCriterion("external_link_type =", value, "externalLinkType");
            return (Criteria) this;
        }

        public Criteria andExternalLinkTypeNotEqualTo(String value) {
            addCriterion("external_link_type <>", value, "externalLinkType");
            return (Criteria) this;
        }

        public Criteria andExternalLinkTypeGreaterThan(String value) {
            addCriterion("external_link_type >", value, "externalLinkType");
            return (Criteria) this;
        }

        public Criteria andExternalLinkTypeGreaterThanOrEqualTo(String value) {
            addCriterion("external_link_type >=", value, "externalLinkType");
            return (Criteria) this;
        }

        public Criteria andExternalLinkTypeLessThan(String value) {
            addCriterion("external_link_type <", value, "externalLinkType");
            return (Criteria) this;
        }

        public Criteria andExternalLinkTypeLessThanOrEqualTo(String value) {
            addCriterion("external_link_type <=", value, "externalLinkType");
            return (Criteria) this;
        }

        public Criteria andExternalLinkTypeLike(String value) {
            addCriterion("external_link_type like", value, "externalLinkType");
            return (Criteria) this;
        }

        public Criteria andExternalLinkTypeNotLike(String value) {
            addCriterion("external_link_type not like", value, "externalLinkType");
            return (Criteria) this;
        }

        public Criteria andExternalLinkTypeIn(List<String> values) {
            addCriterion("external_link_type in", values, "externalLinkType");
            return (Criteria) this;
        }

        public Criteria andExternalLinkTypeNotIn(List<String> values) {
            addCriterion("external_link_type not in", values, "externalLinkType");
            return (Criteria) this;
        }

        public Criteria andExternalLinkTypeBetween(String value1, String value2) {
            addCriterion("external_link_type between", value1, value2, "externalLinkType");
            return (Criteria) this;
        }

        public Criteria andExternalLinkTypeNotBetween(String value1, String value2) {
            addCriterion("external_link_type not between", value1, value2, "externalLinkType");
            return (Criteria) this;
        }

        public Criteria andExternalLinkCodeIsNull() {
            addCriterion("external_link_code is null");
            return (Criteria) this;
        }

        public Criteria andExternalLinkCodeIsNotNull() {
            addCriterion("external_link_code is not null");
            return (Criteria) this;
        }

        public Criteria andExternalLinkCodeEqualTo(String value) {
            addCriterion("external_link_code =", value, "externalLinkCode");
            return (Criteria) this;
        }

        public Criteria andExternalLinkCodeNotEqualTo(String value) {
            addCriterion("external_link_code <>", value, "externalLinkCode");
            return (Criteria) this;
        }

        public Criteria andExternalLinkCodeGreaterThan(String value) {
            addCriterion("external_link_code >", value, "externalLinkCode");
            return (Criteria) this;
        }

        public Criteria andExternalLinkCodeGreaterThanOrEqualTo(String value) {
            addCriterion("external_link_code >=", value, "externalLinkCode");
            return (Criteria) this;
        }

        public Criteria andExternalLinkCodeLessThan(String value) {
            addCriterion("external_link_code <", value, "externalLinkCode");
            return (Criteria) this;
        }

        public Criteria andExternalLinkCodeLessThanOrEqualTo(String value) {
            addCriterion("external_link_code <=", value, "externalLinkCode");
            return (Criteria) this;
        }

        public Criteria andExternalLinkCodeLike(String value) {
            addCriterion("external_link_code like", value, "externalLinkCode");
            return (Criteria) this;
        }

        public Criteria andExternalLinkCodeNotLike(String value) {
            addCriterion("external_link_code not like", value, "externalLinkCode");
            return (Criteria) this;
        }

        public Criteria andExternalLinkCodeIn(List<String> values) {
            addCriterion("external_link_code in", values, "externalLinkCode");
            return (Criteria) this;
        }

        public Criteria andExternalLinkCodeNotIn(List<String> values) {
            addCriterion("external_link_code not in", values, "externalLinkCode");
            return (Criteria) this;
        }

        public Criteria andExternalLinkCodeBetween(String value1, String value2) {
            addCriterion("external_link_code between", value1, value2, "externalLinkCode");
            return (Criteria) this;
        }

        public Criteria andExternalLinkCodeNotBetween(String value1, String value2) {
            addCriterion("external_link_code not between", value1, value2, "externalLinkCode");
            return (Criteria) this;
        }

        public Criteria andExternalLinkExpireTimeIsNull() {
            addCriterion("external_link_expire_time is null");
            return (Criteria) this;
        }

        public Criteria andExternalLinkExpireTimeIsNotNull() {
            addCriterion("external_link_expire_time is not null");
            return (Criteria) this;
        }

        public Criteria andExternalLinkExpireTimeEqualTo(Date value) {
            addCriterion("external_link_expire_time =", value, "externalLinkExpireTime");
            return (Criteria) this;
        }

        public Criteria andExternalLinkExpireTimeNotEqualTo(Date value) {
            addCriterion("external_link_expire_time <>", value, "externalLinkExpireTime");
            return (Criteria) this;
        }

        public Criteria andExternalLinkExpireTimeGreaterThan(Date value) {
            addCriterion("external_link_expire_time >", value, "externalLinkExpireTime");
            return (Criteria) this;
        }

        public Criteria andExternalLinkExpireTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("external_link_expire_time >=", value, "externalLinkExpireTime");
            return (Criteria) this;
        }

        public Criteria andExternalLinkExpireTimeLessThan(Date value) {
            addCriterion("external_link_expire_time <", value, "externalLinkExpireTime");
            return (Criteria) this;
        }

        public Criteria andExternalLinkExpireTimeLessThanOrEqualTo(Date value) {
            addCriterion("external_link_expire_time <=", value, "externalLinkExpireTime");
            return (Criteria) this;
        }

        public Criteria andExternalLinkExpireTimeIn(List<Date> values) {
            addCriterion("external_link_expire_time in", values, "externalLinkExpireTime");
            return (Criteria) this;
        }

        public Criteria andExternalLinkExpireTimeNotIn(List<Date> values) {
            addCriterion("external_link_expire_time not in", values, "externalLinkExpireTime");
            return (Criteria) this;
        }

        public Criteria andExternalLinkExpireTimeBetween(Date value1, Date value2) {
            addCriterion("external_link_expire_time between", value1, value2, "externalLinkExpireTime");
            return (Criteria) this;
        }

        public Criteria andExternalLinkExpireTimeNotBetween(Date value1, Date value2) {
            addCriterion("external_link_expire_time not between", value1, value2, "externalLinkExpireTime");
            return (Criteria) this;
        }

        public Criteria andViewCountIsNull() {
            addCriterion("view_count is null");
            return (Criteria) this;
        }

        public Criteria andViewCountIsNotNull() {
            addCriterion("view_count is not null");
            return (Criteria) this;
        }

        public Criteria andViewCountEqualTo(Integer value) {
            addCriterion("view_count =", value, "viewCount");
            return (Criteria) this;
        }

        public Criteria andViewCountNotEqualTo(Integer value) {
            addCriterion("view_count <>", value, "viewCount");
            return (Criteria) this;
        }

        public Criteria andViewCountGreaterThan(Integer value) {
            addCriterion("view_count >", value, "viewCount");
            return (Criteria) this;
        }

        public Criteria andViewCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("view_count >=", value, "viewCount");
            return (Criteria) this;
        }

        public Criteria andViewCountLessThan(Integer value) {
            addCriterion("view_count <", value, "viewCount");
            return (Criteria) this;
        }

        public Criteria andViewCountLessThanOrEqualTo(Integer value) {
            addCriterion("view_count <=", value, "viewCount");
            return (Criteria) this;
        }

        public Criteria andViewCountIn(List<Integer> values) {
            addCriterion("view_count in", values, "viewCount");
            return (Criteria) this;
        }

        public Criteria andViewCountNotIn(List<Integer> values) {
            addCriterion("view_count not in", values, "viewCount");
            return (Criteria) this;
        }

        public Criteria andViewCountBetween(Integer value1, Integer value2) {
            addCriterion("view_count between", value1, value2, "viewCount");
            return (Criteria) this;
        }

        public Criteria andViewCountNotBetween(Integer value1, Integer value2) {
            addCriterion("view_count not between", value1, value2, "viewCount");
            return (Criteria) this;
        }

        public Criteria andDownloadCountIsNull() {
            addCriterion("download_count is null");
            return (Criteria) this;
        }

        public Criteria andDownloadCountIsNotNull() {
            addCriterion("download_count is not null");
            return (Criteria) this;
        }

        public Criteria andDownloadCountEqualTo(Integer value) {
            addCriterion("download_count =", value, "downloadCount");
            return (Criteria) this;
        }

        public Criteria andDownloadCountNotEqualTo(Integer value) {
            addCriterion("download_count <>", value, "downloadCount");
            return (Criteria) this;
        }

        public Criteria andDownloadCountGreaterThan(Integer value) {
            addCriterion("download_count >", value, "downloadCount");
            return (Criteria) this;
        }

        public Criteria andDownloadCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("download_count >=", value, "downloadCount");
            return (Criteria) this;
        }

        public Criteria andDownloadCountLessThan(Integer value) {
            addCriterion("download_count <", value, "downloadCount");
            return (Criteria) this;
        }

        public Criteria andDownloadCountLessThanOrEqualTo(Integer value) {
            addCriterion("download_count <=", value, "downloadCount");
            return (Criteria) this;
        }

        public Criteria andDownloadCountIn(List<Integer> values) {
            addCriterion("download_count in", values, "downloadCount");
            return (Criteria) this;
        }

        public Criteria andDownloadCountNotIn(List<Integer> values) {
            addCriterion("download_count not in", values, "downloadCount");
            return (Criteria) this;
        }

        public Criteria andDownloadCountBetween(Integer value1, Integer value2) {
            addCriterion("download_count between", value1, value2, "downloadCount");
            return (Criteria) this;
        }

        public Criteria andDownloadCountNotBetween(Integer value1, Integer value2) {
            addCriterion("download_count not between", value1, value2, "downloadCount");
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

        public Criteria andUpdateUserIsNull() {
            addCriterion("update_user is null");
            return (Criteria) this;
        }

        public Criteria andUpdateUserIsNotNull() {
            addCriterion("update_user is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateUserEqualTo(Long value) {
            addCriterion("update_user =", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserNotEqualTo(Long value) {
            addCriterion("update_user <>", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserGreaterThan(Long value) {
            addCriterion("update_user >", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserGreaterThanOrEqualTo(Long value) {
            addCriterion("update_user >=", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserLessThan(Long value) {
            addCriterion("update_user <", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserLessThanOrEqualTo(Long value) {
            addCriterion("update_user <=", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserIn(List<Long> values) {
            addCriterion("update_user in", values, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserNotIn(List<Long> values) {
            addCriterion("update_user not in", values, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserBetween(Long value1, Long value2) {
            addCriterion("update_user between", value1, value2, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserNotBetween(Long value1, Long value2) {
            addCriterion("update_user not between", value1, value2, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("update_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("update_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("update_time not between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andCreateUsernameIsNull() {
            addCriterion("create_username is null");
            return (Criteria) this;
        }

        public Criteria andCreateUsernameIsNotNull() {
            addCriterion("create_username is not null");
            return (Criteria) this;
        }

        public Criteria andCreateUsernameEqualTo(String value) {
            addCriterion("create_username =", value, "createUsername");
            return (Criteria) this;
        }

        public Criteria andCreateUsernameNotEqualTo(String value) {
            addCriterion("create_username <>", value, "createUsername");
            return (Criteria) this;
        }

        public Criteria andCreateUsernameGreaterThan(String value) {
            addCriterion("create_username >", value, "createUsername");
            return (Criteria) this;
        }

        public Criteria andCreateUsernameGreaterThanOrEqualTo(String value) {
            addCriterion("create_username >=", value, "createUsername");
            return (Criteria) this;
        }

        public Criteria andCreateUsernameLessThan(String value) {
            addCriterion("create_username <", value, "createUsername");
            return (Criteria) this;
        }

        public Criteria andCreateUsernameLessThanOrEqualTo(String value) {
            addCriterion("create_username <=", value, "createUsername");
            return (Criteria) this;
        }

        public Criteria andCreateUsernameLike(String value) {
            addCriterion("create_username like", value, "createUsername");
            return (Criteria) this;
        }

        public Criteria andCreateUsernameNotLike(String value) {
            addCriterion("create_username not like", value, "createUsername");
            return (Criteria) this;
        }

        public Criteria andCreateUsernameIn(List<String> values) {
            addCriterion("create_username in", values, "createUsername");
            return (Criteria) this;
        }

        public Criteria andCreateUsernameNotIn(List<String> values) {
            addCriterion("create_username not in", values, "createUsername");
            return (Criteria) this;
        }

        public Criteria andCreateUsernameBetween(String value1, String value2) {
            addCriterion("create_username between", value1, value2, "createUsername");
            return (Criteria) this;
        }

        public Criteria andCreateUsernameNotBetween(String value1, String value2) {
            addCriterion("create_username not between", value1, value2, "createUsername");
            return (Criteria) this;
        }

        public Criteria andAllowPreviewIsNull() {
            addCriterion("allow_preview is null");
            return (Criteria) this;
        }

        public Criteria andAllowPreviewIsNotNull() {
            addCriterion("allow_preview is not null");
            return (Criteria) this;
        }

        public Criteria andAllowPreviewEqualTo(String value) {
            addCriterion("allow_preview =", value, "allowPreview");
            return (Criteria) this;
        }

        public Criteria andAllowPreviewNotEqualTo(String value) {
            addCriterion("allow_preview <>", value, "allowPreview");
            return (Criteria) this;
        }

        public Criteria andAllowPreviewGreaterThan(String value) {
            addCriterion("allow_preview >", value, "allowPreview");
            return (Criteria) this;
        }

        public Criteria andAllowPreviewGreaterThanOrEqualTo(String value) {
            addCriterion("allow_preview >=", value, "allowPreview");
            return (Criteria) this;
        }

        public Criteria andAllowPreviewLessThan(String value) {
            addCriterion("allow_preview <", value, "allowPreview");
            return (Criteria) this;
        }

        public Criteria andAllowPreviewLessThanOrEqualTo(String value) {
            addCriterion("allow_preview <=", value, "allowPreview");
            return (Criteria) this;
        }

        public Criteria andAllowPreviewLike(String value) {
            addCriterion("allow_preview like", value, "allowPreview");
            return (Criteria) this;
        }

        public Criteria andAllowPreviewNotLike(String value) {
            addCriterion("allow_preview not like", value, "allowPreview");
            return (Criteria) this;
        }

        public Criteria andAllowPreviewIn(List<String> values) {
            addCriterion("allow_preview in", values, "allowPreview");
            return (Criteria) this;
        }

        public Criteria andAllowPreviewNotIn(List<String> values) {
            addCriterion("allow_preview not in", values, "allowPreview");
            return (Criteria) this;
        }

        public Criteria andAllowPreviewBetween(String value1, String value2) {
            addCriterion("allow_preview between", value1, value2, "allowPreview");
            return (Criteria) this;
        }

        public Criteria andAllowPreviewNotBetween(String value1, String value2) {
            addCriterion("allow_preview not between", value1, value2, "allowPreview");
            return (Criteria) this;
        }

        public Criteria andAllowDownloadIsNull() {
            addCriterion("allow_download is null");
            return (Criteria) this;
        }

        public Criteria andAllowDownloadIsNotNull() {
            addCriterion("allow_download is not null");
            return (Criteria) this;
        }

        public Criteria andAllowDownloadEqualTo(String value) {
            addCriterion("allow_download =", value, "allowDownload");
            return (Criteria) this;
        }

        public Criteria andAllowDownloadNotEqualTo(String value) {
            addCriterion("allow_download <>", value, "allowDownload");
            return (Criteria) this;
        }

        public Criteria andAllowDownloadGreaterThan(String value) {
            addCriterion("allow_download >", value, "allowDownload");
            return (Criteria) this;
        }

        public Criteria andAllowDownloadGreaterThanOrEqualTo(String value) {
            addCriterion("allow_download >=", value, "allowDownload");
            return (Criteria) this;
        }

        public Criteria andAllowDownloadLessThan(String value) {
            addCriterion("allow_download <", value, "allowDownload");
            return (Criteria) this;
        }

        public Criteria andAllowDownloadLessThanOrEqualTo(String value) {
            addCriterion("allow_download <=", value, "allowDownload");
            return (Criteria) this;
        }

        public Criteria andAllowDownloadLike(String value) {
            addCriterion("allow_download like", value, "allowDownload");
            return (Criteria) this;
        }

        public Criteria andAllowDownloadNotLike(String value) {
            addCriterion("allow_download not like", value, "allowDownload");
            return (Criteria) this;
        }

        public Criteria andAllowDownloadIn(List<String> values) {
            addCriterion("allow_download in", values, "allowDownload");
            return (Criteria) this;
        }

        public Criteria andAllowDownloadNotIn(List<String> values) {
            addCriterion("allow_download not in", values, "allowDownload");
            return (Criteria) this;
        }

        public Criteria andAllowDownloadBetween(String value1, String value2) {
            addCriterion("allow_download between", value1, value2, "allowDownload");
            return (Criteria) this;
        }

        public Criteria andAllowDownloadNotBetween(String value1, String value2) {
            addCriterion("allow_download not between", value1, value2, "allowDownload");
            return (Criteria) this;
        }

        public Criteria andIsSecretIsNull() {
            addCriterion("is_secret is null");
            return (Criteria) this;
        }

        public Criteria andIsSecretIsNotNull() {
            addCriterion("is_secret is not null");
            return (Criteria) this;
        }

        public Criteria andIsSecretEqualTo(String value) {
            addCriterion("is_secret =", value, "isSecret");
            return (Criteria) this;
        }

        public Criteria andIsSecretNotEqualTo(String value) {
            addCriterion("is_secret <>", value, "isSecret");
            return (Criteria) this;
        }

        public Criteria andIsSecretGreaterThan(String value) {
            addCriterion("is_secret >", value, "isSecret");
            return (Criteria) this;
        }

        public Criteria andIsSecretGreaterThanOrEqualTo(String value) {
            addCriterion("is_secret >=", value, "isSecret");
            return (Criteria) this;
        }

        public Criteria andIsSecretLessThan(String value) {
            addCriterion("is_secret <", value, "isSecret");
            return (Criteria) this;
        }

        public Criteria andIsSecretLessThanOrEqualTo(String value) {
            addCriterion("is_secret <=", value, "isSecret");
            return (Criteria) this;
        }

        public Criteria andIsSecretLike(String value) {
            addCriterion("is_secret like", value, "isSecret");
            return (Criteria) this;
        }

        public Criteria andIsSecretNotLike(String value) {
            addCriterion("is_secret not like", value, "isSecret");
            return (Criteria) this;
        }

        public Criteria andIsSecretIn(List<String> values) {
            addCriterion("is_secret in", values, "isSecret");
            return (Criteria) this;
        }

        public Criteria andIsSecretNotIn(List<String> values) {
            addCriterion("is_secret not in", values, "isSecret");
            return (Criteria) this;
        }

        public Criteria andIsSecretBetween(String value1, String value2) {
            addCriterion("is_secret between", value1, value2, "isSecret");
            return (Criteria) this;
        }

        public Criteria andIsSecretNotBetween(String value1, String value2) {
            addCriterion("is_secret not between", value1, value2, "isSecret");
            return (Criteria) this;
        }

        public Criteria andFetchCodeIsNull() {
            addCriterion("fetch_code is null");
            return (Criteria) this;
        }

        public Criteria andFetchCodeIsNotNull() {
            addCriterion("fetch_code is not null");
            return (Criteria) this;
        }

        public Criteria andFetchCodeEqualTo(String value) {
            addCriterion("fetch_code =", value, "fetchCode");
            return (Criteria) this;
        }

        public Criteria andFetchCodeNotEqualTo(String value) {
            addCriterion("fetch_code <>", value, "fetchCode");
            return (Criteria) this;
        }

        public Criteria andFetchCodeGreaterThan(String value) {
            addCriterion("fetch_code >", value, "fetchCode");
            return (Criteria) this;
        }

        public Criteria andFetchCodeGreaterThanOrEqualTo(String value) {
            addCriterion("fetch_code >=", value, "fetchCode");
            return (Criteria) this;
        }

        public Criteria andFetchCodeLessThan(String value) {
            addCriterion("fetch_code <", value, "fetchCode");
            return (Criteria) this;
        }

        public Criteria andFetchCodeLessThanOrEqualTo(String value) {
            addCriterion("fetch_code <=", value, "fetchCode");
            return (Criteria) this;
        }

        public Criteria andFetchCodeLike(String value) {
            addCriterion("fetch_code like", value, "fetchCode");
            return (Criteria) this;
        }

        public Criteria andFetchCodeNotLike(String value) {
            addCriterion("fetch_code not like", value, "fetchCode");
            return (Criteria) this;
        }

        public Criteria andFetchCodeIn(List<String> values) {
            addCriterion("fetch_code in", values, "fetchCode");
            return (Criteria) this;
        }

        public Criteria andFetchCodeNotIn(List<String> values) {
            addCriterion("fetch_code not in", values, "fetchCode");
            return (Criteria) this;
        }

        public Criteria andFetchCodeBetween(String value1, String value2) {
            addCriterion("fetch_code between", value1, value2, "fetchCode");
            return (Criteria) this;
        }

        public Criteria andFetchCodeNotBetween(String value1, String value2) {
            addCriterion("fetch_code not between", value1, value2, "fetchCode");
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