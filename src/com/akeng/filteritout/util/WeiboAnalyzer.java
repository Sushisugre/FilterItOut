package com.akeng.filteritout.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class WeiboAnalyzer {

	public static void splitStatus(String text)throws IOException{
		 Analyzer ikAnalyzer = new IKAnalyzer();  
	        System.out.println("======中文=======IKAnalyzer======分词=======");  
	        showToken(ikAnalyzer, text);  
	        System.out.println("======中文=======IKAnalyzer======分词=======");  
	        
//	        Analyzer standardAnalyzer = new StandardAnalyzer(Version.LUCENE_30);  
//	        System.out.println("=====一元========StandardAnalyzer=====分词========"); 
//	        showToken(standardAnalyzer, text); 
//	        System.out.println("=====一元========StandardAnalyzer=====分词========"); 

	}
	
    /** 
     * 分词及打印分词结果的方法 
     * @param analyzer     分词器名称 
     * @param text         要分词的字符串 
     * @throws IOException 抛出的异常 
     */  
    public static void showToken(Analyzer analyzer, String text) throws IOException {  
          
        Reader reader = new StringReader(text);  
        TokenStream stream = (TokenStream)analyzer.tokenStream("", reader);  
        
       //CharTermAttribute  ctermAtt  = (CharTermAttribute )stream.getAttribute(CharTermAttribute.class);  
        TermAttribute termAtt  = (TermAttribute)stream.addAttribute(TermAttribute.class);
        // 循环打印出分词的结果
//        while(stream.incrementToken()){  
//            System.out.print((new String(ctermAtt.buffer())).trim() + "|");   
//        }  
//        System.out.println();
        
        
        while(stream.incrementToken()){  
            System.out.print(termAtt.term() + "|");  
        }  
        System.out.println();
    }  
}
