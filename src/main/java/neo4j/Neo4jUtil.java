package neo4j;

import com.alibaba.fastjson.JSON;
import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.internal.InternalRelationship;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;

import java.util.*;

/**
 * 通用的neo4j调用类
 *
 * @version 1.0 18-6-5 上午11:21
 */
public class Neo4jUtil {
    private static final Driver driver = Neo4jConf.getDriver();

    public static void main(String[] args) {
        String cql = "match (m:CarInfo) return m";
        Set<Map<String, Object>> nodeList = new HashSet<>();
        Neo4jUtil.getList(cql, nodeList);
        System.out.println(JSON.toJSONString(nodeList));

        cql = "match p=(m:CarInfo)-[*]-() return p limit 25";
        //待返回的值，与cql return后的值顺序对应
        nodeList = new HashSet<>();
        Set<Map<String, Object>> rList = new HashSet<>();
        Neo4jUtil.getPathList(cql, nodeList, rList);
        System.out.println(JSON.toJSONString(nodeList));
        System.out.println(JSON.toJSONString(rList));
    }

    /**
     * cql的return返回多种节点match (n)-[rs]-(n) return n,m,rs：限定返回关系时，关系的别名必须“包含”rs
     *
     * @param cql   查询语句
     * @param lists 和cql的return返回节点顺序对应
     * @return List<Map < String, Object>>
     */
    public static <T> void getList(String cql, Set<T>... lists) {
        //用于给每个Set list赋值
        int listIndex = 0;
        try {
            Session session = driver.session();
            Result result = session.run(cql);
            List<Record> list = result.list();
            for (Record r : list) {
                if (r.size() != lists.length) {
                    System.out.println("节点数和lists数不匹配");
                    return;
                }
            }
            for (Record r : list) {
                for (String index : r.keys()) {
                    //对于关系的封装
                    if (index.indexOf("rs") != -1) {
                        Map<String, Object> map = new HashMap<>();
                        //关系上设置的属性
                        map.putAll(r.get(index).asMap());
                        //外加三个固定属性
                        map.put("rsId", r.get(index).asRelationship().id());
                        map.put("rsType", r.get(index).asRelationship().type());
                        map.put("rsFrom", r.get(index).asRelationship().startNodeId());
                        map.put("rsTo", r.get(index).asRelationship().endNodeId());
                        lists[listIndex++].add((T) map);
                    }
                    //对于节点的封装
                    else {
                        Map<String, Object> map = new HashMap<>();
                        //关系上设置的属性
                        map.putAll(r.get(index).asMap());
                        //外加一个固定属性
                        map.put("nodeId", r.get(index).asNode().id());
                        lists[listIndex++].add((T) map);
                    }
                }
                listIndex = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * cql 路径查询 返回节点和关系
     *
     * @param cql      查询语句
     * @param nodeList 节点
     * @param rsList   关系
     * @return List<Map < String, Object>>
     */
    public static <T> void getPathList(String cql, Set<T> nodeList, Set<T> rsList) {
        try {
            Session session = driver.session();
            Result result = session.run(cql);
            List<Record> list = result.list();
            for (Record r : list) {
                for (String index : r.keys()) {
                    Path path = r.get(index).asPath();
                    //节点
                    Iterable<Node> nodes = path.nodes();
                    for (Iterator iter = nodes.iterator(); iter.hasNext(); ) {
                        InternalNode nodeInter = (InternalNode) iter.next();
                        Map<String, Object> map = new HashMap<>();
                        //节点上设置的属性
                        map.putAll(nodeInter.asMap());
                        //外加一个固定属性
                        map.put("nodeId", nodeInter.id());
                        nodeList.add((T) map);
                    }
                    //关系
                    Iterable<Relationship> rs = path.relationships();
                    for (Iterator iter = rs.iterator(); iter.hasNext(); ) {
                        InternalRelationship relationInter = (InternalRelationship) iter.next();
                        Map<String, Object> map = new HashMap<>();
                        map.putAll(relationInter.asMap());
                        //关系上设置的属性
                        map.put("rsId", relationInter.id());
                        map.put("rsType", relationInter.type());
                        map.put("rsFrom", relationInter.startNodeId());
                        map.put("rsTo", relationInter.endNodeId());
                        rsList.add((T) map);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * cql 返回具体的属性, 如match (n)-[]-() return n.id,n.name，match (n)-[]-() return count(n)
     *
     * @param cql 查询语句
     * @return List<Map < String, Object>>
     */
    public static List<Map<String, Object>> getFields(String cql) {
        List<Map<String, Object>> resList = new ArrayList<>();
        try {
            Session session = driver.session();
            Result result = session.run(cql);
            List<Record> list = result.list();
            for (Record r : list) {
                resList.add(r.asMap());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resList;
    }

    /**
     * 执行添加cql
     *
     * @param cql 查询语句
     */
    public static void add(String cql) {
        //启动事务
        try (Session session = driver.session();
             Transaction tx = session.beginTransaction()) {
            tx.run(cql);
            //提交事务
            tx.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class Neo4jConf {
        private static final String url = "bolt://localhost:7687";
        private static final String username = "neo4j";
        private static final String password = "123456";
        private static volatile Driver driver;

        /**
         * 双重校验锁方式
         *
         * @author: wangpeng
         * @date: 2020/1/14 20:36
         */
        public static Driver getDriver() {
            if (driver == null) {
                synchronized (Neo4jConf.class) {
                    if (driver == null) {
                        Driver driver;
                        try {
                            driver = GraphDatabase.driver(url, AuthTokens.basic(username, password));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        return driver;
                    }
                }
            }
            return driver;
        }
    }
}
