package net.floodlightcontroller.accesscontrol;

import net.floodlightcontroller.accesscontrol.dao.DeviceDao;
import net.floodlightcontroller.accesscontrol.dao.RuleDao;
import net.floodlightcontroller.accesscontrol.dao.UserDao;
import net.floodlightcontroller.accesscontrol.domain.Device;
import net.floodlightcontroller.accesscontrol.domain.Rule;
import net.floodlightcontroller.accesscontrol.domain.User;
import net.floodlightcontroller.accesscontrol.utils.Db;
import net.floodlightcontroller.accesscontrol.utils.DbUtil;
import net.floodlightcontroller.accesscontrol.utils.DeviceInfo;
import net.floodlightcontroller.accesscontrol.web.AccessControlWebRouter;
import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.staticentry.IStaticEntryPusherService;
import org.apache.ibatis.session.SqlSession;
import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.protocol.OFFlowAdd;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.EthType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AccessControl implements IAccessControl, IOFMessageListener, IFloodlightModule {

    protected static Logger logger;
    protected IFloodlightProviderService floodlightProvider;
    protected IStaticEntryPusherService staticEntryPusherService;
    protected IRestApiService restApi;
    private int flowNum;

    @Override
    public String getName() {
        return AccessControl.class.getSimpleName();
    }


    @Override
    public boolean isCallbackOrderingPrereq(OFType type, String name) {
        return (type == OFType.PACKET_IN && name.equals("forwarding"));
    }

    @Override
    public boolean isCallbackOrderingPostreq(OFType type, String name) {
        return false;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IAccessControl.class);
        return l;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
        Map<Class<? extends IFloodlightService>, IFloodlightService> m = new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
        m.put(IAccessControl.class, this);
        return m;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        Collection<Class<? extends IFloodlightService>> l = new ArrayList<>();
        l.add(IFloodlightProviderService.class);
        l.add(IStaticEntryPusherService.class);
        l.add(IRestApiService.class);
        return l;
    }

    @Override
    public void init(FloodlightModuleContext context) throws FloodlightModuleException {
        //模块初始化
        floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
        staticEntryPusherService = context.getServiceImpl(IStaticEntryPusherService.class);
        restApi = context.getServiceImpl(IRestApiService.class);
        logger = LoggerFactory.getLogger(AccessControl.class);
        flowNum = 0;
    }

    @Override
    public void startUp(FloodlightModuleContext context) {
        floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
        restApi.addRestletRoutable(new AccessControlWebRouter());
    }

    @Override
    public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
        Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
        if (eth.getEtherType() == EthType.IPv4) {
            IPv4 iPv4 = (IPv4) eth.getPayload();
            Db db = new Db();
            DeviceInfo srcDeviceInfo = db.getDeviceInfoByMAC(eth.getSourceMACAddress().toString());
            DeviceInfo dstDeviceInfo = db.getDeviceInfoByMAC(eth.getDestinationMACAddress().toString());
            if (db.isDeny(srcDeviceInfo, dstDeviceInfo)) {
                //使用OpenFlowJ构建Flow
                OFFactory ofFactory = sw.getOFFactory();
                //设置流表的匹配域
                Match match = ofFactory.buildMatch()
                        .setExact(MatchField.ETH_TYPE, EthType.IPv4)
                        .setExact(MatchField.ETH_SRC, eth.getSourceMACAddress())
                        .setExact(MatchField.ETH_DST, eth.getDestinationMACAddress())
                        .setExact(MatchField.IPV4_SRC, iPv4.getSourceAddress())
                        .setExact(MatchField.IPV4_DST, iPv4.getDestinationAddress())
                        .build();

                //设备流表的属性
                OFFlowAdd flowAdd = ofFactory.buildFlowAdd()
                        .setPriority(29999)
                        .setIdleTimeout(0)
                        .setHardTimeout(0)
                        .setMatch(match)
                        .build();
                //使用StaticEntryPusher发送流表
                staticEntryPusherService.addFlow("flow" + flowNum++, flowAdd, sw.getId());
                return Command.STOP;
            }
        }
        return Command.CONTINUE;
    }

    @Override
    public List<User> listAllUsers() {
        SqlSession sqlSession = DbUtil.getSqlSession();
        UserDao userDao = sqlSession.getMapper(UserDao.class);
        List<User> users = userDao.listAllUsers();
        sqlSession.close();
        return users;
    }

    @Override
    public User getUserById(int userId) {
        SqlSession sqlSession = DbUtil.getSqlSession();
        UserDao userDao = sqlSession.getMapper(UserDao.class);
        User user = userDao.getUserByUserId(userId);
        sqlSession.close();
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        SqlSession sqlSession = DbUtil.getSqlSession();
        UserDao userDao = sqlSession.getMapper(UserDao.class);
        User user = userDao.getUserByUsername(username);
        sqlSession.close();
        return user;
    }

    @Override
    public boolean addUser(User user) {
        SqlSession sqlSession = DbUtil.getSqlSession();
        UserDao userDao = sqlSession.getMapper(UserDao.class);
        int i = userDao.insertUser(user);
        sqlSession.close();
        return i > 0;
    }

    @Override
    public boolean updateUser(User user) {
        SqlSession sqlSession = DbUtil.getSqlSession();
        UserDao userDao = sqlSession.getMapper(UserDao.class);
        int i = userDao.updateUser(user);
        sqlSession.close();
        return i > 0;
    }

    @Override
    public boolean deleteUser(int userId) {
        SqlSession sqlSession = DbUtil.getSqlSession();
        UserDao userDao = sqlSession.getMapper(UserDao.class);
        int i = userDao.deleteUser(userId);
        sqlSession.close();
        return i > 0;
    }

    @Override
    public List<Device> listDevicesByUserId(int userId) {
        SqlSession sqlSession = DbUtil.getSqlSession();
        DeviceDao deviceDao = sqlSession.getMapper(DeviceDao.class);
        List<Device> devices = deviceDao.listDevicesByUserId(userId);
        sqlSession.close();
        return devices;
    }

    @Override
    public List<Device> listAllDevices() {
        SqlSession sqlSession = DbUtil.getSqlSession();
        DeviceDao deviceDao = sqlSession.getMapper(DeviceDao.class);
        List<Device> devices = deviceDao.listAllDevices();
        sqlSession.close();
        return devices;
    }

    @Override
    public boolean addDevice(Device device) {
        SqlSession sqlSession = DbUtil.getSqlSession();
        DeviceDao deviceDao = sqlSession.getMapper(DeviceDao.class);
        int i = deviceDao.insertDevice(device);
        sqlSession.close();
        return i > 0;
    }

    @Override
    public boolean updateDevice(Device device) {
        SqlSession sqlSession = DbUtil.getSqlSession();
        DeviceDao deviceDao = sqlSession.getMapper(DeviceDao.class);
        int i = deviceDao.updateDevice(device);
        sqlSession.close();
        return i > 0;
    }

    @Override
    public boolean deleteDevice(int deviceId) {
        SqlSession sqlSession = DbUtil.getSqlSession();
        DeviceDao deviceDao = sqlSession.getMapper(DeviceDao.class);
        int i = deviceDao.deleteDevice(deviceId);
        sqlSession.close();
        return i > 0;
    }

    @Override
    public List<Rule> listAllRules() {
        SqlSession sqlSession = DbUtil.getSqlSession();
        RuleDao ruleDao = sqlSession.getMapper(RuleDao.class);
        List<Rule> rules = ruleDao.listAllRules();
        sqlSession.close();
        return rules;
    }

    @Override
    public boolean addRule(Rule rule) {
        SqlSession sqlSession = DbUtil.getSqlSession();
        RuleDao ruleDao = sqlSession.getMapper(RuleDao.class);
        int i = ruleDao.insertRule(rule);
        sqlSession.close();
        return i > 0;
    }

    @Override
    public boolean updateRule(Rule rule) {
        SqlSession sqlSession = DbUtil.getSqlSession();
        RuleDao ruleDao = sqlSession.getMapper(RuleDao.class);
        int i = ruleDao.updateRule(rule);
        sqlSession.close();
        return i > 0;
    }

    @Override
    public boolean deleteRule(int ruleId) {
        SqlSession sqlSession = DbUtil.getSqlSession();
        RuleDao ruleDao = sqlSession.getMapper(RuleDao.class);
        int i = ruleDao.deleteRule(ruleId);
        sqlSession.close();
        return i > 0;
    }
}