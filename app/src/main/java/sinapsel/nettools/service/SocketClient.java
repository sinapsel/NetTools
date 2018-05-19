package sinapsel.nettools.service;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

enum QType{
    RAW, GET, POST;
}

public abstract class SocketClient extends Thread {
    /**
     *
     * @param IP IP String from UI
     * @param headers headers for get/post request
     * @param body body of the request. used for post to transfer arguments
     * @param type type of query
     * @param PORT @see IP
     */
    protected SocketClient(String IP, String headers, String body, String type, String PORT){
        this.IP = new IPContainer(IP, PORT);
        if(type.equals("GET"))
            this.qt = QType.GET;
        if(type.equals("RAW"))
            this.qt = QType.RAW;
        if(type.equals("POST"))
            this.qt = QType.POST;
        if (!headers.equals("")){
            this.headers = headers;
        }
        this.body = body;
    }

    private IPContainer IP;
    private QType qt;
    private String headers = "Connection: Close\n\rAccept: text/html\n\rAccept-Language: en-US,ru-ru\n\rUser-Agent: Mozilla/5.0 (X 11; Ubuntu; Linux x86_64) Gecko";
    private String body;

    private String query;
    public String answ;

    /**
     * implement on fragment or activity to show server log up
     */
    public abstract void commit();

    private void makeQuery(){
        Log.d("SockCli", "making");
        Log.d("SockCli", qt.toString());
        if(qt == QType.RAW){
            query = body;
        }
        if(qt == QType.GET){
            query = "GET "+IP.getROUTE()+" HTTP/1.1\r\n";
            query += "Host: "+IP.getHOST()+"\r\n"+headers+body;
        }
        if(qt == QType.POST){
            query = "POST "+IP.getROUTE()+" HTTP/1.1\r\n";
            query += "Host: "+IP.getHOST()+"\r\n"+headers+body;
        }
        Log.d("SockCli", query);
    }

    @Override
    public void run() {
        try {
            Socket s = new Socket(IP.getHOST(), IP.getPORT());
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            OutputStream ou = s.getOutputStream();
            makeQuery();
            byte buf[] = query.getBytes();
            ou.write(buf);
            StringBuilder sb = new StringBuilder();
            while(!(answ = in.readLine()).equals("")){
                sb.append(answ.concat("\n"));
            }
            answ = sb.toString();
//            int c;
//            while ((c = in.read()) != -1) {
//                sb.append((char) c);
//            }
//            answ = sb.toString();

        } catch (IOException e) {
            answ = "Invalid URL or Port";
            e.printStackTrace();
        }
        commit();
    }
}
