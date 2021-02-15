import billing.OperatorSearchUtil;
import billing.model.PrefixDetail;
import org.junit.Test;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestOperatorSearchUtil {

  @Test
  public void testLoadCheapestOperator() {
    Map<Long, PrefixDetail> cheapestOperatorMap = OperatorSearchUtil.loadCheapestOperator();
    assertEquals("Operator B:", cheapestOperatorMap.get(44L).getOperator());
    assertEquals("Operator A:", cheapestOperatorMap.get(467L).getOperator());
  }

  @Test
  public void testFindCheapestOperator() {
    Map<Long, PrefixDetail> cheapestOperatorMap = OperatorSearchUtil.loadCheapestOperator();
    String chepestOperatorForPrefix = OperatorSearchUtil.findCheapestOperator(cheapestOperatorMap, "44123456");
    assertEquals("Operator B:", chepestOperatorForPrefix);

    chepestOperatorForPrefix = OperatorSearchUtil.findCheapestOperator(cheapestOperatorMap, "467123456");
    assertEquals("Operator A:", chepestOperatorForPrefix);

    chepestOperatorForPrefix = OperatorSearchUtil.findCheapestOperator(cheapestOperatorMap, "463123456");
    assertEquals("Operator A:", chepestOperatorForPrefix);

    chepestOperatorForPrefix = OperatorSearchUtil.findCheapestOperator(cheapestOperatorMap, "913123456");
    assertEquals("Operator B:", chepestOperatorForPrefix);

    chepestOperatorForPrefix = OperatorSearchUtil.findCheapestOperator(cheapestOperatorMap, "+46-73-212345");
    assertEquals("Operator B:", chepestOperatorForPrefix);

    chepestOperatorForPrefix = OperatorSearchUtil.findCheapestOperator(cheapestOperatorMap, "4873-123456");
    assertEquals("Operator B:", chepestOperatorForPrefix);

    chepestOperatorForPrefix = OperatorSearchUtil.findCheapestOperator(cheapestOperatorMap, "26873-123456");
    assertEquals("Operator A:", chepestOperatorForPrefix);



  }
}
