package com.redis.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class ZKOperateMain {


    public void run(String[] args) {
        Options options = new Options();
        Option pathOption = new Option("z", "zkPath", true,
                "请输入zk地址，例：192.168.100.106:2181");
        pathOption.setRequired(true);
        options.addOption(pathOption);

        Option option = new Option("o", "optional", true, "操作类型（getAcl\\setAcl）");
        option.setRequired(true);
        options.addOption(option);

        options.addOption("v", "value", true, "新增acl,如：ip:192.168.100.106:rw,ip:192.168.100.107:rw,");
        CommandLineParser parser = new DefaultParser();
        String zkPath;
        String optional;
        String value;
        try {
            CommandLine cli = parser.parse(options, args);
            zkPath = cli.getOptionValue("z");
            optional = cli.getOptionValue("o");
            value = cli.getOptionValue("v");
            log.info("-m " + zkPath + " -o " + optional + " -k " + (StringUtils.hasLength(value) ? value : "null"));
        } catch (ParseException e) {
            log.error("解析入参数据出现错误");
            e.printStackTrace();
            return;
        }
        if ("getAcl".equals(optional)) {
            ZookeeperUtil.getAcl(zkPath);
            return;
        }

        if ("setAcl".equals(optional)) {
            ZookeeperUtil.getAcl(zkPath);
            ZookeeperUtil.setAcl(zkPath, value);
            return;
        }


    }
}
