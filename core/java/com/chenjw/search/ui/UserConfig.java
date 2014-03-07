package com.chenjw.search.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSON;

public class UserConfig {

    public static final UserConfig INSTANCE = new UserConfig();
    static {
        INSTANCE.init();
    }
    public Map<String, String>     configs;

    private File                   file;

    public void save() {
        try {
            FileUtils.writeStringToFile(file, JSON.toJSONString(configs), "GBK");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void init() {
        try {
            String setup = null;
            if (EnvUtils.getEnvMode() == EnvUtils.ENV_ECLIPSE) {
                setup = "config/user.ini";
            } else {
                setup = "user.ini";
            }
            file = new File(setup);
            if (file.exists()) {
                configs = JSON.parseObject(FileUtils.readFileToString(file, "GBK"), Map.class);
            } else {
                configs = new HashMap<String, String>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String get(String key) {
        return configs.get(key);
    }

    public void put(String key, String value) {
        configs.put(key, value);
    }
}
