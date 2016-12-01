import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

/**
 * 内容摘要 ：入口
 * 创建人　 ：陈佳慧
 * 创建日期 ：2016/12/1
 */
public class Application {
    private static String url = "http://cq.189.cn/mall/sales/num/list/new";//189.cn 号码获取接口
    private static String head = "189";//号段 133,153,189,177
    private static int groupMax = 20;//获取组数
    private static boolean not4 = true;//带4减分
    private static boolean end6 = true;//带6加分
    private static boolean end8 = true;//带8加分
    private static int show=1000;//显示至少大于show分

    private static Map<String, Object> map = new HashMap<>();

    public static void main(String... args) {
        map.put("numberRepo", -5);
        map.put("area", "0000");
        map.put("head", head);
        map.put("stored_charges_limit", "no_limit");
        sortPhone(getAllPhone());
    }

    private static void sortPhone(Set<String> phones) {
        Map<Integer, String> map = new TreeMap<>();
        for (String phone : phones) {
            int l = 0;
            for (int i = 0; i < phone.length(); i++) {
                for (int j = 0; j < phone.length(); j++) {
                    if (i == j) continue;
                    if (phone.charAt(i) == phone.charAt(j)) l += 10000;
                }
            }
            for (int i = 1; i < phone.length(); i++) {
                if (phone.charAt(i - 1) == phone.charAt(i)) l += 20000;
            }
            for (int i = 1; i < phone.length(); i++) {
                if (phone.charAt(i - 1) == phone.charAt(i) - 1 || phone.charAt(i - 1) == phone.charAt(i) + 1)
                    l += 10000;
            }
            for (int i = 2; i < phone.length(); i++) {
                if (phone.charAt(i - 2) == phone.charAt(i - 1) && phone.charAt(i - 1) == phone.charAt(i)) l += 30000;
            }
            for (int i = 3; i < phone.length(); i++) {
                switch (phone.charAt(i)) {
                    case '4':
                        l -= 10000;
                        if (not4) l -= l ^ 2;
                        break;
                    case '6':
                        if(end6) l += l ^ 2;
                    case '8':
                        if(end8) l += l ^ 2;
                        l += 20000;
                }
            }
            if (phone.charAt(3) == '1' && phone.endsWith("1")) l += 30000;
            if (phone.endsWith("8")) l += 30000;
            if (phone.endsWith("88")) l += 50000;
            if (phone.endsWith("6")) l += 30000;
            if (phone.endsWith("66")) l += 50000;
            while (map.get(l) != null) l++;
            map.put(l, phone);
        }
        map.forEach((i, s) -> {
            if(i<show) return;
            System.out.println(i / 10000 + "\t" + s);
        });
    }

    private static Set<String> getAllPhone() {
        Set<String> phoneResultSet = new HashSet<>();
        System.out.print("接受189.cn数据");
        for (int i = 0; i < groupMax; i++) {
            JSONArray array = getJSON(i + 1);
            System.out.print(".");
            for (Object anArray : array) {
                JSONObject object = (JSONObject) anArray;
                phoneResultSet.add(object.getString("tele_no"));
            }
        }
        System.out.println();
        return phoneResultSet;
    }

    private static JSONArray getJSON(int page) {
        map.put("groupNo", page);
        String res = ClientUtil.sendPost(url, map);
        return JSONArray.parseArray(res);
    }
}
