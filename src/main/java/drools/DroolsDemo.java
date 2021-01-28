package drools;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * Drools7.x样例
 */
public class DroolsDemo {
    
    public static void main(String[] args) {
        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.newKieClasspathContainer();
        KieSession kSession = kc.newKieSession("ksession1");

        Applicant a = new Applicant();
        a.setName("小明");
        a.setAge(20);
        kSession.insert(a);

        kSession.fireAllRules();
        kSession.dispose();

        System.err.println("运行结果：" + a.isValid());
    }
}
