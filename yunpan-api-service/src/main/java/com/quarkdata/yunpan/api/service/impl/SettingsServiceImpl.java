package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.quark.share.model.dataobj.Incorporation;
import com.quarkdata.yunpan.api.dal.api.UserInfoUtil;
import com.quarkdata.yunpan.api.dal.dao.DocumentVersionMapper2;
import com.quarkdata.yunpan.api.dal.dao.IncConfigMapper;
import com.quarkdata.yunpan.api.dal.dao.LogMapper;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.IncorporationApi;
import com.quarkdata.yunpan.api.model.common.ActionType;
import com.quarkdata.yunpan.api.model.common.Messages;
import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.model.common.YunPanApiException;
import com.quarkdata.yunpan.api.model.dataobj.IncConfig;
import com.quarkdata.yunpan.api.model.dataobj.IncConfigExample;
import com.quarkdata.yunpan.api.model.vo.IncConfigVO;
import com.quarkdata.yunpan.api.service.DeleteService;
import com.quarkdata.yunpan.api.service.SettingsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class SettingsServiceImpl extends BaseLogServiceImpl implements SettingsService {
    private Logger logger = Logger.getLogger(SettingsServiceImpl.class);

    @Autowired
    private IncorporationApi incorporationApi;

    @Autowired
    private IncConfigMapper incConfigMapper;

    @Autowired
    private DocumentVersionMapper2 documentVersionMapper2;

    @Autowired
    private DeleteService deleteService;

    private LogMapper logMapper;

    @Resource
    public void setLogMapper(LogMapper logMapper) {
        this.logMapper = logMapper;
        super.setLogMapper(logMapper);
    }


    @Override
    public ResultCode<IncConfigVO> checkSettings(Long incId) {
        ResultCode<IncConfigVO> result = new ResultCode<>();
        IncConfigVO vo = new IncConfigVO();

        // 查询企业信息
        ResultCode<Incorporation> incResult = this.incorporationApi.check(incId);

        // 查询企业配置信息
        IncConfigExample example = new IncConfigExample();
        example.createCriteria().andIncIdEqualTo(incId);
        IncConfig incConfig = this.incConfigMapper.selectByExample(example).get(0);

        // 封装数据
        vo.setIncorporation(incResult.getData());
        vo.setIncConfig(incConfig);

        result.setData(vo);

        return result;
    }

    @Override
    public ResultCode<Object> updateSettings(HttpServletRequest request, IncConfigVO incConfigVO, Long incId) throws YunPanApiException {
        ResultCode<Object> result = new ResultCode<Object>();

        // 1.更新企业配置信息
        IncConfig incConfig = incConfigVO.getIncConfig();
        incConfig.setIncId(incId);
        IncConfigExample example = new IncConfigExample();
        example.createCriteria().andIncIdEqualTo(incId);
        this.incConfigMapper.updateByExampleSelective(incConfig, example);

        // 2.组织文档版本删除逻辑
        // 查询需要删除的文档版本记录的ID,保存数据库,供定时任务去清理需要删除的文档版本
        Long userId = Long.valueOf(String.valueOf(UserInfoUtil.getUserInfoVO().getUser().getUserId()));
        String userName = UserInfoUtil.getUserInfoVO().getUser().getUserName();

        String historyVersionType = incConfig.getHistoryVersionType();
        Integer historyVersionParam = incConfig.getHistoryVersionParam();
        switch (historyVersionType) {
            case "0":
                // 保留所有历史版本
                break;
            case "1":
                // 保留最新的xx个历史版本
                List<Long> ids1 = this.documentVersionMapper2.updateDocumentHistoryVersionByNum(incId, historyVersionParam);
                this.deleteDocumentVersionBatch(incId, userId, userName, ids1);
                this.deleteService.addDelDocsToCephDelete(userId, incId, ids1);
                this.logger.info("批量删除组织文档历史版本(doucment_version):" + ids1);
                result.setData(ids1);
                break;
            case "2":
                // 保留最近xx天的历史版本
                List<Long> ids2 = this.documentVersionMapper2.updateDocumentHistoryVersionByDays(incId,
                        historyVersionParam);
                this.deleteDocumentVersionBatch(incId, userId, userName, ids2);
                this.deleteService.addDelDocsToCephDelete(userId, incId, ids2);
                this.logger.info("批量删除组织文档历史版本(doucment_version):" + ids2);
                result.setData(ids2);
                break;
            case "3":
                // 不保留历史版本
                List<Long> ids3 = this.documentVersionMapper2.updateDocumentHistoryVersionByNum(incId, 1);
                this.deleteDocumentVersionBatch(incId, userId, userName, ids3);
                this.deleteService.addDelDocsToCephDelete(userId, incId, ids3);
                this.logger.info("批量删除组织文档历史版本(doucment_version):" + ids3);
                result.setData(ids3);
                break;
            default:
                break;
        }

        // 3.更新企业名称
        result = this.incorporationApi.update(incId, incConfigVO.getIncorporation().getName());
        if (result.getCode() == 1) {
            throw new YunPanApiException(result.getCode(), result.getMsg());
        }

        // 4.记录操作日志
        this.addOtherLog(request, ActionType.UPDATE_INC_INFO_AND_INC_CONFIG, Messages.UPDATE_INCINFO_AND_INCCONFIG, new Date());

        return result;
    }

    @Override
    public ResultCode<IncConfigVO> getLogoAndPerUserQuota(Long incId) {
        ResultCode<IncConfigVO> result = new ResultCode<>();
        IncConfigVO vo = new IncConfigVO();

        // 查询企业配置信息
        IncConfigExample example = new IncConfigExample();
        example.createCriteria().andIncIdEqualTo(incId);
        IncConfig incConfig = this.incConfigMapper.selectByExample(example).get(0);
        IncConfig newIncConfig = new IncConfig();
        newIncConfig.setId(incConfig.getId());
        newIncConfig.setIncId(incConfig.getIncId());
        newIncConfig.setLogo(incConfig.getLogo());
        newIncConfig.setPerUserQuota(incConfig.getPerUserQuota());

        // 封装数据
        vo.setIncConfig(newIncConfig);

        result.setData(vo);

        return result;
    }

    @Override
    public ResultCode<Map<String, Object>> getIncInfo(Long incId) {
        ResultCode<Map<String, Object>> result = new ResultCode<>();
        Map<String, Object> data = new HashMap<>();
        // 查询企业信息
        Incorporation incorporation = this.incorporationApi.check(incId).getData();
        // 查询企业配置信息
        IncConfigExample example = new IncConfigExample();
        example.createCriteria().andIncIdEqualTo(incId);
        IncConfig incConfig = this.incConfigMapper.selectByExample(example).get(0);
        if(incorporation != null && incConfig != null) {
            data.put("name", incorporation.getName());
            data.put("telephone", incConfig.getTelephone());
            data.put("linkMan", incConfig.getLinkMan());
            data.put("email", incConfig.getEmail());
            data.put("logo", incConfig.getLogo());
            result.setData(data);
        }
        return result;
    }

    /**
     * 根据ID批量删除
     *
     * @param incId
     * @param ids
     */
    private void deleteDocumentVersionBatch(Long incId, Long userId, String userName, List<Long> ids) {
        if (ids != null && ids.size() > 0) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("incId", incId);
            params.put("userId", userId);
            params.put("username", userName);
            params.put("ids", ids);
            this.documentVersionMapper2.deleteDocumentVersionByIds(params);
        }
    }

}
