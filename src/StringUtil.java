import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 内容摘要 ：字符串工具类
 * 创建人　 ：陈佳慧
 * 创建日期 ：2016年03月09日
 */
@SuppressWarnings("WeakerAccess")
public class StringUtil {
    /**
     * 将字符串首字母大写
     */
    public static String updateInitial(String str) {
        return updateInitial(str, 0);
    }

    /**
     * 将字符串指定位置字符改为大写
     *
     * @param str   操作字符串
     * @param index 位置
     */
    public static String updateInitial(String str, int index) {
        char[] cs = str.toCharArray();
        if (cs[index] > 96) cs[index] -= 32;
        return String.valueOf(cs);
    }

    /**
     * 在指定区间替换字符
     *
     * @param str     被替换的字符串
     * @param oldChar 搜索字符
     * @param newChar 替换字符
     * @param start   开始位置 从-1开始
     * @param end     结束位置
     * @return 新的字符串 没有找到返回原始字符串
     */
    public static String replace(String str, char oldChar, char newChar, int start, int end) {
        if (oldChar != newChar) {
            int len = end > str.length() ? str.length() : end;
            int i = start < -1 ? -1 : start;
            char[] val = str.toCharArray(); /* avoid getfield opcode */

            while (++i < len) {
                if (val[i] == oldChar) {
                    break;
                }
            }
            if (i < len) {
                char buf[] = new char[len];
                System.arraycopy(val, 0, buf, 0, i);
                while (i < len) {
                    char c = val[i];
                    buf[i] = (c == oldChar) ? newChar : c;
                    i++;
                }
                return new String(buf);
            }
        }
        return str;
    }

    /**
     * 按ascii码去除字符串两段
     *
     * @param s 源字符串
     * @param c 需要去除的ascii码
     * @return 去掉两段后的字符串
     */
    public static String trim(String s, int c) {
        int len = s.length();
        int st = 0;
        char[] val = s.toCharArray();

        while ((st < len) && (val[st] == c)) {
            st++;
        }
        while ((st < len) && (val[len - 1] == c)) {
            len--;
        }
        return ((st > 0) || (len < s.length())) ? s.substring(st, len) : s;
    }

    /**
     * map参数组装
     *
     * @param map 参数
     * @return param参数
     */
    public static String paramMap(Map map) {
        StringBuilder buffer = new StringBuilder();
        for (Object key : map.keySet()) {
            Object val = map.get(key);
            if (val instanceof Object[]) {
                for (Object val1 : (Object[]) val) {
                    buffer.append(key).append("=").append(val1).append('&');
                }
            } else {
                buffer.append(key).append("=").append(val).append('&');
            }
        }
        if (buffer.length() > 0 && buffer.charAt(buffer.length() - 1) == '&')
            buffer.deleteCharAt(buffer.length() - 1);
        return buffer.toString();
    }

    /**
     * 替换
     *
     * @param prefix 前缀
     * @param suffix 后缀
     * @param target 目标支付串
     * @param map    替换map
     */
    @SuppressWarnings("Duplicates")
    public static String replace(String prefix, String suffix, String target, Map<String, String> map) {
        StringBuilder builder = new StringBuilder();
        char prefixChar = prefix.charAt(0);
        boolean prefixT = false;
        for (int i = 0; i < target.length(); i++) {
            if (target.charAt(i) == prefixChar) {
                prefixT = true;
                for (int j = 1; j < prefix.length(); j++) {
                    if (target.charAt(i + j) != prefix.charAt(j)) {
                        prefixT = false;
                        break;
                    }
                }
            }
            if (prefixT) {
                prefixT = false;
                StringBuilder keyBuilder = new StringBuilder();
                char suffixChar = suffix.charAt(0);
                boolean suffixT = false;
                for (int j = i + prefix.length(); j < target.length(); j++) {
                    if (target.charAt(j) == suffixChar) {
                        suffixT = true;
                        for (int k = 1; k < suffix.length(); k++) {
                            if (target.charAt(j + k) != suffix.charAt(k)) {
                                suffixT = false;
                                break;
                            }
                        }
                    }
                    if (suffixT) {
                        builder.append(map.get(keyBuilder.toString()));
                        i += prefix.length() + keyBuilder.length() + suffix.length() - 1;
                        break;
                    } else {
                        keyBuilder.append(target.charAt(j));
                    }
                }
            } else {
                builder.append(target.charAt(i));
            }
        }
        return builder.toString();
    }

    public static String subStrChar(String target, int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0, j = 0; i < target.length(); i++) {
            builder.append(target.charAt(i));
            if (target.charAt(i) > 255) {
                j += 2;
                if (j == length) break;
                if (j + 1 == length && target.charAt(i + 1) > 255) break;
            } else {
                if (++j == length) break;
                if (j + 1 == length && target.charAt(i + 1) > 255) break;
            }
        }
        return builder.toString();
    }
}
