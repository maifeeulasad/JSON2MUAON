package com.mua.json.internal;

import com.mua.json.JsonDecoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MArray {
    public List<MValue> mValues =new ArrayList<MValue>();

    public MArray(){

    }

    public MArray(List<MValue> mValues){
        this.mValues=mValues;
    }

    boolean isAllSame(){
        MValueType type=mValues.get(0).mValueType;
        for(MValue mValue:mValues){
            if(mValue.mValueType!=type)
                return false;
        }
        return true;
    }

    boolean isSame(List<String> l1,List<String> l2){
        for(int i=0;i<l1.size();i++){
            if(!l1.equals(l2))
                return false;
        }
        return true;
    }

    private int getKey(){
        MValue mValue=mValues.get(0);
        List<String> keys=new ArrayList<>();
        if(mValue.isObject())
            keys=mValue.mObjectVal.keys();
        for(Integer i: JsonDecoder.defM.keySet()){
            if(isSame(JsonDecoder.defM.get(i),keys)){
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {


        //System.out.println(getKey());
        //System.out.println(getKey()+" get key");

        String res="["+(isAllSame() ? getKey() : "$");

        System.out.println(res);

        for(MValue mValue:mValues){
            res=","+mValue;
        }

        /*
        boolean first=true;
        for(MValue mValue:mValues){
            res+=(first?"":",") + mValue.toString();
            first=false;
        }
         */
        return res+"]";
    }
}