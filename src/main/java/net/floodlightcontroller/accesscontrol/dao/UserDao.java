package net.floodlightcontroller.accesscontrol.dao;

import net.floodlightcontroller.accesscontrol.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao {
    /**
     * 插入用户
     *
     * @param user 用户
     * @return 受影响的行数
     */
    int insertUser(User user);

    /**
     * 更新用户
     *
     * @param user 用户
     * @return 受影响的行数
     */
    int updateUser(User user);

    /**
     * 根据userId删除用户
     *
     * @param userId 用户ID
     * @return 受影响的行数
     */
    int deleteUser(int userId);

    /**
     * 查询所有用户
     *
     * @return List对象
     */
    List<User> listAllUsers();

    /**
     * 通过userId查询用户
     *
     * @param userId 用户ID
     * @return 查询的User对象
     */
    User getUserByUserId(int userId);

    /**
     * 通过用户名查询用户
     *
     * @param username 用户名
     * @return 查询的User对象
     */
    User getUserByUsername(String username);
}
