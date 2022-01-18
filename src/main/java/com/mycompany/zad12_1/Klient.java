package com.mycompany.zad12_1;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.*;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import javax.swing.*;

public class Klient extends JFrame{ // inheration for setLayout
    JTextArea odbiorWiadomosci;
    JTextField wiadomosc, autor, data;
    BufferedReader czytelnik;
    PrintWriter pisarz;
    Socket gniazdo;
    JLabel e1, e2, e3;
    JFrame frame;
    JPanel panel, panel2;
    String text_pattern;
    //JMenu menu1, menu2;
    //JMenuBar pasek;

    char [][]tab_Vigenere;
    String wiad_dec, autor_dec, data_dec;
    
    
    public static void main(String[] args) {
        
        Klient klient = new Klient();
        
        try{
            klient.polaczMnie();
        }catch(Exception e){
            
        }
        
        
    }
    
    Klient(){
        frame = new JFrame("Prosty klient czatu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new JPanel();
        
        panel2 = new JPanel(new GridBagLayout());
        
        int pom=65;
        
        
        tab_Vigenere = new char[26][];
        for(int i=0; i<tab_Vigenere.length; i++){
            tab_Vigenere[i] = new char[26];
            
            pom=65+i;
            
            for(int j=0; j<tab_Vigenere[i].length ;j++){
                if(pom>90) pom=65;
                tab_Vigenere[i][j] = (char)pom;
                //System.out.print(" "+tab_Vigenere[i][j]);
                pom++;    
            }
            //System.out.println("");
        }
        
        text_pattern="ARTUR";
        
    }    
    
    public void polaczMnie() throws Exception{
        
        odbiorWiadomosci = new JTextArea(15, 50);
        odbiorWiadomosci.setLineWrap(true);
        odbiorWiadomosci.setWrapStyleWord(true);
        odbiorWiadomosci.setEditable(false);
        JScrollPane przewijanie = new JScrollPane(odbiorWiadomosci);

        przewijanie.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        przewijanie.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        wiadomosc = new JTextField(20);
        JButton przyciskWyslij = new JButton("Wyslij");
        przyciskWyslij.addActionListener(new SluchaczPrzycisku());
        
        //-moje
        e1 = new JLabel("Wprowadz dane:");
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=0;
        c.insets = new Insets(2,2,2,2);
        panel2.add(e1, c);
        
        e2 = new JLabel("Autor:");
        c.gridx=0;
        c.gridy=1;
        panel2.add(e2,c);
        
        autor = new JTextField(10);
        c.gridx=1;
        c.gridy=1;
        panel2.add(autor,c);
        
        e3 = new JLabel("Data:");
        c.gridx=0;
        c.gridy=2;
        panel2.add(e3,c);
        
        data = new JTextField(10);
        c.gridx=1;
        c.gridy=2;
        panel2.add(data,c);
        //-----
        
        panel.add(przewijanie);
        panel.add(wiadomosc);
        panel.add(przyciskWyslij);
        //panel.add(e1);
        //panel.add(e2,new BorderLayout(2,2));
        
        
        konfiguruj();
        
        //int pom;
        //System.out.println("Czy na pewno chcesz kontynuowac?\n[1]-tak\n[2]-nie");
        //Scanner sc = new Scanner(System.in);
            
        //pom = sc.nextInt();
            
        //if(pom==2) throw new Exception("S");
        
        Thread watekOdbiorcy = new Thread(new Odbiorca());
        watekOdbiorcy.start();
        
        //frame.getContentPane().add(new MyPanel());
        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.getContentPane().add(panel2,BorderLayout.SOUTH);
        //frame.setLayout(new BorderLayout());
        //frame.add(panel);
        //frame.add(e1,BorderLayout.EAST)
        frame.setSize(new Dimension(600, 400));
        //frame.pack();
        frame.setVisible(true);
    }
    
    private void konfiguruj() {
        
         
            
        try {
            
            
            gniazdo = new Socket("127.0.0.1", 2020);
            InputStreamReader czytelnikStrm = new InputStreamReader(gniazdo.getInputStream());
            czytelnik = new BufferedReader(czytelnikStrm);
            pisarz = new PrintWriter(gniazdo.getOutputStream());
            
            
            
            System.out.println("Zakończono konfiguracje sieci");
        } catch (IOException ex) {
            System.out.println("Konfiguracja sieci nie powiodła się!");
            ex.printStackTrace();
        }catch(Exception e){
            System.out.println("siema");
        }
    }
    private class SluchaczPrzycisku implements ActionListener{
        
        //szyforowanie
        
        
        @Override
        public void actionPerformed(ActionEvent e) {
            encryption(wiadomosc.getText().toUpperCase(), 1);
            encryption(autor.getText().toUpperCase(), 2);
            encryption(data.getText().toUpperCase(), 3);
            try {
                System.out.println("1: "+wiad_dec);
                System.out.println("2: "+autor_dec);
                System.out.println("3: "+data_dec);
                pisarz.println(wiad_dec);
                pisarz.println(autor_dec);
                pisarz.println(data_dec);
                
                //pisarz.println("--------------------");
                pisarz.flush();
            } catch (Exception ex) {
                
            ex.printStackTrace();
            }
            
            wiadomosc.setText("");
            autor.setText("");
            data.setText("");
            wiadomosc.requestFocus();
        }
        
    }
    public class Odbiorca implements Runnable {
        
        @Override
        public void run(){
            
            String wiad;
            
            try {
                while ((wiad = czytelnik.readLine()) != null){
                    System.out.println("Odczytano: " + wiad);
                    wiad=decryption(wiad);
                    odbiorWiadomosci.append(wiad.toLowerCase() + "\n");
                }
                
            }catch (Exception ex) {
                
            ex.printStackTrace();
            }
        }
    }
    
    public void encryption(String wiad_copy, int tryb){
        //String wiad_copy = wiadomosc.getText().toUpperCase();
        //String autor_copy = autor.getText().toUpperCase();
        //String data_copy = data.getText().toUpperCase();
        
        if(tryb==1) wiad_dec="";
        else if(tryb==2) autor_dec="";
        else if(tryb==3) data_dec="";

        char pom;    
        
        int pom_dec, ind_x, ind_y, iterator=0;
        for(int i=0; i<wiad_copy.length(); i++){
            pom = wiad_copy.charAt(i);
            
            pom_dec = (int)pom;
            
            if(pom_dec>64 && pom_dec<91){
                
                ind_x = text_pattern.charAt(iterator)-65;
                ind_y = pom_dec-65;
                
                if(tryb==1) wiad_dec+=tab_Vigenere[ind_x][ind_y];
                else if(tryb==2) autor_dec+=tab_Vigenere[ind_x][ind_y];
                else if(tryb==3) data_dec+=tab_Vigenere[ind_x][ind_y];
            }
            else{
                if(tryb==1) wiad_dec+=pom;
                else if(tryb==2) autor_dec+=pom;
                else if(tryb==3) data_dec+=pom;
                
            }
            if(iterator>=4) iterator=0;
            else iterator++;
        }   
        
    }
    
    public String decryption(String wiad){
        
        String dec_pom="";
        char pom_c, pom_wiad_c;
        int ind_x, iterator=0;
        
        for(int i=0; i<wiad.length(); i++){
            pom_c = text_pattern.charAt(iterator);
            pom_wiad_c = wiad.charAt(i);
            //szukamy indexu wiersza
            //System.out.println("c "+pom_c );
            ind_x = (int)pom_c-65;
            
            if(pom_wiad_c>='A' && pom_wiad_c<='Z'){
                
                for(int j=0; j<tab_Vigenere.length; j++){
                    if(tab_Vigenere[ind_x][j]==pom_wiad_c){
                        //System.out.println("cop: "+tab_Vigenere[ind_x][j]+" and "+pom_c);
                        dec_pom+=tab_Vigenere[0][j];
                        //System.out.println("add: "+tab_Vigenere[0][j]+" j: "+j);
                    }
                }
            }
            else{
                dec_pom+=pom_wiad_c;
            }
            
            if(iterator>3) iterator=0;
            else iterator++;
            //System.out.println("Iterator: "+iterator);
        }
         
        return dec_pom;
    }
    
}
