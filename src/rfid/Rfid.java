
package rfid;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Formatter;
import java.util.List;
import javax.smartcardio.*;

public class Rfid {

    public static void main(String[] args) throws MalformedURLException, IOException {
        //driver: http://www.acs.com.hk/download-driver-unified/6258/ACS-Unified-Driver-Lnx-Mac-110-P.zip
        try{

            // Liste mit allen verfügbaren Terminals
            TerminalFactory factory = TerminalFactory.getDefault();

            List<CardTerminal> terminals = factory.terminals().list();

            System.out.println("Terminals: " + terminals);
            
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

                 String cardUid = "";
                for(int i = 0; i < baCardUid.length; i++ ){
                    System.out.printf("%02X", baCardUid [i]);
                 
                 cardUid = cardUid + formatter.format("%02X", baCardUid[i]);
                }
               cardUid = cardUid.substring(15, 20);
                System.out.println("");
                System.out.println("Die Uid ist: " +cardUid);
            }
            
            
//            URL url = new URL("http://localhost:8888/auth/ ");
//     HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//     urlConnection.disconnect();

        card.disconnect(false);

        } catch (CardException e) {
            e.printStackTrace();
        }
    }
}
