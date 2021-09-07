import com.alibaba.fastjson.JSON;
import mongo.InteractionLogDto;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * 缓存ehcache样例
 * ehcache 2.10.4
 */
public class EhcacheDemo {
    private final static String cacheKey = "ehcacheDemoKey";
    private static CacheManager cacheManager = new CacheManager();
    private static Cache cache = cacheManager.getCache(cacheKey);

    public static void main(String[] args) {
        String id = "T0000000000001";
        InteractionLogDto logDto = get(id);
        logDto = get(id);
        System.out.println(JSON.toJSONString(logDto));
    }

    /**
     * 放
     * @param id
     * @return
     */
    public static InteractionLogDto put(String id) {
        InteractionLogDto logDto = new InteractionLogDto();
        logDto.setBusinessNo(id);
        Element element2 = new Element(id, logDto);
        cache.put(element2);
        return logDto;
    }

    /**
     * 取
     * @param id
     * @return
     */
    public static InteractionLogDto get(String id) {
        Element element = cache.get(id);
        if (element != null) {
            System.out.println(String.format("走缓存：%s", id));
            return (InteractionLogDto) element.getObjectValue();
        }
        System.err.println(String.format("无缓存：%s", id));
        return put(id);
    }
}
