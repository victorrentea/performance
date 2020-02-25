package victor.training.concurrency.leaks;

import org.apache.commons.io.IOUtils;

import java.sql.PreparedStatement;

public class IntCache {
    public static void main(String[] args) {


//PreparedStatement ps;
//ps.set
        String s = "s1";
        int i = 1;
        String s2 = "s"+1;
        System.out.println(s == s2);

        Integer id1  =128;
        Integer id2  =128;
        System.out.println(id1 == id2);
    }
}
