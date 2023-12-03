package com.wang.Utils;

import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @ClassName SimPathUtils
 * Description
 * @Author jqWang
 * Date 2021/12/9 13:51
 **/
public class SimPathUtils {

    /**
     * 
     *
     * @param pathArray
     * @return String
     * @author jqWang
     * @date 2021/12/9 14:02
     */
    public String mergePoint(String[] pathArray,String md5) {
        StringBuilder sb = new StringBuilder();
        sb.append(md5);
        //sb.append(pathArray[0]);
        for (int i = 0; i < pathArray.length; i++) {
            sb.append("-").append(pathArray[i]);
        }
        return sb.toString();
    }

    public String mergePoint(String[] pathArray) {
        StringBuilder sb = new StringBuilder();
        sb.append(pathArray[0]);
        for (int i = 1; i < pathArray.length; i++) {
            sb.append("-").append(pathArray[i]);
        }
        return sb.toString();
    }


    public static String EncoderByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
       
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
       
        String newStr = base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newStr.replace("-","");
    }

    /**
     
     *
     * @param path
     * @return String[]
     * @author jqWang
     * @date 2021/12/9 14:01
     */
    public String[] getSimPath(String path) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String[] pathArray = path.split("-");
        //String md5 = EncoderByMd5(pathArray[0] + pathArray[pathArray.length - 1]);
        String md5 = pathArray[0] + pathArray[pathArray.length - 1];
        if(pathArray.length != 6){
            return new String[]{md5 + "-" +path};
        }
        String[] res = new String[5];// 5
        res[0] =  md5 + "-" +path;
        for (int i = 1; i < pathArray.length - 1; i++) {// -1
            String temp = pathArray[i];
            pathArray[i] = "*";
            res[i] = mergePoint(pathArray,md5);
            pathArray[i] = temp;
        }
        return res;
    }


    public String[] getSimPath_pre(String path) {
        String[] res = new String[5];
        res[0] =  path;
        String[] pathArray = path.split("-");
        if(pathArray.length != 6){
            return new String[]{path};
        }
        for (int i = 1; i < pathArray.length - 1; i++) {
            String temp = pathArray[i];
            pathArray[i] = "*";
            res[i] = mergePoint(pathArray);
            pathArray[i] = temp;
        }
        return res;
    }

    /**
    
     *
     * @param bucket
     * @return Map<String [ ]>
     * @author jqWang
     * @date 2021/12/9 15:07
     */
    public Map<String, String[]> mergePathBucket(Map<String, String[]> bucket) {
        //声明临时变量存储所有不带 * 的键值，users用 Set存储，起到去重作用
        Map<String, Set<String>> tempBucket = new HashMap<>();
        for(Map.Entry<String,String[]> entry : bucket.entrySet()){

            if(!entry.getKey().contains("*")){
                Set<String> users = new HashSet<>();
                String[] simPath = getSimPath_pre(entry.getKey());
                for(String path : simPath){
                    if(bucket.get(path) != null){
                        String[] userArray = bucket.get(path);
                        for(String user : userArray){
                            users.add(user);
                        }
                    }
                }
                tempBucket.put(entry.getKey(),users);
            }
        }
       
        Map<String,String[]> res = new HashMap<>();
        for(Map.Entry<String,Set<String>> entry : tempBucket.entrySet()){
            String[] comUsers = new String[entry.getValue().size()];
            int i = 0;
            for(String user : entry.getValue()){
                comUsers[i] = user;
                i++;
            }
            res.put(entry.getKey(),comUsers);
        }
        return res;
    }
/*
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

    }*/
}
