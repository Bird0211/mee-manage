package com.mee.manage.vo;

import lombok.Data;

@Data
public class WordsVo implements Comparable<WordsVo>,Cloneable {

    String WordText;

    int Left;

    int Top;

    int Height;

    int Width;


    @Override
    public int compareTo(WordsVo o) {
        if (o == null)
            return 0;

        int result = -1;
        if(getTop() < o.getTop()) {
            if(getLeft() > o.getLeft()) {
                int height = Math.abs(getTop() - o.getTop());
                if (height < getHeight()){
                    result =  1;
                }
            }
        }else {
            if (getLeft() > o.getLeft()) {
                result = 1;
            }else {
                int height = Math.abs(getTop() - o.getTop());
                if (height > getHeight())
                    result = 1;
            }
        }
        return result;
    }


    @Override
    public WordsVo clone() {
        WordsVo o = null;
        try {
            o = (WordsVo)super.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println("MyObject can't clone");
        }
        return o;
    }
}
