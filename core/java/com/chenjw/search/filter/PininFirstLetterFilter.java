package com.chenjw.search.filter;

import java.io.IOException;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

public final class PininFirstLetterFilter extends TokenFilter {

    private final CharTermAttribute termAtt = (CharTermAttribute) addAttribute(CharTermAttribute.class);

    public PininFirstLetterFilter(Version matchVersion, TokenStream in) {
        super(in);
    }

    public final boolean incrementToken() throws IOException {
        if (input.incrementToken()) {
            char[] buffer = termAtt.toString().toCharArray();
            if (buffer == null || buffer.length == 0) {
                return true;
            }
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < buffer.length; i++) {
                char c = buffer[i];
                HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
                format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
                format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
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
            char[] chars = sb.toString().toCharArray();
            termAtt.resizeBuffer(chars.length);
            termAtt.setLength(chars.length);
            buffer=termAtt.buffer();
            for (int i = 0; i < chars.length; i++) {
                buffer[i] = chars[i];
            }
            return true;
        } else {
            return false;
        }
    }

}
