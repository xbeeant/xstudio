package io.github.xbeeant.http;

import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicStatusLine;

/**
 * @author xiaobiao
 * @version 2020/2/12
 */
@SuppressWarnings("all")
public class ClientResponse {
    /**
     * 返回内容
     */
    private String content;
    /**
     * 请求对象
     */
    private CloseableHttpResponse origin;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取statusCode
     *
     * @return {@link StatusLine}
     */
    public int statusCode() {
        if (null == this.getOrigin()) {
            return 500;
        }
        return this.getOrigin().getStatusLine().getStatusCode();
    }

    /**
     * 获取statusLine
     *
     * @return {@link StatusLine}
     */
    public StatusLine statusLine() {
        if (null == this.getOrigin()) {
            return new BasicStatusLine(
                    HttpVersion.HTTP_1_1,
                    500,
                    "origin为空");
        }
        return this.getOrigin().getStatusLine();
    }

    public CloseableHttpResponse getOrigin() {
        return origin;
    }

    public void setOrigin(CloseableHttpResponse origin) {
        this.origin = origin;
    }
}
