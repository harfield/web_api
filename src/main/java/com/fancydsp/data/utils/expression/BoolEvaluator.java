package com.fancydsp.data.utils.expression;


import java.util.Arrays;

public class BoolEvaluator  extends Evaluator<Boolean>{

    public BoolEvaluator(){

        super(new ExpressionComparator<Boolean> (){

            @Override
            public Boolean getValue(String op, Boolean oprand1, Boolean oprand2) {
                if("and".equals(op)){
                    return oprand1 && oprand2;
                }else if("or".equals(op)){
                    return oprand1 || oprand2;
                }
                return false;
            }

            @Override
            public Boolean getValue(String oprand) {
                return Boolean.parseBoolean(oprand);
            }
        }, Arrays.asList("and","or"));

    }

    public Boolean cal(String expression){
        return super.cal(super.parseTokens(expression));
    }


    public static void main(String[] args) {
        BoolEvaluator boolEvaluator = new BoolEvaluator();
        System.out.println(boolEvaluator.cal("true"));
        System.out.println(boolEvaluator.cal("false"));
        System.out.println(boolEvaluator.cal("true and false or true "));
        System.out.println(boolEvaluator.cal("true and (false or true) and (true or false) "));
        System.out.println(boolEvaluator.cal("(true and (false or true)) and (true or false) "));
        System.out.println(boolEvaluator.cal("(true and (false or true)) and (true or false)  "));

    }

}


