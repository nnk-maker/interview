package billing;

import billing.model.PrefixDetail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Pattern;

public final class OperatorSearchUtil {
  private static final String PRICELIST_PATH = "src/main/resources/priceList";

  public static Map<Long, PrefixDetail> loadCheapestOperator(){
    Map<Long,PrefixDetail> cheapestOperatorMap = new HashMap<>();
    Map<Long,Double> operatorPrices = new HashMap<>();
    try(FileReader fr=new FileReader(new File(PRICELIST_PATH))){
      BufferedReader br=new BufferedReader(fr);
      String line = br.readLine();
      String operatorName = "";
      String[] prefixAndPrice = new String[0];
      Long prefix = 0l;
      Double price= 0.0d;

      while(line != null) {
        prefixAndPrice = new String[0];
        prefix = 0l;
        price= 0.0d;

        line = line.trim();
        if (line.startsWith("Operator")){
          mergeMap(cheapestOperatorMap, operatorPrices, operatorName);
          operatorPrices = new HashMap<>();
          operatorName = line;
        } else if (Pattern.matches("^[0-9]+.*$", line)){
          prefixAndPrice = line.split("\\s+");
        }
        if (prefixAndPrice.length == 2){
          prefix = Long.parseLong(prefixAndPrice[0]);
          price = Double.parseDouble(prefixAndPrice[1]);
          operatorPrices.put(prefix, price);
        }
        line = br.readLine();
      }
      mergeMap(cheapestOperatorMap, operatorPrices, operatorName);
    } catch(Exception e){

    }
    Map<Long,PrefixDetail> cheapestOperatorBasedOnPrefix = new LinkedHashMap<>();
    cheapestOperatorMap.entrySet()
        .stream()
        .sorted(Map.Entry.<Long,PrefixDetail>comparingByKey().reversed())
        .forEach(entry -> {
      cheapestOperatorBasedOnPrefix.put(entry.getKey(), entry.getValue());
    });
    return cheapestOperatorBasedOnPrefix;
  }

  private static void mergeMap(Map<Long,PrefixDetail> cheapestOperatorMap, Map<Long, Double> operatorPrices, String operator) {
    if (operatorPrices.isEmpty()) {
      return;
    }
    if (cheapestOperatorMap.isEmpty()) {
      operatorPrices.entrySet()
          .stream()
          .forEach(entry -> {
            cheapestOperatorMap.put(entry.getKey(), PrefixDetail.of(entry.getValue(), operator));
          });
      return;
    }
    for (Map.Entry<Long, Double> prefixAndPrice : operatorPrices.entrySet()) {
       Long prefix = prefixAndPrice.getKey();
       Double price = prefixAndPrice.getValue();
       if (cheapestOperatorMap.containsKey(prefix)) {
         PrefixDetail prefixDetail = cheapestOperatorMap.get(prefix);
         if (price < prefixDetail.getPrice()) {
           prefixDetail.setPrice(price);
           prefixDetail.setOperator(operator);
         }
       } else {
         findPartialMatchingPrefix(cheapestOperatorMap, prefix, price, operator);
       }
    }
    findPartialMatchingPrefixInOperatorPrices(cheapestOperatorMap, operatorPrices, operator);
  }

  private static void findPartialMatchingPrefix(Map<Long,PrefixDetail> cheapestOperatorMap, Long prefix, Double price, String operator) {
    Long prefixOriginal = prefix;
    boolean foundMatch = false;
    while (broadenPrefixPossible(prefix, 1)) {
      prefix = prefix / 10;
      if (cheapestOperatorMap.containsKey(prefix)) {
        PrefixDetail prefixDetail = cheapestOperatorMap.get(prefix);
        if (price < prefixDetail.getPrice()) {
          cheapestOperatorMap.put(prefixOriginal, PrefixDetail.of(price, operator));
        } else {
          cheapestOperatorMap.put(prefixOriginal, PrefixDetail.of(prefixDetail.getPrice(), prefixDetail.getOperator()));
        }
        foundMatch = true;
        break;
      }
    }
    if (!foundMatch) {
      cheapestOperatorMap.put(prefixOriginal, PrefixDetail.of(price, operator));
    }
  }

  private static void findPartialMatchingPrefixInOperatorPrices(Map<Long,PrefixDetail> cheapestOperatorMap, Map<Long,Double> operatorPrices, String operator) {
    for (Long prefix: cheapestOperatorMap.keySet()) {
      Long prefixOriginal = prefix;
      while(broadenPrefixPossible(prefix, 1)) {
        prefix = prefix / 10;
        if (operatorPrices.containsKey(prefix)) {
          PrefixDetail prefixDetail = cheapestOperatorMap.get(prefixOriginal);
          Double price = prefixDetail.getPrice();
          Double operatorPrice = operatorPrices.get(prefix);
          if (operatorPrice < price) {
            cheapestOperatorMap.put(prefixOriginal, PrefixDetail.of(operatorPrice, operator));
          }
          break;
        }
      }
    }
  }

  private static boolean broadenPrefixPossible(Object prefix, Integer length) {
    String prefixString = String.valueOf(prefix);
    return prefixString.length() > length;
  }

  public static String findCheapestOperator(Map<Long, PrefixDetail> cheapestOperatorMap, String phoneNumberStr){
    phoneNumberStr = phoneNumberStr.replaceAll("\\+", "");
    phoneNumberStr = phoneNumberStr.replaceAll("\\-", "");
    phoneNumberStr = phoneNumberStr.replaceAll("\\s+", "");
    Long phoneNumberPrefix = Long.parseLong(phoneNumberStr.trim());
    while (broadenPrefixPossible(phoneNumberPrefix, 0)) {
      if (cheapestOperatorMap.containsKey(phoneNumberPrefix)) {
        return cheapestOperatorMap.get(phoneNumberPrefix).getOperator();
      }
      phoneNumberPrefix = phoneNumberPrefix / 10;
    }
    return "No matching operator found for this phone number";
  }
}
