/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lintchecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


/**
 *
 * @author zach
 */
public class ByteCodeAnalyzer  {
    
    
    private Hashtable<String, String> hashToMethod = new Hashtable<String, String>();
    private ArrayList<String> present;
    private ArrayList<String> missing;
    private String inpCode="";
    public String outP="";
   
    
    
    
    public ByteCodeAnalyzer() 
    {
        hashToMethod.put("18160dd", "totalSupply()");
        hashToMethod.put("70a08231", "balanceOf(address)");
        hashToMethod.put("dd62ed3e", "allowance(address,address)");
        hashToMethod.put("a9059cbb", "transfer(address,uint256)");
        hashToMethod.put("095ea7b3", "approve(address,uint256)");
        hashToMethod.put("23b872dd", "transferFrom(address,address,uint256)");
        

    }
    
    
    public void receiveData(String inp, boolean contractData)throws MalformedURLException, IOException, InterruptedException{
        
        
            



          


            

        if(!contractData){
            

            ProcessBuilder builder = new ProcessBuilder("curl",inp);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is)); 
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                inpCode+= line;
                inpCode+="\n";
            }
        
        
       
            
            
        }
        
        else{
            inp.trim();
            String[] inpArray = inp.split(";");
            for(int i=0;i<inpArray.length;i++){
                inpCode+= inpArray[i]+"\n";
                
            }
            
            System.out.print(inpCode);
            
            
            
        }
        
        
        
        
        
    }
    
    public void getAddress(boolean adBoxCheck){
        if(adBoxCheck)
        {
            
            
            String addr = "address";
            String[] lines = inpCode.split("\n");
            for(int i=0;i<lines.length;i++){
                if(lines[i].contains(addr) && !(lines[i].contains("//")) && !(lines[i].contains("/*"))&&lines[i].contains("constant")) outP+=" Warning: Does address at:\n\t"+lines[i]+"\ncontain a valid address ? calls to orphan addresses invloving ether transfer may result in permanaent loss of ether.\n";
            }
        }
        
    }
    
    public void checkCallFunctions(boolean callCheck){
        
        if(callCheck){
            
            
            String check = "\\.call\\.value\\([0123456789]+\\)\\(\\)";
            String[] lines = inpCode.split("\n");
            
            for(int i=0;i<lines.length;i++){
                
                if(lines[i].contains(check)) outP+="(Call Warning) Calling a contracts fallback function with .call() using undefined gas amount at \n\t"+lines[i]+ "\ncan risk danger of re-entrancy";
            }
            
            
           
        }
         
        
    
    }
    
    public void getERC(boolean ercCheck){
        String[] hashes = {"18160dd","70a08231","dd62ed3e","a9059cbb","095ea7b3","23b872dd"};
        
        present = new ArrayList<String>();
        missing = new ArrayList<String>();
        String lines[] = inpCode.split("\n");
        
        
        boolean valid = false;
        int index = 0;
        
        if(ercCheck)
        {
        
            while(!valid)
            {
            
            
            
                if(inpCode.contains(hashes[index]))
                {
                    present.add(hashes[index]);
                
                }
                else missing.add(hashes[index]);
            
            
                index++;
            
                if(index == hashes.length){
                    valid = true;
                }
            
            
            
            
            
            
            }
            if(present.size()>0){
                outP += "Found from ERC20:\n";
                for(int i=0;i<present.size();i++) outP+="\t" +hashToMethod.get(present.get(i))+"\n";
            
            }
            outP+="\n";
        
            if(missing.size()>0){
                outP+="Methods Missing from ERC20 standard:\n";
                for(int i=0;i<missing.size();i++) outP+="\t" +hashToMethod.get(missing.get(i))+"\n";
            
        
            
            }
        }
    }
    
    
    
    
    
    public void findHeaders(boolean headerCheck) throws IOException{
        String isPayable = "payable";
        String fallBack = "function()";
        String modHeader = "modifier";
        String vHeader = "view";
        
        if(headerCheck){
            
        
            if(!inpCode.contains(isPayable)){
                //warning not neeed
                outP+="Note: No function payable, therefore contract doesn't receive ether\n";


            }

            else outP+="Contract is payable\n";

            if(inpCode.contains(fallBack)){
                outP+="Warning: Ensure fallback function's gas cost is 2300 or below to avoid unexpected operation of contract\n";
            }
            String line;
            ArrayList<String> modHeaders = new ArrayList<String>();
            String[] lines = inpCode.split("\n");
            ArrayList<String> views = new ArrayList<String>();
            ArrayList<String> onlyHeaders = new ArrayList<String>();
            for(int i =0;i<lines.length;i++){
                line = lines[i];
                if(line.contains(vHeader)&&line.contains("function")&&line.contains("returns"))  views.add(line);
                if(line.contains(modHeader)&&(!line.contains("///"))&&(!line.contains("//"))) {
                    line.trim();
                    String[] modLine = line.split(" ");
                    for(int j=0;j<modLine.length;j++){
                        modLine[j].trim();


                    }


                    for(int c=0;c<modLine.length;c++) {
                        if(!modLine[c].equals("modifier")&& !modLine[c].equals("{")&&!modLine.equals("")) modHeaders.add(modLine[c]);
                    }
                }
            }

            if(!views.isEmpty()) {
                outP+="Following functions ignored because they don't change state:\n";
                for(int i=0;i<views.size();i++) 
                    outP+=views.get(i)+"\n";
            }

            for(int i=0;i<modHeaders.size();i++){
                for(int b =0;b<lines.length;b++){
                    if(lines[b].contains(modHeaders.get(i))&&!lines[b].contains("modifier")&&!modHeaders.get(i).isEmpty())
                        outP+="Warning: function on line:\n\t"+lines[b]+" contains modifer "+modHeaders.get(i)+"\nCalls to this function outside modifier requirements will result in complete gas loss\n";
                }

            }
        }

    }
    
    public void clearOut(){
        this.outP = "";
    }
    
    // getters for the results
    public ArrayList<String> getMissingResult(){
        return this.missing;
        
    }
    
    public ArrayList<String> getPresentResult(){
        return this.present;
        
    }
    public void setInpCode(String inp){
            this.inpCode = inp;
        }
    
}
