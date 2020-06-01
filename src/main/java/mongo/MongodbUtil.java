package mongo;

import com.alibaba.fastjson.JSON;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.thoughtworks.xstream.XStream;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.mongodb.client.model.Filters.eq;

/**
 * MongoDb操作工具类
 * 
 * @author seamus
 */
public class MongodbUtil {

    /**
     * 
     */
    private static XStream xStream = new XStream();

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private String serverHost; // = "9.23.28.22";
    private int serverPort;// = 27017;
    private String database;// = "ncs";
    private String password;// = "123456";
    private String userName;// = "ncs";
    private int connectionsPerHost;// = "ncs";
    private int threadsAllowedToBlockForConnectionMultiplier;// = "ncs";
    private int connectTimeout;// = "ncs";
    private int maxWaitTime;// = "ncs";
    private Boolean socketKeepAlive;// = "ncs";
    private int socketTimeout;// = "ncs";
    private String multiServerHosts;// = "ncs";
   
    /**
     * 线程池
     */
    private static ExecutorService executorService = Executors.newFixedThreadPool(3);

    {
        Properties pro = new Properties();
        try {
            pro.load(this.getClass().getClassLoader().getResourceAsStream("mongodb.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 此属性是人寿方有的
        serverHost = pro.getProperty("mongodb.serverHost");
        serverPort = Integer.parseInt(pro.getProperty("mongodb.serverPort"));
        database = pro.getProperty("mongodb.database");
        userName = pro.getProperty("mongodb.userName");
        password = pro.getProperty("mongodb.password");

        connectionsPerHost = Integer.parseInt(pro.getProperty("mongodb.connectionsPerHost"));
        threadsAllowedToBlockForConnectionMultiplier = Integer.parseInt(pro.getProperty("mongodb.threadsAllowedToBlockForConnectionMultiplier"));
        connectTimeout = Integer.parseInt(pro.getProperty("mongodb.connectTimeout"));
        maxWaitTime = Integer.parseInt(pro.getProperty("mongodb.maxWaitTime"));
        socketKeepAlive = Boolean.parseBoolean(pro.getProperty("mongodb.socketKeepAlive"));
        socketTimeout = Integer.parseInt(pro.getProperty("mongodb.socketTimeout"));
        multiServerHosts = pro.getProperty("mongodb.multiServerHosts");
    }

    public static final String PORT_DEMILITER = ":";
    public static final String SERVER_DEMILITER = ",";
    private static String hashKey = "_id";
    private static MongoClient mg = null;
    /**
     * 
     */
    private static ConcurrentHashMap<String, MongoDatabase> dbs = new ConcurrentHashMap<String, MongoDatabase>();
    /**
     * 
     */
    private static ConcurrentHashMap<String, MongoCollection<Document>> cols = new ConcurrentHashMap<String, MongoCollection<Document>>();
    /**
     * 
     */
    private static volatile MongodbUtil singleton = null;

    /**
     * private static volatile boolean isShard = true;
     */

    /**
     * 
     * @Description: TODO
     * @return MongodbUtil
     */
    public static MongodbUtil getInstance() {
        if (singleton == null) {
            synchronized (MongodbUtil.class) {
                if (singleton == null) {
                    singleton = new MongodbUtil();
                }
            }
            singleton = new MongodbUtil();
        }
        return singleton;
    }

    /**
     * 
     * @Description: TODO
     */
    private MongodbUtil() {

        if (mg == null) {
            try {
                MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
                builder.connectionsPerHost(connectionsPerHost); // 与目标数据库能够建立的最大连接数
                builder.threadsAllowedToBlockForConnectionMultiplier(threadsAllowedToBlockForConnectionMultiplier);// 当所有的连接都在使用中，可以等待的线程数
                builder.maxWaitTime(maxWaitTime);// 获取数据库连接时的最大等待时间(毫秒)
                builder.connectTimeout(connectTimeout); // 与数据库建立连接的超时时间(毫秒)
                builder.socketKeepAlive(socketKeepAlive);
                builder.socketTimeout(socketTimeout); // Socket读写超时时间()
                MongoCredential credential = MongoCredential.createCredential(userName, database,
                        password.toCharArray());

                if (multiServerHosts != null && multiServerHosts.length() > 0) {
                    List<ServerAddress> seeds = new ArrayList<ServerAddress>();
                    String[] servers = multiServerHosts.split(SERVER_DEMILITER);
                    for (String server : servers) {
                        String host = server;
                        int port = this.serverPort;
                        if (server.indexOf(PORT_DEMILITER) > 0) {
                            host = server.substring(0, server.indexOf(PORT_DEMILITER));
                            port = new Integer(server.substring(server.indexOf(PORT_DEMILITER) + 1)).intValue();
                        }
                        seeds.add(new ServerAddress(host, port));
                    }
                    logger.info("connect to Mongo Replica Set：{}", multiServerHosts);
                    mg = new MongoClient(seeds, Arrays.asList(credential), builder.build());
                } else {
                    logger.info("connect to Mongo server : {}", serverHost);
                    mg = new MongoClient(new ServerAddress(serverHost, serverPort), Arrays.asList(credential),
                            builder.build());
                }
                dbs.put(database, getDB(database));
            } catch (Exception e) {
                logger.info("connect to mongo server failed：{}", serverHost + "/" + multiServerHosts);
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     * @Description: getDB:(获取数据库).
     * @param databasename
     *            databasename
     * @return MongoDatabase
     */
    public MongoDatabase getDB(String databasename) {
        MongoDatabase db = dbs.get(databasename);
        if (db == null) {
            db = mg.getDatabase(databasename);
            dbs.put(databasename, db);
        }
        return db;
    }

    /**
     * 
     * @Description:getCollection:(获取表集合).
     * @param databasename
     *            databasename
     * @param collectionname
     *            collectionname
     * @return MongoCollection<Document>
     */
    public MongoCollection<Document> getCollection(String databasename, String collectionname) {
        MongoCollection<Document> dbCollection = cols.get(databasename + collectionname);
        if (dbCollection == null) {
            MongoDatabase db = getDB(databasename);
            dbCollection = db.getCollection(collectionname);
            cols.put(databasename + collectionname, dbCollection);
        }
        return dbCollection;
    }

    /**
     * insert:(在指定表集合中存入的对象).
     * 
     * @param databasename
     *            databasename
     * @param collectionname
     *            collectionname
     * @param bean
     *            bean
     * @throws Exception
     *             Exception
     */
    public void insert(String databasename, String collectionname, Object bean) throws Exception {

        MongoCollection<Document> collection = this.getCollection(databasename, collectionname);
        Document document = BeanUtil.bean2Document(bean);
        collection.insertOne(document);
    }

    /**
     * 
     * @Description: (在指定表集合中存入的对象).
     * @param databasename
     *            databasename
     * @param collectionname
     *            collectionname
     * @param beans
     *            beans
     * @throws Exception
     *             Exception
     */
    public void insertList(String databasename, String collectionname, List<?> beans) throws Exception {
        MongoCollection<Document> col = this.getCollection(databasename, collectionname);
        List<Document> objs = BeanUtil.beans2Documents(beans);
        col.insertMany(objs);
    }

    /**
     * 
     * @Description:insert:(在指定表集合中存入的对象)
     * @param databasename
     *            databasename
     * @param collectionname
     *            collectionname
     * @param list
     *            list
     */
    public void insertMapList(String databasename, String collectionname, List<Map<String, Object>> list) {
        MongoCollection<Document> col = this.getCollection(databasename, collectionname);
        col.insertMany(BeanUtil.map2Documents(list));
    }

    /**
     * 
     * @Description:insert:(在指定表集合中存入的对象).
     * @param databasename
     *            databasename
     * @param collectionname
     *            collectionname
     * @param map
     *            map
     */
    public void insertMap(String databasename, String collectionname, Map<String, Object> map) {
        MongoCollection<Document> col = this.getCollection(databasename, collectionname);
        col.insertOne(BeanUtil.map2Document(map));
    }

    /**
     * 
     * @Description:update:(在指定表集合中存入的对象)
     * @param databasename
     *            databasename
     * @param collectionname
     *            collectionname
     * @param key
     *            key
     * @param map
     *            map
     */
    public void update(String databasename, String collectionname, String key, Map<String, Object> map) {
        MongoCollection<Document> col = this.getCollection(databasename, collectionname);
        col.replaceOne(eq(key, map.get(key)), BeanUtil.map2Document(map));
    }

    /**
     * 
     * @Description: TODO
     * @param collectionname
     *            String
     * @param id
     *            id
     * @param map
     *            Map<String, Object>
     * @throws Exception
     *             Exception
     */
    public void updateOne(String collectionname, String id, Map<String, Object> map) throws Exception {
        MongoCollection<Document> collection = this.getCollection(database, collectionname);
        Document document = new Document();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object object = entry.getValue();
            String json = JSON.toJSONString(object, true);
            logger.info(entry.getKey() + ":{}", json);
            document.append(entry.getKey(), json);
        }
        Document modify = new Document();
        modify.append("$set", document);
        collection.updateOne(new BasicDBObject(hashKey, new ObjectId(id)), modify);
    }

    /**
     * 
     * @Description: remove:(从指定集合对象中清除对象).
     * @param databasename
     *            databasename
     * @param collectionname
     *            collectionname
     * @param key
     *            key
     * @param value
     *            value
     */
    public void removeOne(String databasename, String collectionname, String key, String value) {
        MongoCollection<Document> col = this.getCollection(databasename, collectionname);
        col.deleteOne(eq(key, value));
    }

    /**
     * 
     * @Description: remove:(从指定集合对象中清除对象)
     * @param databasename
     *            databasename
     * @param collectionname
     *            collectionname
     * @param key
     *            key
     * @param value
     *            value
     */
    public void removeAll(String databasename, String collectionname, String key, String value) {
        MongoCollection<Document> col = this.getCollection(databasename, collectionname);
        col.deleteMany(eq(key, value));
    }

    /**
     * 
     * @Description: 根据查询条件查询指定db和collection所有数据集
     * @param databasename
     *            databasename
     * @param collectionname
     *            collectionname
     * @param conds
     *            conds
     * @param clazz
     *            clazz
     * @param <T>
     *            List<T>
     * @return <T>
     */
    public <T> List<T> find(String databasename, String collectionname, Map<String, Object> conds, Class<T> clazz) {
        FindIterable<Document> result = null;
        List<T> list = new ArrayList<T>();
        MongoCollection<Document> col = this.getCollection(databasename, collectionname);
        Document condition = new Document();
        for (Map.Entry<String, Object> cond : conds.entrySet()) {
            condition.append(cond.getKey(), cond.getValue());
        }
        result = col.find(condition);
        result.forEach(new Block<Document>() {
            @Override
            public void apply(Document doc) {
                try {
                    T b = clazz.newInstance();
                    list.add(BeanUtil.doc2Bean(doc, b));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return list;
    }

    /**
     * @Description: 根据查询条件分页查询指定db和collection所有数据集
     * @param databasename
     *            databasename
     * @param collectionname
     *            collectionname
     * @param conds
     *            conds
     * @param page
     *            page
     * @param pageize
     *            pageize
     * @param clazz
     *            clazz
     * @param <T>
     *            List<T>
     * @return <T>
     */
    public <T> List<T> findByPages(String databasename, String collectionname, Map<String, Object> conds, int page,
            int pageize, Class<T> clazz) {
        FindIterable<Document> result = null;
        List<T> list = new ArrayList<T>();
        MongoCollection<Document> col = this.getCollection(databasename, collectionname);
        Document condition = new Document();
        for (Map.Entry<String, Object> cond : conds.entrySet()) {
            condition.append(cond.getKey(), cond.getValue());
        }
        result = col.find(condition).skip((page - 1) * pageize).limit(pageize);
        result.forEach(new Block<Document>() {
            @Override
            public void apply(Document doc) {
                try {
                    T b = clazz.newInstance();
                    list.add(BeanUtil.doc2Bean(doc, b));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return list;
    }

    /**
     * @Description: 根据查询条件分页查询指定db和collection所有数据集 forQueryInteractionInterface
     * @param databasename
     *            databasename
     * @param collectionname
     *            collectionname
     * @param conds
     *            conds
     * @param from
     *            from
     * @param pageize
     *            pageize
     * @param clazz
     *            clazz
     * @param <T>
     *            List<T>
     * @return <T> resultList
     */
    public <T> List<T> findByPagesForQueryInteractionInterface(String databasename, String collectionname,
            Map<String, Object> conds, int from, int pageize, Class<T> clazz) {
        FindIterable<Document> result = null;
        List<T> list = new ArrayList<T>();
        MongoCollection<Document> col = this.getCollection(databasename, collectionname);
        Document condition = new Document();
        for (Map.Entry<String, Object> cond : conds.entrySet()) {
            condition.append(cond.getKey(), cond.getValue());
        }
        result = col.find(condition).skip(from).limit(pageize);
        result.forEach(new Block<Document>() {
            @Override
            public void apply(Document doc) {
                try {
                    T b = clazz.newInstance();
                    list.add(BeanUtil.doc2Bean(doc, b));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return list;
    }
    
    /**
     * 
     * @Description: TODO
     * @param collectionname
     *            collectionname
     * @param map
     *            map
     * @return String
     * @throws Exception
     *             Exception
     */
    public String insertOne(String collectionname, Map<String, Object> map) throws Exception {
        MongoCollection<Document> collection = this.getCollection(database, collectionname);
        Document document = new Document();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object object = entry.getValue();
            String json = JSON.toJSONString(object, true);
            logger.info(entry.getKey() + ":{}", json);
            document.append(entry.getKey(), json);
        }
        collection.insertOne(document);
        ObjectId objectId = (ObjectId) document.get(hashKey);
        return objectId.toString();
    }
    
    /**
     * 
     * @Description: TODO
     * @param collectionname
     *            collectionname
     * @param map
     *            map
     * @return String
     * @throws Exception
     *             Exception
     */
    public String insertOneJS(String collectionname, Map<String, Object> map) throws Exception {
        long listenableFutureStart = System.currentTimeMillis();
        MongoCollection<Document> collection = this.getCollection(database, collectionname);
        Document document = new Document();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object object = entry.getValue();
            String json = JSON.toJSONString(object, true);
            logger.info(entry.getKey() + ":{}", json);
            document.append(entry.getKey(), json);
        }
        //异步存报文
        executorService.submit(() -> {
            logger.info("=========Strat===========");
            collection.insertOne(document);
            logger.info("===========End=========");
        });
        ObjectId objectId = (ObjectId) document.get(hashKey);
        logger.info("=============耗费时间 :{}毫秒==============",
                (System.currentTimeMillis() - listenableFutureStart));
        return objectId.toString();
    }

    /**
    * 
    * @param collectionname collectionname
    * @param map map
    * @throws Exception Exception
    */
    public void ansyncInsertOne(String collectionname, Map<String, Object> map) throws Exception {
        MongoCollection<Document> collection = this.getCollection(database, collectionname);
        Document document = new Document();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object object = entry.getValue();
            String json = JSON.toJSONString(object, true);
            logger.info(entry.getKey() + ":{}", json);
            document.append(entry.getKey(), json);
        }
        collection.insertOne(document);
    }

    /**
     * 
     * @Description: TODO databasename
     * @param collectionname
     *            collectionname
     * @param id
     *            id
     * @return String
     * @throws Exception
     *             Exception
     */
    public Document findById(String collectionname, String id) throws Exception {
        Document document = null;
        MongoCollection<Document> collection = this.getCollection(database, collectionname);
        BasicDBObject query = new BasicDBObject();
        query.put(hashKey, new ObjectId(id));
        FindIterable<Document> findIterable = collection.find(query);
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while (mongoCursor.hasNext()) {
            document = (Document) mongoCursor.next();
        }
        return document;
    }

    /**
     * @Description: 根据查询条件查询指定db和collection所有数据集并排序
     * @param databasename
     *            databasename
     * @param collectionname
     *            collectionname
     * @param conds
     *            conds
     * @param orders
     *            orders
     * @param limit
     *            limit
     * @param clazz
     *            clazz
     * @param <T>
     *            List<T>
     * @return <T>
     */
    public <T> List<T> findAndSort(String databasename, String collectionname, Map<String, Object> conds,
            Map<String, Object> orders, Integer limit, Class<T> clazz) {
        FindIterable<Document> result = null;
        List<T> list = new ArrayList<T>();
        MongoCollection<Document> col = this.getCollection(databasename, collectionname);
        Document condition = new Document();
        for (Map.Entry<String, Object> cond : conds.entrySet()) {
            condition.append(cond.getKey(), cond.getValue());
        }
        Document orderCons = new Document();
        for (Map.Entry<String, Object> cond : orders.entrySet()) {
            orderCons.append(cond.getKey(), cond.getValue());
        }
        result = col.find(condition).sort(orderCons).limit(limit);
        result.forEach(new Block<Document>() {
            @Override
            public void apply(Document doc) {
                try {
                    T b = clazz.newInstance();
                    list.add(BeanUtil.doc2Bean(doc, b));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return list;
    }

    public String getDatabase() {
        return database;
    }

}
