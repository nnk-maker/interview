package billing;

import billing.model.PrefixDetail;

import java.util.Map;
import java.util.Scanner;

public class CheapestOperatorSearch {
  public static void main(String[] args) {
    Map<Long, PrefixDetail> cheapestOperatorMap = OperatorSearchUtil.loadCheapestOperator();
    cheapestOperatorMap.entrySet()
        .stream()
        .forEach(entry -> {
          System.out.println(entry.getKey() + " " + entry.getValue().getPrice() + " " + entry.getValue().getOperator());
        });

    Scanner scanner = new Scanner(System.in);
    System.out.println("Enter the phone number: ");
    String phoneNumber = scanner.next();
    scanner.close();
    System.out.println(OperatorSearchUtil.findCheapestOperator(cheapestOperatorMap, phoneNumber));

  }
}
