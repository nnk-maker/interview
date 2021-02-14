package billing.model;

public class PrefixDetail {
  Double price;
  String operator;

  private PrefixDetail(Double price, String operator) {
    this.price = price;
    this.operator = operator;
  }

  public static PrefixDetail of(Double price, String operator) {
    return new PrefixDetail(price, operator);
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }
}
