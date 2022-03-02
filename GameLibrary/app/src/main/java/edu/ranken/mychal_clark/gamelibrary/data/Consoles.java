package edu.ranken.mychal_clark.gamelibrary.data;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;

public class Consoles {

    @DocumentId
    public String id;
    public String name;
    public String icon;

    public Consoles() {}

@NonNull
@Override
public String toString() {
    return "Console{" + id + ", " + name + "}";
}
}
