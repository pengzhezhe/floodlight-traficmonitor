package net.floodlightcontroller.accesscontrol.dao;

import net.floodlightcontroller.accesscontrol.domain.Device;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeviceDao {
    /**
     * 插入设备
     *
     * @param device 要插入的Device对象
     * @return 受影响的行数
     */
    int insertDevice(Device device);

    /**
     * 根据设备id删除设备
     *
     * @param deviceId 设备id
     * @return 受影响的行数
     */
    int deleteDevice(int deviceId);

    /**
     * 更新设备
     *
     * @param device 要更新的Device对象
     * @return 受影响的行数
     */
    int updateDevice(Device device);

    /**
     * 查询所有设备
     *
     * @return List对象
     */
    List<Device> listAllDevices();

    /**
     * 根据userId查询用户所有设备
     *
     * @param userId userId
     * @return 查询的设备的列表对象
     */
    List<Device> listDevicesByUserId(int userId);
}
