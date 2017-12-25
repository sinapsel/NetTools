package simapps.nettools.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Ping {
    public static String ping(int n, String addr){
        String pr = "";
        try{
            Process p = Runtime.getRuntime().exec("/system/bin/ping -c ".concat(Integer.toString(n)).concat(" ").concat(addr));
            BufferedReader is = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s;
            StringBuilder sb = new StringBuilder("");
            while((s = is.readLine())!= null){
                sb.append(s.concat("\n"));
            }
            pr = sb.toString();
        } catch (Exception e){}
        return pr;
    }
}
