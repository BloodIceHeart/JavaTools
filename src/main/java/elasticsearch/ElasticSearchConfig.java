package elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * ES配置类,给容器中注入一个RestHighLevelClient
 */
public class ElasticSearchConfig {
    private String hostName = "172.9.156.201";
    private int port = 9200;

    public static final RequestOptions COMMON_OPTIONS;

    /**
     * 统一设置请求项
     */
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        COMMON_OPTIONS = builder.build();
    }

    /**
     * 获取ES客户端
     */
    public RestHighLevelClient getClient() {
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost(hostName, port, "http"))
        );
    }
}
