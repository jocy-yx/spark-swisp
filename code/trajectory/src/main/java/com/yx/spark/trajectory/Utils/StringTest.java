package com.yx.spark.trajectory.Utils;

/**
 * 从n个数里取出m个数的排列或组合算法实现
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringTest {
    /**
     * 组合选择
     * @param dataList 待选列表
     * @param dataIndex 待选开始索引
     * @param resultList 前面（resultIndex-1）个的组合结果
     * @param resultIndex 选择索引，从0开始
     */
    private List<String> combinationSelect(String[] dataList, int dataIndex, String[] resultList, int resultIndex) {
        int resultLen = resultList.length;
        List<String> ans = new ArrayList<String>();
        int resultCount = resultIndex + 1;
        if (resultCount > resultLen) { // 全部选择完时，输出组合结果
            String tem = new String("");
            for (int i = 0; i < resultList.length; i ++) {
                tem += resultList[i];
                if (i != resultList.length - 1)
                    tem += "-";
            }
            ans.add(tem);
            //System.out.println(Arrays.asList(resultList));
            ans = ans.stream().filter(item -> {
                if (item.contains("*")){
                    String[] split = item.split("-");
                    if (split[0].contains(split[1])||split[1].contains(split[0])){
                        return false;
                    }else {
                        return true;
                    }
                }else {
                    return false;
                }
            }).collect(Collectors.toList());
            return ans;
        }
        // 递归选择下一个
        for (int i = dataIndex; i < dataList.length + resultCount - resultLen; i++) {
            resultList[resultIndex] = dataList[i];
            List<String> tem = combinationSelect(dataList, i + 1, resultList, resultIndex + 1);
            for (String x : tem){
                ans.add(x);
            }
        }
        return ans;
    }
    public String[] combination(String[] str){
        List<String> ans = combinationSelect(str, 0, new String[2], 0);
        String[] strings =  new String[ans.size()] ;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0;i<ans.size();i++){
            stringBuilder.setLength(0);
            String replace = ans.get(i).replace("*", "_");
            String[] split = replace.split("_");
            for (String s:split){
                stringBuilder.append(s);
            }
            strings[i] = stringBuilder.toString();
        }
        ArrayList<String> list = new ArrayList<>();
        for (int i=0;i<strings.length;i++){
            if (!list.contains(strings[i])) list.add(strings[i]);
        }
        String[] result = list.toArray(new String[list.size()]);

        return result;
    }

    /*public static void main(String[] args) {
        StringTest stringTest = new StringTest();
        String[] str = new String[]{"user1","user2","user3*","user4*"};

        String[] strings1 = stringTest.combination(str);
        for (String x: strings1)
            System.out.println(x);
    }*/

}
