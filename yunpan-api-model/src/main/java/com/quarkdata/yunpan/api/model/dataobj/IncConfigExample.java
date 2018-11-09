package com.quarkdata.yunpan.api.model.dataobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IncConfigExample implements Serializable {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    protected Integer limitStart;

    protected Integer limitEnd;

    private static final long serialVersionUID = 1L;

    public IncConfigExample() {
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

        public Criteria andHistoryVersionTypeIsNull() {
            addCriterion("history_version_type is null");
            return (Criteria) this;
        }

        public Criteria andHistoryVersionTypeIsNotNull() {
            addCriterion("history_version_type is not null");
            return (Criteria) this;
        }

        public Criteria andHistoryVersionTypeEqualTo(String value) {
            addCriterion("history_version_type =", value, "historyVersionType");
            return (Criteria) this;
        }

        public Criteria andHistoryVersionTypeNotEqualTo(String value) {
            addCriterion("history_version_type <>", value, "historyVersionType");
            return (Criteria) this;
        }

        public Criteria andHistoryVersionTypeGreaterThan(String value) {
            addCriterion("history_version_type >", value, "historyVersionType");
            return (Criteria) this;
        }

        public Criteria andHistoryVersionTypeGreaterThanOrEqualTo(String value) {
            addCriterion("history_version_type >=", value, "historyVersionType");
            return (Criteria) this;
        }

        public Criteria andHistoryVersionTypeLessThan(String value) {
            addCriterion("history_version_type <", value, "historyVersionType");
            return (Criteria) this;
        }

        public Criteria andHistoryVersionTypeLessThanOrEqualTo(String value) {
            addCriterion("history_version_type <=", value, "historyVersionType");
            return (Criteria) this;
        }

        public Criteria andHistoryVersionTypeLike(String value) {
            addCriterion("history_version_type like", value, "historyVersionType");
            return (Criteria) this;
        }

        public Criteria andHistoryVersionTypeNotLike(String value) {
            addCriterion("history_version_type not like", value, "historyVersionType");
            return (Criteria) this;
        }

        public Criteria andHistoryVersionTypeIn(List<String> values) {
            addCriterion("history_version_type in", values, "historyVersionType");
            return (Criteria) this;
        }

        public Criteria andHistoryVersionTypeNotIn(List<String> values) {
            addCriterion("history_version_type not in", values, "historyVersionType");
            return (Criteria) this;
        }

        public Criteria andHistoryVersionTypeBetween(String value1, String value2) {
            addCriterion("history_version_type between", value1, value2, "historyVersionType");
            return (Criteria) this;
        }

        public Criteria andHistoryVersionTypeNotBetween(String value1, String value2) {
            addCriterion("history_version_type not between", value1, value2, "historyVersionType");
            return (Criteria) this;
        }

        public Criteria andHistoryVersionParamIsNull() {
            addCriterion("history_version_param is null");
            return (Criteria) this;
        }

        public Criteria andHistoryVersionParamIsNotNull() {
            addCriterion("history_version_param is not null");
            return (Criteria) this;
        }

        public Criteria andHistoryVersionParamEqualTo(Integer value) {
            addCriterion("history_version_param =", value, "historyVersionParam");
            return (Criteria) this;
        }

        public Criteria andHistoryVersionParamNotEqualTo(Integer value) {
            addCriterion("history_version_param <>", value, "historyVersionParam");
            return (Criteria) this;
        }

        public Criteria andHistoryVersionParamGreaterThan(Integer value) {
            addCriterion("history_version_param >", value, "historyVersionParam");
            return (Criteria) this;
        }

        public Criteria andHistoryVersionParamGreaterThanOrEqualTo(Integer value) {
            addCriterion("history_version_param >=", value, "historyVersionParam");
            return (Criteria) this;
        }

        public Criteria andHistoryVersionParamLessThan(Integer value) {
            addCriterion("history_version_param <", value, "historyVersionParam");
            return (Criteria) this;
        }

        public Criteria andHistoryVersionParamLessThanOrEqualTo(Integer value) {
            addCriterion("history_version_param <=", value, "historyVersionParam");
            return (Criteria) this;
        }

        public Criteria andHistoryVersionParamIn(List<Integer> values) {
            addCriterion("history_version_param in", values, "historyVersionParam");
            return (Criteria) this;
        }

        public Criteria andHistoryVersionParamNotIn(List<Integer> values) {
            addCriterion("history_version_param not in", values, "historyVersionParam");
            return (Criteria) this;
        }

        public Criteria andHistoryVersionParamBetween(Integer value1, Integer value2) {
            addCriterion("history_version_param between", value1, value2, "historyVersionParam");
            return (Criteria) this;
        }

        public Criteria andHistoryVersionParamNotBetween(Integer value1, Integer value2) {
            addCriterion("history_version_param not between", value1, value2, "historyVersionParam");
            return (Criteria) this;
        }

        public Criteria andIncRatioIsNull() {
            addCriterion("inc_ratio is null");
            return (Criteria) this;
        }

        public Criteria andIncRatioIsNotNull() {
            addCriterion("inc_ratio is not null");
            return (Criteria) this;
        }

        public Criteria andIncRatioEqualTo(Integer value) {
            addCriterion("inc_ratio =", value, "incRatio");
            return (Criteria) this;
        }

        public Criteria andIncRatioNotEqualTo(Integer value) {
            addCriterion("inc_ratio <>", value, "incRatio");
            return (Criteria) this;
        }

        public Criteria andIncRatioGreaterThan(Integer value) {
            addCriterion("inc_ratio >", value, "incRatio");
            return (Criteria) this;
        }

        public Criteria andIncRatioGreaterThanOrEqualTo(Integer value) {
            addCriterion("inc_ratio >=", value, "incRatio");
            return (Criteria) this;
        }

        public Criteria andIncRatioLessThan(Integer value) {
            addCriterion("inc_ratio <", value, "incRatio");
            return (Criteria) this;
        }

        public Criteria andIncRatioLessThanOrEqualTo(Integer value) {
            addCriterion("inc_ratio <=", value, "incRatio");
            return (Criteria) this;
        }

        public Criteria andIncRatioIn(List<Integer> values) {
            addCriterion("inc_ratio in", values, "incRatio");
            return (Criteria) this;
        }

        public Criteria andIncRatioNotIn(List<Integer> values) {
            addCriterion("inc_ratio not in", values, "incRatio");
            return (Criteria) this;
        }

        public Criteria andIncRatioBetween(Integer value1, Integer value2) {
            addCriterion("inc_ratio between", value1, value2, "incRatio");
            return (Criteria) this;
        }

        public Criteria andIncRatioNotBetween(Integer value1, Integer value2) {
            addCriterion("inc_ratio not between", value1, value2, "incRatio");
            return (Criteria) this;
        }

        public Criteria andUserRatioIsNull() {
            addCriterion("user_ratio is null");
            return (Criteria) this;
        }

        public Criteria andUserRatioIsNotNull() {
            addCriterion("user_ratio is not null");
            return (Criteria) this;
        }

        public Criteria andUserRatioEqualTo(Integer value) {
            addCriterion("user_ratio =", value, "userRatio");
            return (Criteria) this;
        }

        public Criteria andUserRatioNotEqualTo(Integer value) {
            addCriterion("user_ratio <>", value, "userRatio");
            return (Criteria) this;
        }

        public Criteria andUserRatioGreaterThan(Integer value) {
            addCriterion("user_ratio >", value, "userRatio");
            return (Criteria) this;
        }

        public Criteria andUserRatioGreaterThanOrEqualTo(Integer value) {
            addCriterion("user_ratio >=", value, "userRatio");
            return (Criteria) this;
        }

        public Criteria andUserRatioLessThan(Integer value) {
            addCriterion("user_ratio <", value, "userRatio");
            return (Criteria) this;
        }

        public Criteria andUserRatioLessThanOrEqualTo(Integer value) {
            addCriterion("user_ratio <=", value, "userRatio");
            return (Criteria) this;
        }

        public Criteria andUserRatioIn(List<Integer> values) {
            addCriterion("user_ratio in", values, "userRatio");
            return (Criteria) this;
        }

        public Criteria andUserRatioNotIn(List<Integer> values) {
            addCriterion("user_ratio not in", values, "userRatio");
            return (Criteria) this;
        }

        public Criteria andUserRatioBetween(Integer value1, Integer value2) {
            addCriterion("user_ratio between", value1, value2, "userRatio");
            return (Criteria) this;
        }

        public Criteria andUserRatioNotBetween(Integer value1, Integer value2) {
            addCriterion("user_ratio not between", value1, value2, "userRatio");
            return (Criteria) this;
        }

        public Criteria andPerUserQuotaIsNull() {
            addCriterion("per_user_quota is null");
            return (Criteria) this;
        }

        public Criteria andPerUserQuotaIsNotNull() {
            addCriterion("per_user_quota is not null");
            return (Criteria) this;
        }

        public Criteria andPerUserQuotaEqualTo(Integer value) {
            addCriterion("per_user_quota =", value, "perUserQuota");
            return (Criteria) this;
        }

        public Criteria andPerUserQuotaNotEqualTo(Integer value) {
            addCriterion("per_user_quota <>", value, "perUserQuota");
            return (Criteria) this;
        }

        public Criteria andPerUserQuotaGreaterThan(Integer value) {
            addCriterion("per_user_quota >", value, "perUserQuota");
            return (Criteria) this;
        }

        public Criteria andPerUserQuotaGreaterThanOrEqualTo(Integer value) {
            addCriterion("per_user_quota >=", value, "perUserQuota");
            return (Criteria) this;
        }

        public Criteria andPerUserQuotaLessThan(Integer value) {
            addCriterion("per_user_quota <", value, "perUserQuota");
            return (Criteria) this;
        }

        public Criteria andPerUserQuotaLessThanOrEqualTo(Integer value) {
            addCriterion("per_user_quota <=", value, "perUserQuota");
            return (Criteria) this;
        }

        public Criteria andPerUserQuotaIn(List<Integer> values) {
            addCriterion("per_user_quota in", values, "perUserQuota");
            return (Criteria) this;
        }

        public Criteria andPerUserQuotaNotIn(List<Integer> values) {
            addCriterion("per_user_quota not in", values, "perUserQuota");
            return (Criteria) this;
        }

        public Criteria andPerUserQuotaBetween(Integer value1, Integer value2) {
            addCriterion("per_user_quota between", value1, value2, "perUserQuota");
            return (Criteria) this;
        }

        public Criteria andPerUserQuotaNotBetween(Integer value1, Integer value2) {
            addCriterion("per_user_quota not between", value1, value2, "perUserQuota");
            return (Criteria) this;
        }

        public Criteria andIncTotalQuotaIsNull() {
            addCriterion("inc_total_quota is null");
            return (Criteria) this;
        }

        public Criteria andIncTotalQuotaIsNotNull() {
            addCriterion("inc_total_quota is not null");
            return (Criteria) this;
        }

        public Criteria andIncTotalQuotaEqualTo(Integer value) {
            addCriterion("inc_total_quota =", value, "incTotalQuota");
            return (Criteria) this;
        }

        public Criteria andIncTotalQuotaNotEqualTo(Integer value) {
            addCriterion("inc_total_quota <>", value, "incTotalQuota");
            return (Criteria) this;
        }

        public Criteria andIncTotalQuotaGreaterThan(Integer value) {
            addCriterion("inc_total_quota >", value, "incTotalQuota");
            return (Criteria) this;
        }

        public Criteria andIncTotalQuotaGreaterThanOrEqualTo(Integer value) {
            addCriterion("inc_total_quota >=", value, "incTotalQuota");
            return (Criteria) this;
        }

        public Criteria andIncTotalQuotaLessThan(Integer value) {
            addCriterion("inc_total_quota <", value, "incTotalQuota");
            return (Criteria) this;
        }

        public Criteria andIncTotalQuotaLessThanOrEqualTo(Integer value) {
            addCriterion("inc_total_quota <=", value, "incTotalQuota");
            return (Criteria) this;
        }

        public Criteria andIncTotalQuotaIn(List<Integer> values) {
            addCriterion("inc_total_quota in", values, "incTotalQuota");
            return (Criteria) this;
        }

        public Criteria andIncTotalQuotaNotIn(List<Integer> values) {
            addCriterion("inc_total_quota not in", values, "incTotalQuota");
            return (Criteria) this;
        }

        public Criteria andIncTotalQuotaBetween(Integer value1, Integer value2) {
            addCriterion("inc_total_quota between", value1, value2, "incTotalQuota");
            return (Criteria) this;
        }

        public Criteria andIncTotalQuotaNotBetween(Integer value1, Integer value2) {
            addCriterion("inc_total_quota not between", value1, value2, "incTotalQuota");
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

        public Criteria andLinkManIsNull() {
            addCriterion("link_man is null");
            return (Criteria) this;
        }

        public Criteria andLinkManIsNotNull() {
            addCriterion("link_man is not null");
            return (Criteria) this;
        }

        public Criteria andLinkManEqualTo(String value) {
            addCriterion("link_man =", value, "linkMan");
            return (Criteria) this;
        }

        public Criteria andLinkManNotEqualTo(String value) {
            addCriterion("link_man <>", value, "linkMan");
            return (Criteria) this;
        }

        public Criteria andLinkManGreaterThan(String value) {
            addCriterion("link_man >", value, "linkMan");
            return (Criteria) this;
        }

        public Criteria andLinkManGreaterThanOrEqualTo(String value) {
            addCriterion("link_man >=", value, "linkMan");
            return (Criteria) this;
        }

        public Criteria andLinkManLessThan(String value) {
            addCriterion("link_man <", value, "linkMan");
            return (Criteria) this;
        }

        public Criteria andLinkManLessThanOrEqualTo(String value) {
            addCriterion("link_man <=", value, "linkMan");
            return (Criteria) this;
        }

        public Criteria andLinkManLike(String value) {
            addCriterion("link_man like", value, "linkMan");
            return (Criteria) this;
        }

        public Criteria andLinkManNotLike(String value) {
            addCriterion("link_man not like", value, "linkMan");
            return (Criteria) this;
        }

        public Criteria andLinkManIn(List<String> values) {
            addCriterion("link_man in", values, "linkMan");
            return (Criteria) this;
        }

        public Criteria andLinkManNotIn(List<String> values) {
            addCriterion("link_man not in", values, "linkMan");
            return (Criteria) this;
        }

        public Criteria andLinkManBetween(String value1, String value2) {
            addCriterion("link_man between", value1, value2, "linkMan");
            return (Criteria) this;
        }

        public Criteria andLinkManNotBetween(String value1, String value2) {
            addCriterion("link_man not between", value1, value2, "linkMan");
            return (Criteria) this;
        }

        public Criteria andTelephoneIsNull() {
            addCriterion("telephone is null");
            return (Criteria) this;
        }

        public Criteria andTelephoneIsNotNull() {
            addCriterion("telephone is not null");
            return (Criteria) this;
        }

        public Criteria andTelephoneEqualTo(String value) {
            addCriterion("telephone =", value, "telephone");
            return (Criteria) this;
        }

        public Criteria andTelephoneNotEqualTo(String value) {
            addCriterion("telephone <>", value, "telephone");
            return (Criteria) this;
        }

        public Criteria andTelephoneGreaterThan(String value) {
            addCriterion("telephone >", value, "telephone");
            return (Criteria) this;
        }

        public Criteria andTelephoneGreaterThanOrEqualTo(String value) {
            addCriterion("telephone >=", value, "telephone");
            return (Criteria) this;
        }

        public Criteria andTelephoneLessThan(String value) {
            addCriterion("telephone <", value, "telephone");
            return (Criteria) this;
        }

        public Criteria andTelephoneLessThanOrEqualTo(String value) {
            addCriterion("telephone <=", value, "telephone");
            return (Criteria) this;
        }

        public Criteria andTelephoneLike(String value) {
            addCriterion("telephone like", value, "telephone");
            return (Criteria) this;
        }

        public Criteria andTelephoneNotLike(String value) {
            addCriterion("telephone not like", value, "telephone");
            return (Criteria) this;
        }

        public Criteria andTelephoneIn(List<String> values) {
            addCriterion("telephone in", values, "telephone");
            return (Criteria) this;
        }

        public Criteria andTelephoneNotIn(List<String> values) {
            addCriterion("telephone not in", values, "telephone");
            return (Criteria) this;
        }

        public Criteria andTelephoneBetween(String value1, String value2) {
            addCriterion("telephone between", value1, value2, "telephone");
            return (Criteria) this;
        }

        public Criteria andTelephoneNotBetween(String value1, String value2) {
            addCriterion("telephone not between", value1, value2, "telephone");
            return (Criteria) this;
        }

        public Criteria andEmailIsNull() {
            addCriterion("email is null");
            return (Criteria) this;
        }

        public Criteria andEmailIsNotNull() {
            addCriterion("email is not null");
            return (Criteria) this;
        }

        public Criteria andEmailEqualTo(String value) {
            addCriterion("email =", value, "email");
            return (Criteria) this;
        }

        public Criteria andEmailNotEqualTo(String value) {
            addCriterion("email <>", value, "email");
            return (Criteria) this;
        }

        public Criteria andEmailGreaterThan(String value) {
            addCriterion("email >", value, "email");
            return (Criteria) this;
        }

        public Criteria andEmailGreaterThanOrEqualTo(String value) {
            addCriterion("email >=", value, "email");
            return (Criteria) this;
        }

        public Criteria andEmailLessThan(String value) {
            addCriterion("email <", value, "email");
            return (Criteria) this;
        }

        public Criteria andEmailLessThanOrEqualTo(String value) {
            addCriterion("email <=", value, "email");
            return (Criteria) this;
        }

        public Criteria andEmailLike(String value) {
            addCriterion("email like", value, "email");
            return (Criteria) this;
        }

        public Criteria andEmailNotLike(String value) {
            addCriterion("email not like", value, "email");
            return (Criteria) this;
        }

        public Criteria andEmailIn(List<String> values) {
            addCriterion("email in", values, "email");
            return (Criteria) this;
        }

        public Criteria andEmailNotIn(List<String> values) {
            addCriterion("email not in", values, "email");
            return (Criteria) this;
        }

        public Criteria andEmailBetween(String value1, String value2) {
            addCriterion("email between", value1, value2, "email");
            return (Criteria) this;
        }

        public Criteria andEmailNotBetween(String value1, String value2) {
            addCriterion("email not between", value1, value2, "email");
            return (Criteria) this;
        }

        public Criteria andCephAccessKeyIsNull() {
            addCriterion("ceph_access_key is null");
            return (Criteria) this;
        }

        public Criteria andCephAccessKeyIsNotNull() {
            addCriterion("ceph_access_key is not null");
            return (Criteria) this;
        }

        public Criteria andCephAccessKeyEqualTo(String value) {
            addCriterion("ceph_access_key =", value, "cephAccessKey");
            return (Criteria) this;
        }

        public Criteria andCephAccessKeyNotEqualTo(String value) {
            addCriterion("ceph_access_key <>", value, "cephAccessKey");
            return (Criteria) this;
        }

        public Criteria andCephAccessKeyGreaterThan(String value) {
            addCriterion("ceph_access_key >", value, "cephAccessKey");
            return (Criteria) this;
        }

        public Criteria andCephAccessKeyGreaterThanOrEqualTo(String value) {
            addCriterion("ceph_access_key >=", value, "cephAccessKey");
            return (Criteria) this;
        }

        public Criteria andCephAccessKeyLessThan(String value) {
            addCriterion("ceph_access_key <", value, "cephAccessKey");
            return (Criteria) this;
        }

        public Criteria andCephAccessKeyLessThanOrEqualTo(String value) {
            addCriterion("ceph_access_key <=", value, "cephAccessKey");
            return (Criteria) this;
        }

        public Criteria andCephAccessKeyLike(String value) {
            addCriterion("ceph_access_key like", value, "cephAccessKey");
            return (Criteria) this;
        }

        public Criteria andCephAccessKeyNotLike(String value) {
            addCriterion("ceph_access_key not like", value, "cephAccessKey");
            return (Criteria) this;
        }

        public Criteria andCephAccessKeyIn(List<String> values) {
            addCriterion("ceph_access_key in", values, "cephAccessKey");
            return (Criteria) this;
        }

        public Criteria andCephAccessKeyNotIn(List<String> values) {
            addCriterion("ceph_access_key not in", values, "cephAccessKey");
            return (Criteria) this;
        }

        public Criteria andCephAccessKeyBetween(String value1, String value2) {
            addCriterion("ceph_access_key between", value1, value2, "cephAccessKey");
            return (Criteria) this;
        }

        public Criteria andCephAccessKeyNotBetween(String value1, String value2) {
            addCriterion("ceph_access_key not between", value1, value2, "cephAccessKey");
            return (Criteria) this;
        }

        public Criteria andCephSecretKeyIsNull() {
            addCriterion("ceph_secret_key is null");
            return (Criteria) this;
        }

        public Criteria andCephSecretKeyIsNotNull() {
            addCriterion("ceph_secret_key is not null");
            return (Criteria) this;
        }

        public Criteria andCephSecretKeyEqualTo(String value) {
            addCriterion("ceph_secret_key =", value, "cephSecretKey");
            return (Criteria) this;
        }

        public Criteria andCephSecretKeyNotEqualTo(String value) {
            addCriterion("ceph_secret_key <>", value, "cephSecretKey");
            return (Criteria) this;
        }

        public Criteria andCephSecretKeyGreaterThan(String value) {
            addCriterion("ceph_secret_key >", value, "cephSecretKey");
            return (Criteria) this;
        }

        public Criteria andCephSecretKeyGreaterThanOrEqualTo(String value) {
            addCriterion("ceph_secret_key >=", value, "cephSecretKey");
            return (Criteria) this;
        }

        public Criteria andCephSecretKeyLessThan(String value) {
            addCriterion("ceph_secret_key <", value, "cephSecretKey");
            return (Criteria) this;
        }

        public Criteria andCephSecretKeyLessThanOrEqualTo(String value) {
            addCriterion("ceph_secret_key <=", value, "cephSecretKey");
            return (Criteria) this;
        }

        public Criteria andCephSecretKeyLike(String value) {
            addCriterion("ceph_secret_key like", value, "cephSecretKey");
            return (Criteria) this;
        }

        public Criteria andCephSecretKeyNotLike(String value) {
            addCriterion("ceph_secret_key not like", value, "cephSecretKey");
            return (Criteria) this;
        }

        public Criteria andCephSecretKeyIn(List<String> values) {
            addCriterion("ceph_secret_key in", values, "cephSecretKey");
            return (Criteria) this;
        }

        public Criteria andCephSecretKeyNotIn(List<String> values) {
            addCriterion("ceph_secret_key not in", values, "cephSecretKey");
            return (Criteria) this;
        }

        public Criteria andCephSecretKeyBetween(String value1, String value2) {
            addCriterion("ceph_secret_key between", value1, value2, "cephSecretKey");
            return (Criteria) this;
        }

        public Criteria andCephSecretKeyNotBetween(String value1, String value2) {
            addCriterion("ceph_secret_key not between", value1, value2, "cephSecretKey");
            return (Criteria) this;
        }

        public Criteria andCephUrlIsNull() {
            addCriterion("ceph_url is null");
            return (Criteria) this;
        }

        public Criteria andCephUrlIsNotNull() {
            addCriterion("ceph_url is not null");
            return (Criteria) this;
        }

        public Criteria andCephUrlEqualTo(String value) {
            addCriterion("ceph_url =", value, "cephUrl");
            return (Criteria) this;
        }

        public Criteria andCephUrlNotEqualTo(String value) {
            addCriterion("ceph_url <>", value, "cephUrl");
            return (Criteria) this;
        }

        public Criteria andCephUrlGreaterThan(String value) {
            addCriterion("ceph_url >", value, "cephUrl");
            return (Criteria) this;
        }

        public Criteria andCephUrlGreaterThanOrEqualTo(String value) {
            addCriterion("ceph_url >=", value, "cephUrl");
            return (Criteria) this;
        }

        public Criteria andCephUrlLessThan(String value) {
            addCriterion("ceph_url <", value, "cephUrl");
            return (Criteria) this;
        }

        public Criteria andCephUrlLessThanOrEqualTo(String value) {
            addCriterion("ceph_url <=", value, "cephUrl");
            return (Criteria) this;
        }

        public Criteria andCephUrlLike(String value) {
            addCriterion("ceph_url like", value, "cephUrl");
            return (Criteria) this;
        }

        public Criteria andCephUrlNotLike(String value) {
            addCriterion("ceph_url not like", value, "cephUrl");
            return (Criteria) this;
        }

        public Criteria andCephUrlIn(List<String> values) {
            addCriterion("ceph_url in", values, "cephUrl");
            return (Criteria) this;
        }

        public Criteria andCephUrlNotIn(List<String> values) {
            addCriterion("ceph_url not in", values, "cephUrl");
            return (Criteria) this;
        }

        public Criteria andCephUrlBetween(String value1, String value2) {
            addCriterion("ceph_url between", value1, value2, "cephUrl");
            return (Criteria) this;
        }

        public Criteria andCephUrlNotBetween(String value1, String value2) {
            addCriterion("ceph_url not between", value1, value2, "cephUrl");
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