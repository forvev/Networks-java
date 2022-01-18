package com.mycompany.zad12_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
public class Serwer {
    ArrayList strumienieWyjsciowe;
    
    public class ObslugaKlienta implements Runnable {
        BufferedReader czytelnik;
        Socket gniazdo;
        public ObslugaKlienta(Socket gniazdo){
            try {
                
                this.gniazdo = gniazdo;
                InputStreamReader reader = new InputStreamReader(gniazdo.getInputStream());
                czytelnik = new BufferedReader(reader);
                
            } catch (Exception ex) {
            ex.printStackTrace();
            }
        }
        
        @Override
        public void run(){
            String wiadomosc;
            try {
            while ((wiadomosc = czytelnik.readLine()) != null) {
                System.out.println("Odczytano: " +wiadomosc);
                rozeslijDoWszystkich(wiadomosc);
            }
            }catch (IOException ex) {
            ex.printStackTrace();
            }
        }
    
    }
    
    public static void main(String[] args){
        System.out.println("dsda");
        new Serwer().doRoboty();
    }   
    
    public void doRoboty(){
        strumienieWyjsciowe = new ArrayList();
        try {
            ServerSocket gniazdoSerwera = new ServerSocket(2020);
            while (true) {
                Socket gniazdoKlienta = gniazdoSerwera.accept();
                PrintWriter pisarz = new PrintWriter(gniazdoKlienta.getOutputStream());
                strumienieWyjsciowe.add(pisarz);
                Thread watekKlienta = new Thread(new ObslugaKlienta(gniazdoKlienta));
                watekKlienta.start();
                System.out.println("Nawiązano połączenie!");
            }
        } catch (Exception ex) {
        ex.printStackTrace();
        }
    }
    
    public void rozeslijDoWszystkich(String wiadomosc) {
        Iterator it = strumienieWyjsciowe.iterator();
        while (it.hasNext()) {
        try {
            PrintWriter pisarz = (PrintWriter) it.next();
            pisarz.println(wiadomosc);
            pisarz.flush();
            
        } catch (Exception ex){
        ex.printStackTrace();
        }
        }
    }

}