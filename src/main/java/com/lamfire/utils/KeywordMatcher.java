package com.lamfire.utils;

import com.lamfire.logger.Logger;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * ����DFA�ؼ���ƥ���㷨
 * User: lamfire
 * Date: 15-1-19
 * Time: ����10:06
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
        //ѭ��ÿ���ֽ�
        for (int i = 0; i < bytes.length; i++) {
            int index = bytes[i] & 0xff; //�ַ�ת��������
            TreeNode node = tempNode.getSubNode(index);

            if(node == null){ //û��ʼ��
                node = new TreeNode();
                tempNode.setSubNode(index, node);
            }
            tempNode = node;
            if(i == bytes.length - 1){
                tempNode.setKeywordEnd(true);    //�ؼ��ʽ����� ���ý�����־
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
     * �����ؼ���
     */
    private List<String> match(byte[] bytes){
        List<String> words = Lists.newArrayList();
        if(bytes == null || bytes.length == 0){
            return words;
        }

        TreeNode tempNode = rootNode;
        int rollback = 0;   //�ع���
        int position = 0; //��ǰ�Ƚϵ�λ��

        while (position < bytes.length) {
            int index = bytes[position] & 0xFF;
            keywordBuffer.put(bytes[position]); //д�ؼ��ʻ���
            tempNode = tempNode.getSubNode(index);
            //��ǰλ�õ�ƥ�����
            if(tempNode == null){
                position = position - rollback; //���� ��������һ���ֽ�
                rollback = 0;
                tempNode = rootNode;    //״̬����λ
                keywordBuffer.clear();  //���
            }
            else if(tempNode.isKeywordEnd()){  //�ǽ����� ��¼�ؼ���
                keywordBuffer.flip();
                String keyword = charset.decode(keywordBuffer).toString();
                logger.debug("Find keyword:" + keyword);
                keywordBuffer.limit(keywordBuffer.capacity());
                words.add(keyword);
                rollback = 1;   //����������  rollback ��Ϊ1
            }else{
                rollback++; //�ǽ����� ��������1
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
     * ���ڵ�
     * ÿ���ڵ����һ������Ϊ256������
     */
    private class TreeNode {
        private static final int NODE_LEN = 256;

        /**
         * true �ؼ��ʵ��ս� �� false ����
         */
        private boolean end = false;

        private List<TreeNode> subNodes = new ArrayList<TreeNode>(NODE_LEN);

        public TreeNode(){
            for (int i = 0; i < NODE_LEN; i++) {
                subNodes.add(i, null);
            }
        }

        /**
         * ��ָ��λ����ӽڵ���
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
