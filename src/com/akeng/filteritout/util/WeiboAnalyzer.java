package com.akeng.filteritout.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import android.util.Log;

public class WeiboAnalyzer {
	static Analyzer ikAnalyzer;  
	static String host = "http://jkx.fudan.edu.cn/fudannlp/";
	
	private static void initAnalyzer(){
		if(ikAnalyzer==null)
			ikAnalyzer=new IKAnalyzer(true);
	}
	
	private static Analyzer getAnalyzer(){
		initAnalyzer();
		return ikAnalyzer;
	}
	
	public static String nlp(String func, String input) throws IOException {        
        // must encode url!! if we write FudannlpResource.seg(String) this way
        input = URLEncoder.encode(input, "utf-8"); //utf-8 重要!
        String url =host + func + "/" + input;
        
        HttpClient client=new DefaultHttpClient();
        HttpGet request=new HttpGet(url);
        HttpResponse response=client.execute(request);
        
        StringBuffer sb = new StringBuffer();
        BufferedReader out = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8")); //utf-8 重要!
        String line;
        while ((line = out.readLine()) != null) 
                sb.append(line);
        out.close();
        
        return sb.toString();
}



	public static Map<String,Integer> splitStatus(String text)throws IOException{
		// Analyzer ikAnalyzer = new IKAnalyzer(true);  
	     return getToken(getAnalyzer(), text);  
	}
	
	/**
	 * 除去用户名以及短链接
	 * @param text
	 * @return
	 */
	public static String cleanUpText(String text){
		String cleanText;
		cleanText=text.replaceAll("(//@)\\S+:", "");
		cleanText=cleanText.replaceAll("(@)\\S+\\s", "");
		cleanText=cleanText.replaceAll("(http://t\\.cn/)\\S{7}", "");
		cleanText=cleanText.replaceAll("(\\[\\S+\\])", "");
		return cleanText;
	}
	
    /** 
     * 分词及打印分词结果的方法 
     * @param analyzer     分词器名称 
     * @param text         要分词的字符串 
     * @throws IOException 抛出的异常 
     */  
    private static Map<String,Integer> getToken(Analyzer analyzer, String text) throws IOException {  
  
        Reader reader = new StringReader(text);  
        TokenStream stream = (TokenStream)analyzer.tokenStream("", reader);  

//       CharTermAttribute  ctermAtt  = (CharTermAttribute )stream.getAttribute(CharTermAttribute.class);  
//        // 循环打印出分词的结果
//        while(stream.incrementToken()){  
//            System.out.print((new String(ctermAtt.buffer())).trim() + "|");   
//        }  
//        System.out.println();
        
//        System.out.println("After tokenize:"+nlp("pos",text)); 
//        System.out.println("Before tokenize:"+nlp("key",text));

        Log.i("Processed Text", text);

        TermAttribute termAtt  = (TermAttribute)stream.addAttribute(TermAttribute.class);

        Map<String,Integer> keyMap=new HashMap<String,Integer>();
        while(stream.incrementToken()){
        	String term=termAtt.term();	
        	if(keyMap.containsKey(term))
        		keyMap.put(term, keyMap.get(term)+1);
        	else
        		keyMap.put(term, 1);
        	
        }  

        stream.close();
        reader.close();
        
//        System.out.println("After tokenize:"+nlp("pos",tokens));
//        System.out.println("After tokenize:"+nlp("key",tokens));
        
        return keyMap;
    }  
}
