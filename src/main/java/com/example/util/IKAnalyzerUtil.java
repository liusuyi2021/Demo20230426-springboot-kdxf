package com.example.util;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: IKAnalyzerUtil
 * @Description:
 * @Author: 刘苏义
 * @Date: 2023年04月28日10:49
 * @Version: 1.0
 **/

public class IKAnalyzerUtil {

    public static List<String> cut(String msg) throws IOException {
        StringReader sr=new StringReader(msg);
        IKSegmenter ik=new IKSegmenter(sr, true);
        Lexeme lex=null;
        List<String> list=new ArrayList<>();
        while((lex=ik.next())!=null){
            list.add(lex.getLexemeText());
        }
        return list;
    }

    public static void main(String[] args) throws IOException {
        String text="打开安瑞达的冰毒";
        List<String> list=IKAnalyzerUtil.cut(text);
        System.out.println(list);
    }
}
