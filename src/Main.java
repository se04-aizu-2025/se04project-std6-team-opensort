package com.opensort;
import com.opensort.controller.Controller;
import com.opensort.controller.IController;
import com.opensort.sorting.*;
import com.opensort.view.IView;
import com.opensort.view.ConsoleView;

class Main{
    public static void main(String[] args){
        IView view = new ConsoleView();
        IController controller = new Controller(view);
        new Thread(controller).start();
        new Thread(view).start();
    }
}