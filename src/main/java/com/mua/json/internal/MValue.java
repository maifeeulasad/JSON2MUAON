package com.mua.json.internal;


public class MValue {
    public Integer mIntVal=null;
    public Double mDoubleVal=null;
    public Boolean mBooleanVal=null;
    public String mStringVal=null;
    public MArray mArrayVal=null;
    public MObject mObjectVal=null;
    public MValueType mValueType=MValueType.UndefinedVal;

    public MValue(){

    }

    public MValue(Integer mIntVal){
        this.mIntVal=mIntVal;
        mValueType=MValueType.IntVal;
    }
    public MValue(Double mDoubleVal){
        this.mDoubleVal=mDoubleVal;
        mValueType=MValueType.DoubleVal;
    }
    public MValue(Boolean mBooleanVal){
        this.mBooleanVal=mBooleanVal;
        mValueType=MValueType.BooleanVal;
    }
    public MValue(String mStringVal){
        this.mStringVal=mStringVal;
        mValueType=MValueType.StringVal;
    }
    public MValue(MObject mObject){
        this.mObjectVal = mObject;
        mValueType=MValueType.ObjectVal;
    }
    public MValue(MArray mArray){
        this.mArrayVal = mArray;
        mValueType=MValueType.ArrayVal;
    }

    public Boolean isInt(){
        return mValueType==MValueType.IntVal;
    }

    public Boolean isDouble(){
        return mValueType==MValueType.DoubleVal;
    }

    public Boolean isBoolean(){
        return mValueType==MValueType.BooleanVal;
    }

    public Boolean isString(){
        return mValueType==MValueType.StringVal;
    }

    public Boolean isObject(){
        return mValueType==MValueType.ObjectVal;
    }

    public Boolean isArray(){
        return mValueType==MValueType.ArrayVal;
    }

    @Override
    public String toString() {
        if(isInt())
            return mIntVal.toString();
        if(isDouble())
            return mDoubleVal.toString();
        if(isBoolean())
            return mBooleanVal.toString();
        if(isString())
            return mStringVal;
        if(isObject())
            return mObjectVal.toString();
        if(isArray())
            return mArrayVal.toString();
        return "--undefined--";
    }
}
