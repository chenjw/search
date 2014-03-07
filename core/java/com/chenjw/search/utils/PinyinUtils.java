package com.chenjw.search.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.commons.lang.StringUtils;

public class PinyinUtils {
    private static HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
    static {
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

    }

    public static String toPinyin(String text) {
        if (text == null || text.length() == 0) {
            return text;
        }
        char[] chars = text.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            String[] pintin = null;
            try {
                pintin = PinyinHelper.toHanyuPinyinStringArray(c, format);
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
            if (pintin != null && pintin.length != 0 && !StringUtils.isBlank(pintin[0])) {
                sb.append(pintin[0]);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String toPinyinFirstLetter(String text) {
        if (text == null || text.length() == 0) {
            return text;
        }
        char[] chars = text.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            String[] pintin = null;
            try {
                pintin = PinyinHelper.toHanyuPinyinStringArray(c, format);
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
            if (pintin != null && pintin.length != 0 && !StringUtils.isBlank(pintin[0])) {
                sb.append(pintin[0].charAt(0));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
