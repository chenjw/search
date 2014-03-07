/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.chenjw.search.ui;

import java.util.HashMap;
import java.util.Map;

public class SetupConfig {
    public static final String      KEY_TEMPLATE_PATH = "templatePath";
    
    public static final String      KEY_DOWNLOAD_PATH = "downloadPath";

    public static final SetupConfig INSTANCE          = new SetupConfig();
    static {
        INSTANCE.init();
    }
    public Map<String, String>      configs           = new HashMap<String, String>();


    public void init() {
        if (EnvUtils.getEnvMode() == EnvUtils.ENV_ECLIPSE) {
            configs.put(KEY_TEMPLATE_PATH, "config/script");
        } else {
            configs.put(KEY_TEMPLATE_PATH, "script");
        }
        configs.put(KEY_DOWNLOAD_PATH, "download");
        //        try {
        //            String setup = null;
        //            if (EnvUtils.getEnvMode() == EnvUtils.ENV_ECLIPSE) {
        //                setup = "resources/setup.ini";
        //            } else {
        //                setup = "setup.ini";
        //            }
        //            Iterator<String> iterator = FileUtils.lineIterator(new File(setup), "GBK");
        //            while (iterator.hasNext()) {
        //                String line = iterator.next();
        //                if (line.startsWith("#")) {
        //                    continue;
        //                }
        //                String key = StringUtils.substringBefore(line, "=").trim();
        //                String value = StringUtils.substringAfter(line, "=").trim();
        //                configs.put(key, value);
        //            }
        //        } catch (IOException e) {
        //           e.printStackTrace();
        //        }
    }

    public String get(String key) {
        return configs.get(key);
    }
}
