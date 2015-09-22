/*
Copyright (c) 2011, Gerhard H. Schalk (www.smartcard-magic.net)
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the 
documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT 
HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.*/
package rfid;
import java.util.List;
import javax.smartcardio.*;

public class Rfid {

    public static void main(String[] args) {

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
                for(int i = 0; i < baCardUid.length; i++ ){
                    System.out.printf("%02X ", baCardUid [i]);
                }
            }

        card.disconnect(false);

        } catch (CardException e) {
            e.printStackTrace();
        }
    }
}
