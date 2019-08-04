package net.floodlightcontroller.accesscontrol.dao;

import net.floodlightcontroller.accesscontrol.domain.Rule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RuleDao {
    /**
     * 添加规则
     *
     * @param rule 插入的Rule对象
     * @return 受影响的行数
     */
    int insertRule(Rule rule);

    /**
     * 删除规则
     *
     * @param ruleId Rule主键
     * @return 受影响的行数
     */
    int deleteRule(int ruleId);

    /**
     * 更新规则
     *
     * @param rule 要更新的Rule对象
     * @return 受影响的行数
     */
    int updateRule(Rule rule);

    /**
     * 查询所有规则
     *
     * @return 所有规则
     */
    List<Rule> listAllRules();
}
