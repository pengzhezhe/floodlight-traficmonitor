package net.floodlightcontroller.accesscontrol;

import net.floodlightcontroller.accesscontrol.domain.Device;
import net.floodlightcontroller.accesscontrol.domain.Rule;
import net.floodlightcontroller.accesscontrol.domain.User;
import net.floodlightcontroller.core.module.IFloodlightService;

import java.util.List;

public interface IAccessControl extends IFloodlightService {
    /**
     * 根据userId查询用户
     *
     * @param userId userId
     * @return User
     */
    User getUserById(int userId);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return User
     */
    User getUserByUsername(String username);

    /**
     * 查询所有用户
     *
     * @return 所有User的List
     */
    List<User> listAllUsers();

    /**
     * 添加用户
     *
     * @param user User
     * @return 操作的状态
     */
    boolean addUser(User user);

    /**
     * 更新用户
     *
     * @param user User
     * @return 操作的状态
     */
    boolean updateUser(User user);

    /**
     * 删除用户
     *
     * @param userId userId
     * @return 操作的状态
     */
    boolean deleteUser(int userId);

    /**
     * 查询一个用户的所有设备
     *
     * @param userId userId
     * @return 所有设备的List
     */
    List<Device> listDevicesByUserId(int userId);

    /**
     * 查询所有设备
     *
     * @return 所有设备的List
     */
    List<Device> listAllDevices();

    /**
     * 添加设备
     *
     * @param device Device
     * @return 操作的状态
     */
    boolean addDevice(Device device);

    /**
     * 更新设备信息
     *
     * @param device Device
     * @return 操作的状态
     */
    boolean updateDevice(Device device);

    /**
     * 删除设备信息
     *
     * @param deviceId deviceId
     * @return 操作的状态
     */
    boolean deleteDevice(int deviceId);

    /**
     * 查询所有规则
     *
     * @return Rule的List
     */
    List<Rule> listAllRules();

    /**
     * 添加规则
     *
     * @param rule Rule
     * @return 操作的状态
     */
    boolean addRule(Rule rule);

    /**
     * 更新规则
     *
     * @param rule Rule
     * @return 操作的状态
     */
    boolean updateRule(Rule rule);

    /**
     * 删除规则
     *
     * @param ruleId ruleId
     * @return 操作的状态
     */
    boolean deleteRule(int ruleId);
}
