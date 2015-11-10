
package rfid;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Formatter;
import java.util.List;
import javax.smartcardio.*;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.MediaType;

public class Rfid {

    public static void main(String[] args) throws MalformedURLException, IOException, InterruptedException {
        //driver: http://www.acs.com.hk/download-driver-unified/6258/ACS-Unified-Driver-Lnx-Mac-110-P.zip
       String REST_URL = "http://localhost/auth/";
        while (true) {            
           //nimmt später die Uid auf!
            String cardUid = "";
        
        try{

            // Liste mit allen verfügbaren Terminals
            TerminalFactory factory = TerminalFactory.getDefault();

            List<CardTerminal> terminals = factory.terminals().list();

         //   System.out.println("Terminals: " + terminals);//schreibt das aktuelle terminal in die console!
            
            // das erste terminal "bekommen"
            CardTerminal terminal = terminals.get(0);

            // verbindung mit der Karte herstellen
            Card card = terminal.connect("*");
            System.out.println("card: " + card);

            // ATR bekommen
            ATR atr = card.getATR();
            byte[] baAtr = atr.getBytes();

            System.out.print("ATR = 0x");
            for(int i = 0; i < baAtr.length; i++ ){
                System.out.printf("%02X ",baAtr[i]);
            }

            CardChannel channel = card.getBasicChannel();
            byte[] cmdApduGetCardUid = new byte[]{
                        (byte)0xFF, (byte)0xCA, (byte)0x00, (byte)0x00, (byte)0x00};
            
            ResponseAPDU respApdu = channel.transmit(
                                                new CommandAPDU(cmdApduGetCardUid));

            if(respApdu.getSW1() == 0x90 && respApdu.getSW2() == 0x00){

                byte[] baCardUid = respApdu.getData();

                System.out.print("Card UID: = 0x");
                
                 Formatter formatter = new Formatter();

                 
                for(int i = 0; i < baCardUid.length; i++ ){
                    System.out.printf("%02X", baCardUid [i]);
                 
                 cardUid = cardUid + formatter.format("%02X", baCardUid[i]);
                }
               cardUid = cardUid.substring(12, 20);
                System.out.println("");
                System.out.println("Die Uid ist: " +cardUid);
            }
            
            //schickt die Uid an den Server ab!
//            URL url = new URL(REST_URL+ cardUid);
//            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.disconnect();
     
     try {
            System.out.println("notity called");
            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);
            System.out.println(REST_URL + cardUid);
            WebResource webResource = client.resource(UriBuilder.fromUri(REST_URL + cardUid).build());

            ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class);
            System.out.println("Response: " + response.getEntity(String.class));
            
        } catch (ClientHandlerException | UniformInterfaceException ex) {
            ex.printStackTrace();

        } catch (Throwable ex) {
            ex.printStackTrace();
            throw ex;
        }
            
            
            Thread.sleep(4000);
            
            
            

        card.disconnect(false);

        } catch (CardException e) {
            Thread.sleep(500);
        }
    }
   }
}
