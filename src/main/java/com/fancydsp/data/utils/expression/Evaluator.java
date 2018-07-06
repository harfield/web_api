package com.fancydsp.data.utils.expression;

import java.util.*;

public abstract class Evaluator <T>{
    ExpressionComparator<T> comparator;
    Set<String> op ;
    public Evaluator(ExpressionComparator c,List<String> ops){
        comparator = c;
        op = new HashSet<String>();
        op.addAll(ops);
    }

    protected  T cal(List<Node> tokens){
        T root = null;
        int leftBrace = 0;
        int rightBrace = 0;
        if(tokens.size() ==1 && tokens.get(0).type == 0){
            return comparator.getValue(tokens.get(0).v);
        }
        //()
        if(tokens.get(0).type==2 && tokens.get(tokens.size()-1).type == 3
                && tokens.get(0).v .equals(tokens.get(tokens.size() - 1).v)){
            tokens = tokens.subList(1,tokens.size()-1);
        }
        for(int i = 0 ;i< tokens.size();i++){
            Node n = tokens.get(i);
            if(n.type==2) leftBrace ++;
            else if(n.type==3)rightBrace ++;
            else if(n.type == 1){
                if(leftBrace == rightBrace){
                   root = comparator.getValue(n.v,cal(tokens.subList(0,i)),cal(tokens.subList(i+1,tokens.size())));
                   break;
                }

            }
        }
        return root;
    }
    public  List<Node> parseTokens(String expression){
        String token = "";
        int brace = 0;
        Stack<Integer> braceStack = new Stack<Integer>();

        List<Node> tokens = new ArrayList<Node>();
        for(int i =0 ;i< expression.length();i++){
            char c = expression.charAt(i);
            if(c == ' '){
                if(!token.isEmpty()){
                    if(op.contains(token)){
                        tokens.add(new Node(token,1));
                    }else{
                        tokens.add(new Node(token,0));
                    }
                    token = "";
                }
            }else if( c == '(' ||  c ==')'){
                if(!token.isEmpty()){
                    if(op.contains(token)){
                        tokens.add(new Node(token,1));
                    }else{
                        tokens.add(new Node(token,0));
                    }
                    token = "";
                }
                if(c == '('){
                    tokens.add(new Node("" + brace  ,2));
                    braceStack.push(brace++);

                }else{
                    tokens.add(new Node(braceStack.pop() + "" ,3));

                }
            }else{
                token += expression.charAt(i);
            }
        }

        if(!token.isEmpty()){
            tokens.add(new Node(token,0));
        }

        return  tokens;
    }


    public interface ExpressionComparator <T> {
        T getValue(String op,T oprand1,T oprand2 );
        T getValue(String oprand);
    }

    static class Node{
        String v;
        int type = 0; // a and ( )
        Node left;
        Node right;

        public String getV() {
            return v;
        }

        public void setV(String v) {
            this.v = v;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public Node getLeft() {
            return left;
        }

        public void setLeft(Node left) {
            this.left = left;
        }

        public Node getRight() {
            return right;
        }

        public void setRight(Node right) {
            this.right = right;
        }

        public Node(String v, int type) {
            this.v = v;
            this.type = type;
        }


        @Override
        public String toString() {
            return "Node{" +
                    "v=" + v +
                    ", type=" + type +
                    ", left=" + left +
                    ", right=" + right +
                    '}';
        }
    }
}
