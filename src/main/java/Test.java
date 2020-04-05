import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Test implements Serializable {
    private int code;
    int arr[] = new int[10];

    public Test(int i) {
        code = i;
    }
    public static void main(String[] args) throws Exception {
        int i = 0;
        final List<String> list = new ArrayList<String>();
        List<String> proxyInstance = (List<String>) Proxy.newProxyInstance(list.getClass().getClassLoader(), list.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return method.invoke(list, args);
            }
        });
        proxyInstance.add(" 你好");
        System.out.println(list);
    }

    static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + "Tools run");
        }
    }

    private static HashMap<Integer, Test> sort(HashMap<Integer, Test> map) {
        Set<Map.Entry<Integer, Test>> set =  map.entrySet();
        List<Map.Entry<Integer, Test>> list = new ArrayList(set);
        Collections.sort(list, new Comparator<Map.Entry<Integer, Test>>() {
            @Override
            public int compare(Map.Entry<Integer, Test> o1, Map.Entry<Integer, Test> o2) {
                return o2.getValue().getCode() - o1.getValue().getCode();
            }
        });
        LinkedHashMap<Integer, Test> linkedHashMap = new LinkedHashMap();
        list.forEach(obj -> linkedHashMap.put(obj.getKey(), obj.getValue()));
        return linkedHashMap;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public int hashCode() {
        return code;
    }
    @Override
    public boolean equals(Object obj) {
        return this.getCode() == ((Test)obj).getCode();
    }
}
