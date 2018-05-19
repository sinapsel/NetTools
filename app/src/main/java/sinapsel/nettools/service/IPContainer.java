/**
 * @author sinapsel
 *
 * Class that stores IP parts
 *
 */

package sinapsel.nettools.service;

public class IPContainer{
    private String HOST, ROUTE, PORT;

    /**
     *
     * @param addr is a full url or IP address with protocol and routes exclude port
     * @param PORT is a string with port
     */
    public IPContainer(String addr, String PORT){
        if(addr.contains("http://"))
            addr = addr.replace("http://", "");
        if(addr.contains("https://"))
            addr = addr.replace("https://", "");
        String[] s = addr.split("/");
        HOST = s[0];
        this.PORT = PORT;
        ROUTE = "";
        for (String ss: s){
            ROUTE = ROUTE.concat("/".concat((ss != null) ? ss : ""));
        }
        ROUTE = ROUTE.replace(HOST, "");
    }

    /**
     * Constructor for default http 80 port
     * @param addr is a full url or IP address with protocol and routes exclude port
     */
    public IPContainer(String addr){
        if(addr.contains("http://"))
            addr = addr.replace("http://", "");
        if(addr.contains("https://"))
            addr = addr.replace("https://", "");
        String[] s = addr.split("/");
        HOST = s[0];
        this.PORT = "80";
        ROUTE = "";
        for (String ss: s){
            ROUTE = ROUTE.concat("/".concat((ss != null) ? ss : ""));
        }
        ROUTE = ROUTE.replace(HOST, "");
    }

    /**
     * @return String in format of host:port/route like google.com:80/images
     */
    public String getAddr(){
        return HOST.concat(":").concat(PORT).concat(ROUTE);
    }

    /**
     * @return base url
     */
    public String getHOST() {
        return HOST;
    }

    /**
     * @return routes from base url to destination point
     */
    public String getROUTE() {
        return ROUTE;
    }

    /**
     * @return port, INT!
     */
    public int getPORT() {
        return Integer.parseInt(PORT);
    }
}