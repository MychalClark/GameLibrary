package edu.ranken.mychal_clark.gamelibrary.data;

import com.google.firebase.firestore.DocumentId;

public class Consoles {

    @DocumentId
    public String id;
    public String name;
    public String icon;
}

@Override
public String toString() {
    return "Console{" id + ", " + name"}";
}
}
