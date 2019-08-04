package net.floodlightcontroller.accesscontrol.utils;


import java.sql.*;

public class Db {
    private Connection connection;

    /**
     * 获取数据库连接
     */
    private void getConnection() {
        String url = "jdbc:mysql://localhost:3306/sdn?useSSL=false";
        String username = "root";
        String password = "123456";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据MAC地址查询设备类型，用户权限等级
     *
     * @param mac MAC地址
     * @return 查询结果
     */
    public DeviceInfo getDeviceInfoByMAC(String mac) {
        getConnection();
        DeviceInfo deviceInfo = null;
        String sql = "select u.level,d.mac_address,d.device_type from device d,user u where u.user_id=d.user_id and d.mac_address=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, mac);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                deviceInfo = new DeviceInfo();
                deviceInfo.setDeviceType(resultSet.getInt("device_type"));
                deviceInfo.setMacAddress(resultSet.getString("mac_address"));
                deviceInfo.setUserLevel(resultSet.getInt("level"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return deviceInfo;
    }

    /**
     * 根据源、目的设备信息在role表中查找
     *
     * @param srcDeviceInfo 源设备信息
     * @param dstDeviceInfo 目的设备信息
     * @return 是否禁止访问
     */
    public boolean isDeny(DeviceInfo srcDeviceInfo, DeviceInfo dstDeviceInfo) {
        if (srcDeviceInfo == null && dstDeviceInfo.getDeviceType() != 3)
            return true;
        if (srcDeviceInfo == null || dstDeviceInfo == null)
            return false;
        getConnection();
        String sql = "select action from rule where device_type=? and user_level=? and dest_device_type=? and dest_user_level=?";
        String action = "";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, srcDeviceInfo.getDeviceType());
            preparedStatement.setInt(2, srcDeviceInfo.getUserLevel());
            preparedStatement.setInt(3, dstDeviceInfo.getDeviceType());
            preparedStatement.setInt(4, dstDeviceInfo.getUserLevel());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                action = resultSet.getString("action");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return action.equals("deny");
    }
}
