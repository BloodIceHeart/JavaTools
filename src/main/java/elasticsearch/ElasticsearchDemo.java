package elasticsearch;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * ES测试类,ElasticsearchDemo
 */
public class ElasticsearchDemo {
    private final ElasticSearchConfig elasticSearchConfig = new ElasticSearchConfig();
    private final RestHighLevelClient client = elasticSearchConfig.getClient();

    public static void main(String[] args) throws Exception {
        new ElasticsearchDemo().indexData();
    }
    /**
     * 测试ES配置类是否被spring容器管理
     */
    public void contextLoads() {
        System.out.println(elasticSearchConfig);
    }

    /**
     * 新增一个文档
     */
    public void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("wlp-index");//参数为索引名称
        indexRequest.id("101");
        UserDto user = new UserDto().setFirst_name("张").setLast_name("三").setAge(30).setAbout("普通人").setInterests(new String[]{"游泳", "学习"});
        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString, XContentType.JSON);//一定要指定数据类型
        /**
         * ElasticSearchConfig.COMMON_OPTIONS:从配置类中获取请求项,默认为RequestOptions.DEFAULT
         */
        //执行操作
        IndexResponse index = client.index(indexRequest, ElasticSearchConfig.COMMON_OPTIONS);
        //提取响应数据
        System.out.println(index);
    }

    /**
     * 批量插入文档
     */
    public void testBulkIndexDocument() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        List<UserDto> documents = new ArrayList<>();
        documents.add(new UserDto().setFirst_name("张").setLast_name("三").setAge(30).setAbout("普通人001").setInterests(new String[]{"游泳", "学习"}));
        documents.add(new UserDto().setFirst_name("李").setLast_name("四").setAge(30).setAbout("普通人002").setInterests(new String[]{"吃饭", "睡觉"}));
        documents.add(new UserDto().setFirst_name("王").setLast_name("五").setAge(30).setAbout("普通人003").setInterests(new String[]{"读书", "唱歌"}));
        documents.forEach(doc -> {
            //可以通过IndexRequest().id()指定主键,不指定则自增
            bulkRequest.add(new IndexRequest("wlp-index").source(JSON.toJSONString(doc), XContentType.JSON));
        });
        BulkResponse bulk = client.bulk(bulkRequest, ElasticSearchConfig.COMMON_OPTIONS);
        //bulk.hasFailures():是否失败
        System.out.println(bulk.hasFailures());
    }

    /**
     * 根据id查询一个文档
     */
    public void getDocumentById() throws IOException {
        GetRequest getRequest = new GetRequest("wlp-index", "100");
        GetResponse documentFields = client.get(getRequest, ElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(documentFields);
    }

    /**
     * 条件查询(精确,不支持模糊)
     */
    public void searchData() throws IOException {
        SearchRequest searchRequest = new SearchRequest("wlp-index");
        //searchRequest.indices("wlp-index");//同时也可以这样指定查询的索引
        /**
         * 条件查询 如果查询条件为中文 需要在属性添加 .keyword,不加就查不到
         * 例如： QueryBuilders.termQuery("name.keyword","张三");
         * termQuery:精确查询 matchQuery:模糊查询
         */
        TermQueryBuilder queryBuilder = QueryBuilders.termQuery("about.keyword", "普通人001");
        //构件搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //分页 size:每页几条(默认10) from从第几条开始查
        searchSourceBuilder.size(20);
        searchSourceBuilder.from(0);
        searchSourceBuilder.query(queryBuilder);
        //设置查询超时时间
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
        //遍历输出
        SearchHit[] hits = searchResponse.getHits().getHits();
        Arrays.stream(hits).forEach(documentFields -> {
            System.out.println(documentFields.getSourceAsMap());
        });
    }

    /**
     * 模糊查询
     */
    public void testFuzzyQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("wlp-index");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("about", "人之初"));
        //超时时间
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
        SearchHit[] hits = response.getHits().getHits();
        Arrays.stream(hits).forEach(documentFields -> {
            System.out.println(documentFields.getSourceAsMap());
        });
    }

    /**
     * 测试判断文档是否存在
     */
    public void testIsDocumentExists() throws IOException {
        GetRequest request = new GetRequest("wlp-index", "101");
        //不获取source上下文
        request.fetchSourceContext(new FetchSourceContext(false));
        boolean exists = client.exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    /**
     * 根据主键更改文档信息
     */
    public void updateDocumentById() throws IOException {
        UpdateRequest request = new UpdateRequest("wlp-index", "101");
        //没有设置的属性,es不会更新
        UserDto user = new UserDto().setFirst_name("TEST").setLast_name("UPDATE");
        request.doc(JSON.toJSONString(user), XContentType.JSON);
        UpdateResponse update = client.update(request, ElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(update);
    }


    /**
     * 根据id删除一个文档
     */
    public void deleteDocumentById() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("wlp-index", "100");
        DeleteResponse delete = client.delete(deleteRequest, ElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(delete);
    }

    //高亮搜索
    public void  testHighlightSearch() throws IOException {
        //搜索条件
        SearchRequest searchRequest = new SearchRequest("wlp-index");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder .query(QueryBuilders.matchQuery("about.keyword","普通")) //精确匹配
                .timeout(new TimeValue(60, TimeUnit.SECONDS))
                .highlighter(new HighlightBuilder()
                        .field("about.keyword") //搜索字段
                        .requireFieldMatch(false) // 只需一个高亮
                        .preTags("<span style='coler:red'>")
                        .postTags("</span>"));
        //执行搜索
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
        //解析结果
        List<Object> queryList = new LinkedList<>();
        for (SearchHit documentFields : search.getHits().getHits()) {
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();//原来的结果
            HighlightField field = documentFields.getHighlightFields().get("name.keyword");
            //解析高亮的字段，将原来的字段替换成高亮字段
            Text[] texts = field.fragments();
            String n_text ="";
            for (Text text : texts) {
                n_text+= text;
            }
            sourceAsMap.put("name",n_text);//高亮字段替换原来的内容
            queryList.add(sourceAsMap);

        }
        queryList.forEach(System.out::println);
    }
}
