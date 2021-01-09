package com.mua.json;

import com.mua.antlr.JsonLexer;
import com.mua.antlr.JsonParser;
import com.mua.json.internal.MArray;
import com.mua.json.internal.MObject;
import com.mua.json.internal.MValue;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import java.util.*;

public class JsonDecoder {

    public static Map<Integer, List<List<String>>> defs=new HashMap<>();
    public static Map<Integer,List<String>> defM=new HashMap<>();


    public void decode(String json){
        JsonLexer lexer = new JsonLexer(new ANTLRInputStream(json));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JsonParser parser = new JsonParser(tokens);


        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
            }
        });

        MValue value=decodeJson(parser.json());

        /*
        for(Integer i:defs.keySet()){
            System.out.println(i+" : "+defs.get(i));
        }
         */
        generateUniq();

        debugDefs();

        System.out.println("data=");

        System.out.println(value);

        System.out.println("}");
    }

    void debugDefs(){
        System.out.println("{def=[");
        boolean first=true;
        for(Integer i:defM.keySet()){
            System.out.print((first? "" : ",\n")+i);
            System.out.print("(");
            String keys=defM.get(i).toString();
            keys = keys.substring(1,keys.length()-1);
            System.out.print(keys);
            System.out.print(")");
            first=false;
        }
        System.out.println("\n]");
    }

    private MValue decodeJson(JsonParser.JsonContext jsonContext){
        //System.out.println(jsonContext.getText());
        //System.out.println(decodeValue(jsonContext.value()));
        return decodeValue(jsonContext.value());
    }

    private MValue decodeValue(JsonParser.ValueContext valueContext){
        ParseTree pt=valueContext.children.get(0);
        if(pt instanceof JsonParser.ObjContext){
            MValue mValue =decodeObject(pt);
            //System.out.println();
            List<String> keys=mValue.mObjectVal.keys();
            if(defs.containsKey(keys.size())){
                if(!containsDef(keys))
                    defs.get(keys.size()).add(keys);
            }else{
                List<List<String>> res=new ArrayList<>();
                res.add(keys);
                defs.put(keys.size(),res);
            }
            return mValue;
        }else if(pt instanceof JsonParser.ArrContext){
            return decodeArray(pt);
        }else if(pt instanceof JsonParser.BoolContext){
            return decodeBool(pt);
        }else{
            return decodeTerminalNode(pt);
        }
    }

    private MValue decodeTerminalNode(ParseTree pt){
        if(((TerminalNodeImpl)pt).symbol.getType()==JsonParser.STRING){
            return decodeString(pt);
        }else if(((TerminalNodeImpl)pt).symbol.getType()==JsonParser.NUMBER){
            return decodeNumber(pt);
        }else{
            return new MValue();
        }
    }


    private MValue decodeNumber(ParseTree pt){
        try{
            return decodeInteger(pt);
        }catch (Exception e){
            try {
                return decodeFloat(pt);
            } catch (Exception ignored) {
                return new MValue();
            }
        }
    }

    private MValue decodeInteger(ParseTree pt) throws Exception {
        return new MValue(Integer.parseInt(pt.getText()));
    }

    private MValue decodeFloat(ParseTree pt) throws Exception{
        return new MValue(Double.parseDouble(pt.getText()));
    }

    private MValue decodeString(ParseTree pt){
        return new MValue(pt.getText());
    }

    private MValue decodeBool(ParseTree pt){
        return new MValue(pt.getText().equals("true"));
    }

    private MValue decodeArray(JsonParser.ArrContext arrContext){
        if(arrContext.getChildCount()==2){
            return new MValue(new MArray());
        }else{
            return decodeValueSet(arrContext.getChild(1));
        }
        //return new MValue(new MArray());
    }

    private MValue decodeValueSet(ParseTree pt){
        JsonLexer lexer = new JsonLexer(new ANTLRInputStream(pt.getText()));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JsonParser parser = new JsonParser(tokens);
        return decodeValueSet(parser.valueSet());
    }

    private MValue decodeValueSet(JsonParser.ValueSetContext valueSetContext){
        if(valueSetContext.getChildCount()==1){
            return decodeValue(valueSetContext.value());
        }else{
            List<MValue> mValueList=new ArrayList<MValue>();
            mValueList.add(decodeValue(valueSetContext.value()));
            MValue mValue=decodeValueSet(valueSetContext.valueSet());
            if(mValue.isArray()){
                mValueList.addAll(mValue.mArrayVal.mValues);
            }else{
                mValueList.add(mValue);
            }
            return new MValue(new MArray(mValueList));
        }
    }

    private MValue decodeArray(ParseTree pt){
        JsonLexer lexer = new JsonLexer(new ANTLRInputStream(pt.getText()));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JsonParser parser = new JsonParser(tokens);
        return decodeArray(parser.arr());
    }

    private MValue decodeObject(ParseTree pt){
        JsonLexer lexer = new JsonLexer(new ANTLRInputStream(pt.getText()));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JsonParser parser = new JsonParser(tokens);
        return decodeObject(parser.obj());
    }

    private Map<String,MValue> decodePairSet(JsonParser.PairSetContext pairSetContext){
        if(pairSetContext.getChildCount()==2){
            return null;
        }else{
            //System.out.println(pairSetContext.getText());
            if(pairSetContext.getChildCount()==1){
                //return decodePairSet()
                return decodePair(pairSetContext.pair());
                //return new MValue(new MObject(decodePair(pairSetContext.pair())));
            }else{
                Map<String,MValue> res=new HashMap<String, MValue>();
                res = decodePair(pairSetContext.pair());
                res.putAll(decodePairSet(pairSetContext.pairSet()));
                return res;
            }
            //System.out.println(decodePair(pairSetContext.pair()));
            //return new MValue();
            /*
            List<MValue> mValueList=new ArrayList<MValue>();
            mValueList.add(decodeValue(pairSetContext.pair()));
            MValue mValue=decodePairSet(pairSetContext.pairSet());
            if(mValue.isArray()){
                mValueList.addAll(mValue.mArrayVal.mValues);
            }else{
                mValueList.add(mValue);
            }
            return new MValue(new MArray(mValueList));
            */
        }
    }

    private Map<String,MValue> decodePair(JsonParser.PairContext pairContext){
        Map<String,MValue> res=new HashMap<String, MValue>();
        String key=pairContext.STRING().getText();
        res.put(key.substring(0,key.length()-1).substring(1), decodeValue(pairContext.value()));
        return res;
    }

    private MValue decodePairSet(ParseTree pt){
        JsonLexer lexer = new JsonLexer(new ANTLRInputStream(pt.getText()));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JsonParser parser = new JsonParser(tokens);
        return new MValue(new MObject(decodePairSet(parser.pairSet())));
    }

    private MValue decodeObject(JsonParser.ObjContext objContext){
        if(objContext.getChildCount()==2){
            return new MValue(new MObject());
        }else{
            return decodePairSet(objContext.getChild(1));
        }
    }

    boolean containsDef(List<String> tobe){
        List<List<String>> defKey=defs.get(tobe.size());
        for(int i=0;i<defKey.size();i++){
            int res=0;
            for(int j=0;j<defKey.size();j++){
                if(tobe.get(j).equals(defKey.get(i).get(j)))
                    ++res;
            }
            if(res== tobe.size())
                return true;
        }
        return false;
    }

    public int keyCode(int i,int j){
        return ((i+j)*(i+j+1))/2+j;
    }

    private void generateUniq(){
        int u=1;
        for(Integer i:defs.keySet()){
            List<List<String>> deff=defs.get(i);
            for (List<String> strings : deff) {
                defM.put(u++, strings);
            }
        }
    }

}

/*

{
  "name": "Maifee Ul Asad",
  "id": 17701086,
  "nick_name": {
    "a": 1,
    "b": [
      1,
      3,
      5.6,
      {
        "c": "jani",
        "e": "kintu",
        "cc": "bolbo",
        "f": "na",
        "g": {
          "a": 45.6,
          "c": "love you"
        }
      }
    ]
  },
  "test": {
    "a": "a",
    "b": "b"
  }
}


 */
/*
{"name": "Maifee Ul Asad","id": 17701086,"det": {"a": "1","b": {"a": 1.1}}}
 */