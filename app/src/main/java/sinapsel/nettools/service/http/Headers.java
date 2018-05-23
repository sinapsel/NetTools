package sinapsel.nettools.service.http;

/**
 * Enumeration class collecting various response statuses
 */
enum HTTP_STATUS{
    OK200("200 OK"),
    ERR404("404 NOT FOUND"),
    ERR403("403 PERMISSION DENIED"),
    ERR400("400 BAD REQUEST");

    private final String status;
    HTTP_STATUS(String status){
        this.status = status;
    }

    @Override
    public String toString(){
        return "HTTP/1.1 " + this.status + " \r\n";
    }
}

/**
 * Enumeration class for selecting a correct content-type header from requesting extension
 */
enum MIME {
    JS("JS"),
    CSS("CSS"),
    GIF("GIF"),
    HTM("HTM"),
    HTML("HTML"),
    ICO("ICO"),
    JPG("JPG"),
    JPEG("JPEG"),
    PNG("PNG"),
    TXT("TXT"),
    XML("XML"),
    OTHER("");

    private final String extension;

    MIME(String extension) {
        this.extension = extension;
    }

    /**
     *
     * @return Content-type: ... for header in the response
     */
    @Override
    public String toString() {
        String out = "";
        switch (this) {
            case JS:
                out = "application/x-javascript"; break;
            case CSS:
                out = "text/css"; break;
            case GIF:
            case ICO:
                out = "image/gif"; break;
            case HTM:
            case HTML:
                out = "text/html; charset=UTF-8"; break;
            case JPG:
            case JPEG:
                out = "image/jpeg"; break;
            case PNG:
                out = "image/png"; break;
            case TXT:
                out = "text/plain; charset=UTF-8"; break;
            case XML:
                out = "text/xml"; break;
            default:
                out = "text/plain; charset=UTF-8"; break;
        }
        String title = "Content-Type: ";
        String LN = " \r\n";
        return title.concat(out).concat(LN);
    }
}

enum CONNECTION{
    CLOSE("Close"), KEEP_ALIVE("Keep-alive");
    private final String type;
    CONNECTION(String type){
        this.type = type;
    }
    @Override
    public String toString(){
        return "Connection: " + this.type + " \r\n";
    }

}

final class LENGTH{
    private int length;
    LENGTH(int length){
        this.length = length;
    }
    @Override
    public String toString(){
        return "Content-Length: " + Integer.toString(this.length) + " \r\n";
    }
}

public class Headers {
    private MIME Content_type;
    private HTTP_STATUS HTTP_Status;
    private CONNECTION Connection;
    private LENGTH Length;
    Headers(String ext, String stat, int len){
        Connection = CONNECTION.CLOSE;
        Content_type = MIME.valueOf(ext);
        HTTP_Status = HTTP_STATUS.valueOf(stat);
        Length = new LENGTH(len);
    }
    Headers(){
        Connection = CONNECTION.CLOSE;
        Length = new LENGTH(0);
    }
    public void setContent_type(String ext){
        Content_type = MIME.valueOf(ext);
    }
    public void setHTTP_Status(String stat){
        HTTP_Status = HTTP_STATUS.valueOf(stat);
    }
    public void setLength(int l){
        Length = new LENGTH(l);
    }

    public MIME getContent_type() {
        return Content_type;
    }

    public void reset(){

    }

    @Override
    public String toString(){
        return HTTP_Status.toString() + Content_type.toString()
                + Connection.toString() + Length.toString();
    }
}
