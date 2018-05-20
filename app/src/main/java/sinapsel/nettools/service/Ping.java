/**
 * @author sinapsel
 *
 * Class executes a system programm ping
 */
package sinapsel.nettools.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Ping {
    /**
     *
     * @param n count of sending packets
     * @param IP IP address
     * @return String with output from ping
     */
    public static String ping(int n, IPContainer IP){
        String pr = "";
        try{
            Process p = Runtime.getRuntime().exec("/system/bin/ping -c ".concat(Integer.toString(n)).concat(" ").concat(IP.getHOST()));
            BufferedReader is = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s;
            StringBuilder sb = new StringBuilder("");
            while((s = is.readLine())!= null){
                sb.append(s.concat("\n"));
            }
            pr = sb.toString();
        } catch (IOException e){}
        return pr;
    }
}
