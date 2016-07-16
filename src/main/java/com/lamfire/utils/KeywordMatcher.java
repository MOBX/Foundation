package com.lamfire.utils;

import com.lamfire.logger.Logger;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于DFA关键词匹配算法
 * User: lamfire
 * Date: 15-1-19
 * Time: 上午10:06
 * To change this template use File | Settings | File Templates.
 */
public class KeywordMatcher {
    private static final Logger logger = Logger.getLogger(KeywordMatcher.class);
    private TreeNode rootNode = new TreeNode();
    private ByteBuffer keywordBuffer = ByteBuffer.allocate(1024);
    private Charset charset;

    public KeywordMatcher(List<String> keywordList){
       this(keywordList,Charset.forName("UTF-8"));
    }

    public KeywordMatcher(List<String> keywordList, Charset charset){
        this.charset = charset;
        for (String keyword : keywordList) {
            if(keyword == null) continue;
            addKeyword(keyword, charset);
        }
    }

    public void addKeywords(List<String> keywordList){
        addKeywords(keywordList,this.charset);
    }

    public void addKeywords(List<String> keywordList,Charset charset){
        this.charset = charset;
        for (String keyword : keywordList) {
            if(keyword == null) continue;
            addKeyword(keyword,charset);
        }
    }

    public void addKeyword(String keyword){
        addKeyword(keyword,this.charset);
    }

    public void addKeyword(String keyword,Charset charset){
        if(keyword == null) return;
        keyword = keyword.trim();
        byte[] bytes = keyword.getBytes(charset);

        TreeNode tempNode = rootNode;
        //循环每个字节
        for (int i = 0; i < bytes.length; i++) {
            int index = bytes[i] & 0xff; //字符转换成数字
            TreeNode node = tempNode.getSubNode(index);

            if(node == null){ //没初始化
                node = new TreeNode();
                tempNode.setSubNode(index, node);
            }
            tempNode = node;
            if(i == bytes.length - 1){
                tempNode.setKeywordEnd(true);    //关键词结束， 设置结束标志
            }
        }
    }

    public List<String> match(String text){
        return match(text, charset);
    }

    public List<String> match(String text,Charset charset){
        return match(text.getBytes(charset));
    }

    /**
     * 搜索关键字
     */
    private List<String> match(byte[] bytes){
        List<String> words = Lists.newArrayList();
        if(bytes == null || bytes.length == 0){
            return words;
        }

        TreeNode tempNode = rootNode;
        int rollback = 0;   //回滚数
        int position = 0; //当前比较的位置

        while (position < bytes.length) {
            int index = bytes[position] & 0xFF;
            keywordBuffer.put(bytes[position]); //写关键词缓存
            tempNode = tempNode.getSubNode(index);
            //当前位置的匹配结束
            if(tempNode == null){
                position = position - rollback; //回退 并测试下一个字节
                rollback = 0;
                tempNode = rootNode;    //状态机复位
                keywordBuffer.clear();  //清空
            }
            else if(tempNode.isKeywordEnd()){  //是结束点 记录关键词
                keywordBuffer.flip();
                String keyword = charset.decode(keywordBuffer).toString();
                logger.debug("Find keyword:" + keyword);
                keywordBuffer.limit(keywordBuffer.capacity());
                words.add(keyword);
                rollback = 1;   //遇到结束点  rollback 置为1
            }else{
                rollback++; //非结束点 回退数加1
            }

            position++;
        }
        return words;
    }

    public String replace(String source,char replaceChar){
        return replace(source,this.charset,replaceChar);
    }

    public String replace(String source,Charset charset ,char replaceChar){
        List<String> words = match(source,charset);
        for(String keyword : words){
            char[] chars = new char[keyword.length()];
            ArrayUtils.fill(chars, replaceChar);
            String replaceWith = new String(chars);
            source = StringUtils.replace(source,keyword,replaceWith);
        }
        return source;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    /**
     * 树节点
     * 每个节点包含一个长度为256的数组
     */
    private class TreeNode {
        private static final int NODE_LEN = 256;

        /**
         * true 关键词的终结 ； false 继续
         */
        private boolean end = false;

        private List<TreeNode> subNodes = new ArrayList<TreeNode>(NODE_LEN);

        public TreeNode(){
            for (int i = 0; i < NODE_LEN; i++) {
                subNodes.add(i, null);
            }
        }

        /**
         * 向指定位置添加节点树
         * @param index
         * @param node
         */
        public void setSubNode(int index, TreeNode node){
            subNodes.set(index, node);
        }

        public TreeNode getSubNode(int index){
            return subNodes.get(index);
        }


        public boolean isKeywordEnd() {
            return end;
        }

        public void setKeywordEnd(boolean end) {
            this.end = end;
        }
    }
}
