package eu.globaldevelopers.globalsms.Class.globalwallet;

import eu.globaldevelopers.globalsms.Class.Product;

public class QrTransaction {
    public Integer id;
    public Integer transaction_type_id;
    public String transaction_date;
    public String terminal;
    public String station_code;
    public Integer product_id;
    public Product product;
    public Integer pump_number;
    public Float quantity;
    public Float pump_price;
    public Integer validated;
    public String result_code;
    public String result_description;
    public String due_date;
    public String created_at;
    public String updated_at;
    public Double max_quantity;
    public String staton;
    public QrCard card;
}
