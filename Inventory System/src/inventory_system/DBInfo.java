package inventory_system;


public interface DBInfo {
    String Schema = "product_db";

    String URL = "jdbc:mysql://localhost:3306/"+Schema;
    String URL2 = "jdbc:mysql://localhost:3306/";
    String ENCODING = "?useUnicode=yes&characterEncoding=UTF-8";

    String DB_NAME_WITH_ENCODING = URL + ENCODING;

    String USER = "root";

    String PASSWORD = "";


}