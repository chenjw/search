/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.chenjw.search.ui;

import java.io.File;

/**
 * 用来判断当前环境的类别
 * 
 * @author junwen.chenjw
 * @version $Id: EnvUtils.java, v 0.1 2013年9月1日 下午9:23:23 junwen.chenjw Exp $
 */
public class EnvUtils {


    /** eclipse开发环境 */
    public static final int ENV_ECLIPSE = 1;

    /** 工具启动的环境 */
    public static final int ENV_TOOLS   = 2;

    /** 当前环境 */
    private static int      envMode;
    static {
        // 生产环境或测试环境
        if (new File(".project").exists()) {
            envMode = ENV_ECLIPSE;
        } else {
            envMode = ENV_TOOLS;
        }
    }

    /**
     * 获得当前环境
     * @return
     */
    public static int getEnvMode() {
        return envMode;
    }
}
