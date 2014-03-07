/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.chenjw.search.ui;

import com.chenjw.dynacomponent.scanner.EnvAwareScanner;



public class CustomScanner extends EnvAwareScanner {

    /** 
     * @see com.alipay.mobilecommon.dynamiccomponent.scanner.EnvAwareScanner#checkFolderPath(java.lang.String)
     */
    @Override
    protected String checkFolderPath(String folderPath) {
        return SetupConfig.INSTANCE.get(SetupConfig.KEY_TEMPLATE_PATH);
    }

    /** 
     * @see com.alipay.mobilecommon.dynamiccomponent.scanner.EnvAwareScanner#checkInPreEnv()
     */
    protected boolean checkInPreEnv() {
        return true;
    }

}
