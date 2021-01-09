package com.mua.json.internal;


import com.mua.json.JsonDecoder;

import java.util.*;

public class MObject {
    public Map<String, MValue> pairs=new HashMap<>();


    public MObject(){

    }

    public MObject(Map<String, MValue> pairs){
        this.pairs=pairs;
    }

    public List<String> keys(){
        List<String> res=new ArrayList<>(pairs.keySet());
        res.sort(new CustomComparator());
        return res;
    }

    public int keyLength(){
        return pairs.size();
    }

    boolean isSame(List<String> l1,List<String> l2){
        for(int i=0;i<l1.size();i++){
            if(!l1.equals(l2))
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String res="{";
        List<String> keys=keys();

        for(Integer i:JsonDecoder.defM.keySet()){
            if(isSame(JsonDecoder.defM.get(i),keys)){
                res+=i;
                break;
            }
        }
        for(String key:pairs.keySet()){
            res+=","+pairs.get(key);
        }
        return res + "}";
        /*
        boolean first=true;
        String res = "{";
        for(String key:pairs.keySet()){
            res+=(first?"":",") + "\"" + key+ "\"" +" : "+pairs.get(key);
            first=false;
        }
        return res+"}";

         */
    }
}

class CustomComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        if(o1.length()==o2.length()){
            return o1.compareTo(o2);
        }
        return o1.length()<o2.length() ? -1 : 1;
        //return o1.compareTo(o2);
    }
}