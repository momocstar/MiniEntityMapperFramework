import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RegexExample {
    public static void main(String[] args) {
        String sql1 = "select u.id as userid, o.orderNo,o.id from t_user join t_order o on u.id = o.userid  where o.id = @o.id and u.id = @uid";
        String sql2 = "select u.id as userid, o.orderNo,o.id from t_user join t_order o on u.id = o.userid  where o.id = @id and u.id = @uid";

        Pattern pattern = Pattern.compile("@\\w+(\\.\\w+)*");
        Matcher matcher1 = pattern.matcher(sql1);
        Matcher matcher2 = pattern.matcher(sql2);

        while (matcher1.find()) {
            System.out.println("Match in sql1: " + matcher1.group());
        }

        while (matcher2.find()) {
            System.out.println("Match in sql2: " + matcher2.group());
        }
    }
}