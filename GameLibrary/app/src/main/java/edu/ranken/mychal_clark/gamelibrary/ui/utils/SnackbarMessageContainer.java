package edu.ranken.mychal_clark.gamelibrary.ui.utils;

import androidx.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

public class SnackbarMessageContainer {

    private List<Integer> messages;

    public SnackbarMessageContainer(){
        messages = new ArrayList<>();
    }

    public void addMessage(@StringRes int messageId){
        messages.add(messageId);
    }
}
