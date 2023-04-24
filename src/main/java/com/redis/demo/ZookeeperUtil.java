package com.redis.demo;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：wangshimin
 * @date ：Created in 2023/4/24 10:59
 */
public class ZookeeperUtil {
    private static Logger log = LoggerFactory.getLogger(ZookeeperUtil.class);
    private static Logger data = LoggerFactory.getLogger("DATA");

    private static String aclString(int p) {
        if (p >= ZooDefs.Perms.ALL) {
            return "cdrwa";
        }
        StringBuffer stringBuffer = new StringBuffer();
        if (p >= ZooDefs.Perms.ADMIN) {
            stringBuffer.append("a");
            p = p - ZooDefs.Perms.ADMIN;
        }
        if (p >= ZooDefs.Perms.DELETE) {
            stringBuffer.append("d");
            p = p - ZooDefs.Perms.DELETE;
        }
        if (p >= ZooDefs.Perms.CREATE) {
            stringBuffer.append("c");
            p = p - ZooDefs.Perms.CREATE;
        }
        if (p >= ZooDefs.Perms.WRITE) {
            stringBuffer.append("w");
            p = p - ZooDefs.Perms.WRITE;
        }
        if (p >= ZooDefs.Perms.READ) {
            stringBuffer.append("r");
        }
        return stringBuffer.toString();
    }

    /**
     * 列出指定path下所有孩子
     */
    private static void lsr(String path, ZooKeeper zk, List<String> list) {
        if (path.split("/").length >= 3) {
            return;
        }
        List<String> newList = null;
        try {
            newList = zk.getChildren(path, null);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //判断是否有子节点
        if (newList.isEmpty() || newList == null) {
            return;
        }
        for (String s : newList) {
            //判断是否为根目录
            if (path.equals("/")) {
                list.add(path + s);
                lsr(path + s, zk, list);
            } else {
                list.add(path + "/" + s);
                lsr(path + "/" + s, zk, list);
            }
        }
    }

    public static void getAcl(String connectPath) {
        List<String> result = new ArrayList<>();
        ZooKeeper zooKeeper = null;
        try {
            zooKeeper = new ZooKeeper(connectPath, 30000, event -> log.info(" receive event : " + event.getType().name()));
            String path = "/";
            List<String> list = new ArrayList<>();
            list.add(path);
            lsr(path, zooKeeper, list);
            for (String newPath : list) {
                List<ACL> acls = zooKeeper.getACL(newPath, null);
                if (CollectionUtils.isEmpty(acls)) {
                    log.info("getAcl " + newPath + " null");
                    continue;
                }
                StringBuffer stringBuffer = new StringBuffer();
                for (ACL acl : acls) {
                    stringBuffer.append(acl.getId().getScheme()).append(":").append(acl.getId().getId()).append(":").append(aclString(acl.getPerms())).append(",");
                }
                result.add("getAcl " + newPath + " " + stringBuffer.substring(0, stringBuffer.length() - 1));
                log.info("getAcl " + newPath + " " + stringBuffer.substring(0, stringBuffer.length() - 1));
            }
        } catch (IOException e) {
            log.error("查询ip异常");
            log.error(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } finally {
            if (zooKeeper != null) {
                try {
                    zooKeeper.close();
                } catch (InterruptedException e) {
                    log.error("zk关闭失败");
                }
            }
        }
        if (!CollectionUtils.isEmpty(result)) {
            data.info("================读取权限如下=================");
            for (String res : result) {
                data.info(res);
            }
            data.info("================读取权限完成=================");
        }
    }

    public static void setAcl(String zkPath, String value) {
        List<String> result = new ArrayList<>();
        ZooKeeper zooKeeper = null;
        try {
            zooKeeper = new ZooKeeper(zkPath, 30000, event -> log.info(" receive event : " + event.getType().name()));
            String path = "/";
            List<String> list = new ArrayList<>();
            list.add(path);
            lsr(path, zooKeeper, list);
            for (String newPath : list) {
                List<ACL> acls = zooKeeper.getACL(newPath, null);
                if (CollectionUtils.isEmpty(acls)) {
                    log.info("getAcl " + newPath + " null");
                    continue;
                }
                StringBuffer stringBuffer = new StringBuffer();
                for (ACL acl : acls) {
                    stringBuffer.append(acl.getId().getScheme()).append(":").append(acl.getId().getId()).append(":").append(aclString(acl.getPerms())).append(",");
                }
                stringBuffer.append(value);
                log.info("setAcl " + newPath + " " + stringBuffer.substring(0, stringBuffer.length()));
                result.add("setAcl " + newPath + " " + stringBuffer.substring(0, stringBuffer.length()));
            }
        } catch (IOException e) {
            log.error("查询ip异常");
            log.error(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } finally {
            if (zooKeeper != null) {
                try {
                    zooKeeper.close();
                } catch (InterruptedException e) {
                    log.error("zk关闭失败");
                }
            }
        }
        if (!CollectionUtils.isEmpty(result)) {
            data.info("================写入权限如下=================");
            for (String res : result) {
                data.info(res);
            }
            data.info("================写入权限结束=================");
        }
    }
}
