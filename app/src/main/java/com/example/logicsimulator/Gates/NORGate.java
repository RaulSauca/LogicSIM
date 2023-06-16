//package com.example.logicsimulator.Gates;
//
//public class NORGate implements LogicGate{
//    private Boolean inputA;
//    private Boolean inputB;
//    private Boolean output;
//
//    public NORGate(){
//        this.inputA = Boolean.FALSE;
//        this.inputB = Boolean.FALSE;
//        this.output = Boolean.TRUE;
//    }
//
//    public NORGate(Boolean inputA, Boolean inputB){
//        this.inputA = inputA;
//        this.inputB = inputB;
//        this.output = output(inputA,inputB);
//    }
//
//    @Override
//    public Boolean output(Boolean A, Boolean B) {
//        output = !(A || B);
//        return output;
//    }
//
//    public Boolean getInputA() {
//        return inputA;
//    }
//
//    public Boolean getInputB() {
//        return inputB;
//    }
//
//    public Boolean getOutput() {
//        return output;
//    }
//
//    public void setInputA(Boolean inputA) {
//        this.inputA = inputA;
//    }
//
//    public void setInputB(Boolean inputB) {
//        this.inputB = inputB;
//    }
//
//    public void setOutput(Boolean output) {
//        this.output = output;
//    }
//}
