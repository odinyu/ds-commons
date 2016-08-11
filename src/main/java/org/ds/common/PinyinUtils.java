package org.ds.common;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * <p>
 * 拼音工具类
 * </p>
 *
 * @author Daniel
 */
public class PinyinUtils {
    /**
     * 获取汉字全拼
     *
     * @param str 汉字字符串
     * @return 汉字字符串全拼
     */
    public static String toPingYin(String str) {
        char[] chars = str.toCharArray();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        String py = "";
        try {
            for (char c : chars) {
                // 判断是否为汉字字符
                if (RegexUtils.isChinese(Character.toString(c))) {
                    py += PinyinHelper.toHanyuPinyinStringArray(c, format)[0];
                } else {
                    py += Character.toString(c);
                }
            }
            return py;
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return py;
    }

    /**
     * 获取中文字符串拼音首字母
     *
     * @param str 字符串
     * @return 中文拼音首字母字符串
     */
    public static String toPinYinHeadChar(String str) {
        String result = "";
        for (int i = 0; i < str.length(); i++) {
            char word = str.charAt(i);
            String[] array = PinyinHelper.toHanyuPinyinStringArray(word);
            if (array != null) result += array[0].charAt(0);
            else result += word;
        }
        return result;
    }
}
