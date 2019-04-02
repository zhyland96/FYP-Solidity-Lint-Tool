/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lintchecker;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

/**
 *
 * @author zach
 */
public class CInterface {
    
    public String addressOrSample;
    public ByteCodeAnalyzer bc;
    public JCheckBox Erc20 = new JCheckBox("ERC20");
    public JCheckBox mod = new JCheckBox("Headers");
    public JCheckBox add = new JCheckBox("Address");
    public JCheckBox callTick = new JCheckBox("Call Functions");
    public JCheckBox manualInp = new JCheckBox("Man");
    
    public CInterface() throws IOException, MalformedURLException, InterruptedException{
        
        JTextField addressBox = new JTextField("Enter contract address here");
        JTextArea outputField = new JTextArea();
        
        
        bc = new ByteCodeAnalyzer();
        
        
        
        
        
        JButton addressBut = new JButton("Go");
        
        
        
        addressBut.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                
                
                addressOrSample = addressBox.getText();
                
                
                
                try {
                    bc.receiveData(addressOrSample,manualInp.isSelected());
                    bc.getERC(Erc20.isSelected());
                    
                } catch (IOException ex) {
                    Logger.getLogger(CInterface.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(CInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                try {
                    bc.findHeaders(mod.isSelected());
                } catch (IOException ex) {
                    Logger.getLogger(CInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
                bc.checkCallFunctions(callTick.isSelected());
                bc.getAddress(add.isSelected());
                outputField.setText(bc.outP);
                bc.clearOut();
                
                
                
            }
        });
        
        
        
        
        
        JPanel div1 = new JPanel();
        JPanel div2 = new JPanel();
        
        div2.setLayout(new GridLayout(0,4));
        JPanel checkDiv1 = new JPanel();
        JPanel checkDiv2 = new JPanel();
        div2.add(Erc20);
        div2.add(add);
        div2.add(mod);
        div2.add(callTick);
        
        
        JPanel div3 = new JPanel();
        JPanel div4 = new JPanel();
        
        JFrame wind = new JFrame("Lint Tool");
        
        JPanel topH = new JPanel();
        JPanel botH = new JPanel();
        
        
        
        JPanel backDiv = new JPanel();
        backDiv.setLayout(new GridLayout(5,0));
        
        JPanel back = new JPanel();
        back.setLayout(new GridLayout(2,0));
        topH.setLayout(new GridLayout(0,2));
        JPanel rightCorner = new JPanel();
        rightCorner.setLayout(new GridLayout(0,5));
        JPanel r1 = new JPanel();
        JPanel r2 = new JPanel();
        JPanel r3 = new JPanel();
        JPanel r4 = new JPanel();
        rightCorner.add(manualInp);
        rightCorner.add(r2);
        rightCorner.add(r3);
        rightCorner.add(addressBut);
        rightCorner.add(r4);
        
        topH.add(addressBox);
        topH.add(rightCorner);
        backDiv.add(topH);
        backDiv.add(div1);
        backDiv.add(div2);
        backDiv.add(div3);
        backDiv.add(div4);
        
        
       
        
        JScrollPane scroll = new JScrollPane(outputField);
        
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        
        
        
        
        
        back.add(backDiv);
        back.add(scroll);
        
        
       
        wind.add(back);
        wind.setSize(new Dimension(800,600));
        wind.setDefaultCloseOperation(EXIT_ON_CLOSE);
        wind.setVisible(true);
    }
    
}
