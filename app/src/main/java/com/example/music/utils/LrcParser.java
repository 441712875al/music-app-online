package com.example.music.utils;

import android.util.Log;

import com.example.music.pojo.LrcInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

/*
 * 此类用来解析LRC文件
 * @author along
 */
public class LrcParser {

    private LrcInfo lrcinfo= new LrcInfo();
    //存放临时时间
    private Integer currentTime = 0 ;
    //存放临时歌词
    private String currentContent=null;
    //用于保存时间点和歌词之间的对应关系
    //private Map<Long,String> maps =new HashMap<Long,String>();
    private Map<Integer,String> maps = new TreeMap<>();

    /*
     * 根据文件路径，读取文件，返回一个输入流
     * @param	path	文件路径
     * @return InputStream 文件输入流
     * @throws FileNotFoundException
     * */
    private InputStream readLrcFile(String path) throws FileNotFoundException{
        File f=	new File(path);
        InputStream ins = new FileInputStream(f);
        return ins;
    }

    public LrcInfo parser(String path)throws Exception{
        InputStream in = readLrcFile(path);
        lrcinfo = parser(in);
        return lrcinfo;
    }

    public LrcInfo parse(String lyricText) throws  Exception{
        maps.clear();
        asList(lyricText.split("\n")).forEach(e->parserLine(e));
        //全部解析完后，设置info
        lrcinfo.setInfos(maps);
        return lrcinfo;
    }


    /**
     * @param inputStream 输入流
     * @return
     *
     * */
    public LrcInfo parser(InputStream inputStream) throws IOException{
        //包装对象
        maps.clear();
        InputStreamReader inr = new InputStreamReader(inputStream,"utf-8");
        BufferedReader reader =new BufferedReader(inr);
        //一行一行的读，每读一行解析一行
        String line =null;
        while((line=reader.readLine())!=null){
            parserLine(line);
        }
        //全部解析完后，设置info
        lrcinfo.setInfos(maps);
        return lrcinfo;
    }



    /**
     * 利用正则表达式解析每行具体语句
     * 并将解析完的信息保存到LrcInfo对象中
     * @param line
     */
    private void parserLine(String line) {
        //获取歌曲名信息
        if(line.startsWith("[ti:")){
            String title =line.substring(4,line.length()-1);
            Log.i("","title-->"+title);
            lrcinfo.setTitle(title);
        }
        //取得歌手信息
        else if(line.startsWith("[ar:")){
            String artist = line.substring(4, line.length()-1);
            Log.i("","artist-->"+artist);
            lrcinfo.setArtist(artist);
        }
        //取得专辑信息
        else if(line.startsWith("[al:")){
            String album =line.substring(4, line.length()-1);
            Log.i("","album-->"+album);
            lrcinfo.setAlbum(album);
        }
        //取得歌词制作者
        else if(line.startsWith("[by:")){
            String bysomebody=line.substring(4, line.length()-1);
            Log.i("","by-->"+bysomebody);
            lrcinfo.setBySomeBody(bysomebody);
        }
        //通过正则表达式取得每句歌词信息
        else{
            //设置正则表达式
//            String reg ="\\[(\\d{1,2}:\\d{1,2}\\.\\d{1,2})\\]|\\[(\\d{1,2}:\\d{1,2})\\]";
            String reg="\\[(\\d{2}:\\d{2}\\.\\d+)\\]";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher=pattern.matcher(line);
            //如果存在匹配项则执行如下操作
            while(matcher.find()){
                //得到匹配的内容
                String msg=matcher.group();
                //得到这个匹配项开始的索引
                int start = matcher.start();
                //得到这个匹配项结束的索引
                int end = matcher.end();
                //得到这个匹配项中的数组
                int groupCount = matcher.groupCount();
                for(int index =0;index<groupCount;index++){
                    String timeStr = matcher.group(index);

                    if(index==0){
                        //将第二组中的内容设置为当前的一个时间点
                        currentTime=str2Long(timeStr.substring(1, timeStr.length()-1));
                    }
                }
                //得到时间点后的内容
                String[] content = pattern.split(line);
                //将内容设置成当前内容
                if(content.length>0){
                    currentContent = content[content.length-1];
                }else{
                    currentContent = "";
                }
                //设置时间点和内容的映射,消减currentTime的精度
                maps.put(currentTime/1000, currentContent);
            }
        }
    }



    private Integer str2Long(String timeStr){
        //将时间格式为xx:xx.xx，返回的long要求以毫秒为单位
        String[] s = timeStr.split("\\:");
        int min = Integer.parseInt(s[0]);
        int sec=0;
        int mill=0;
        if(s[1].contains(".")){
            String[] ss=s[1].split("\\.");
            sec =Integer.parseInt(ss[0]);
            mill=Integer.parseInt(ss[1]);
        }else{
            sec=Integer.parseInt(s[1]);
        }
        return min*60*1000+sec*1000+mill;
    }
}