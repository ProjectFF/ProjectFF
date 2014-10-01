package com.tommyx.sacredgeometrydemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

import android.content.Context;
import com.tommyx.sacredgeometrydemo.ZahlenPaar;

public class Tools {

	public Tools() {
		// TODO Auto-generated constructor stub
	}

	
	 public ZahlenPaar[] readRawTextFile(Context ctx, int resId)
     {
		 ZahlenPaar[] z = new ZahlenPaar[0];
		 Stack <Integer> start = new Stack<Integer>();
		 Stack <Integer> end   = new Stack<Integer>();
		 InputStream inputStream = ctx.getResources().openRawResource(resId);
         InputStreamReader inputreader = new InputStreamReader(inputStream);
         BufferedReader buffreader = new BufferedReader(inputreader);
         String line;
         
         int linecount = 0;
         try {
             while (( line = buffreader.readLine()) != null) {
            	 String[] lines = line.split(",");
                 start.add(Integer.valueOf(lines[0]));
                 end.add(Integer.valueOf(lines[1]));
                 linecount++;
             }
            
             z = new ZahlenPaar[linecount];
             
            for (int i=0;i<linecount; i++){
            	z[i] = new ZahlenPaar(start.get(i),end.get(i));
            }
            return z;
         } catch (IOException e) {
        	e.printStackTrace();
        }
		return z;
     }
	
}
