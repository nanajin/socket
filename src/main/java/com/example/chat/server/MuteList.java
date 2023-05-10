package com.example.chat.server;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;

//public class MuteList implements ListChangeListener<String>{
////    public static void main(String[] args) {
////        ObservableList<String> muteList = FXCollections.observableArrayList();
////        muteList.addListener((Observable ob)->{
////            System.out.println("MuteList의 ob");
////        });
////        muteList.addListener((ListChangeListener.Change<? extends String> change) -> {
////            System.out.println("\tlist = " + change.getList());
////        });
//
//    @Override
//    public void onChanged(Change<? extends String> c) {
//        System.out.println("Mutelist: "+c.getList());
//    }
//}

public class MuteList {
    public ObservableList<String> muteList;
    private String mute_nn;
    void setMute_nn(String name){
        this.mute_nn = name;
    }
    String getMute_nn(){
        return mute_nn;
    }
//    public ObservableList<String> muteList;
//    public void obList(String name){
//        muteList = FXCollections.observableArrayList();
//        muteList.add(name);
//        for(Object i:muteList){
//            System.out.println("mutelist에서 "+i);
//        }
//    }

    //    private ArrayList<String> muteList;
    String nickname;
    void setMuteList(String nickname){
        System.out.println("1: "+nickname);
        this.nickname = nickname;
        muteList.add("1");
        muteList.add("2");
        muteList.add(nickname);
        System.out.println("2: "+nickname);
        for(String i: muteList){
            System.out.println("MuteList.java에서: "+ i);
        }
    }
    public ObservableList<String> getMuteList(){

        ObservableList<String> ObList = FXCollections.observableList(muteList);
//        for(String i: muteList){
//            System.out.println("MuteList getlist에서: "+ i);
//        }
        return ObList;
    }
}
