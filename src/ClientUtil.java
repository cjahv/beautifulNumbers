import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * 内容摘要 ：网络连接辅助类
 * 创建人　 ：陈佳慧
 * 创建日期 ：2016年01月21日
 */
public class ClientUtil {

    private static BufferedReader in;
    private static PrintWriter out;

    private static int connectTimeout = 10000;
    private static int readTimeout = 10000;
    private static boolean MOVED_MODEL = true;

    public static void setConnectTimeout(int timeout) {
        connectTimeout = timeout;
    }

    public static void setReadTimeout(int timeout) {
        readTimeout = timeout;
    }

    public static void notMoved() {
        MOVED_MODEL = false;
    }

    public static String sendGet(String url, Map<String, String> map) {
        return sendGet(url + "?" + StringUtil.paramMap(map));
    }

    public static String sendGet(String url) {
        try {
            HttpURLConnection conn = getConnection(url);
            conn.connect();
            return getResult(conn);
        } catch (IOException e) {
            throw new RuntimeException("connect fail!", e);
        }
    }

    public static String sendPost(String url, Map<String, Object> map) {
        return sendPost(url, StringUtil.paramMap(map));
    }

    public static String sendPost(String url, String param) {
        HttpURLConnection conn = getConnection(url);
        doPost(conn, param);
        return getResult(conn);
    }

    public static byte[] download(String url) {
        HttpURLConnection conn = getConnection(url);
        try {
            InputStream inputStream = conn.getInputStream();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            inputStream.close();
            return outStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("get input stream fail!!");
        }
    }

    private static HttpURLConnection getConnection(String url) {
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            conn.setRequestProperty("Accept-Language", "zh-cn,zh;q=0.5");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Cookie","JSESSIONID=220A6C7432F18ACF223C1588530C6070; dqmhIpCityInfos=%E9%87%8D%E5%BA%86%E5%B8%82+%E7%94%B5%E4%BF%A1; flow_analysis_key=2e7be93ee71b7a476fd7581a64e1c6da; lvid=4570d8f2629011de9b42154c47c664f3; nvid=1; SHOPID_COOKIEID=10004; trkHmCitycode=0; trkHmCoords=0; trkHmPageName=0; recentview=M1317C1008-1073S20160929108161&; ecsc_session_uuid=\"ebf6a43213b84c7fb734ba6ec3a4e5bb:1480596246797\"; _ecsc_area_key=1088; s_cc=true; trkHmClickCoords=600%2C527%2C11922; s_fid=23461157F4788009-387C57CF7FC2BEFA; cityCode=cq; loginStatus=non-logined; trkId=C9068E6F-B608-4C8F-956E-6F9DC4E15E54; s_sq=eshipeship-189-all%3D%2526pid%253D%25252Fmall%25252Fgoods%25252FM1317C1008-1073S20160929108161%2526pidt%253D1%2526oid%253Djavascript%25253A%2526ot%253DA");
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            return conn;
        } catch (IOException e) {
            throw new RuntimeException("open connection fail!", e);
        }
    }

    private static void doPost(HttpURLConnection conn, String param) {
        try {
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException("do post fail!", e);
        }
    }

    private static String getResult(HttpURLConnection conn) {
        try {
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else if (conn.getResponseCode() / 100 == 3) {
                String location = conn.getHeaderField("Location");
                if (location != null && location.length() > 0) {
                    if (!MOVED_MODEL) return location;
                    else return sendGet(location);
                }
                throw new IOException("error! not exist location!");
            } else {
                in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            closeStream();
            return result.toString();
        } catch (IOException e) {
            throw new RuntimeException("get result fail!", e);
        }
    }

    private static void closeStream() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
        } catch (IOException e) {
            throw new RuntimeException("close stream fail!", e);
        }
    }
}
